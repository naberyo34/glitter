package com.example.glitter.service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.glitter.domain.ActivityPub.Accept;
import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Glitter 外部の ActivityPub サーバー間でフォロー処理を行うクラス
 */
@Service
@Transactional
public class ExternalFollowService {
  @Value("${env.api-url}")
  private String apiUrl;
  @Value("${env.domain}")
  private String domain;

  @Autowired
  private ActivityPubCreateService activityPubCreateService;
  @Autowired
  private ActivityPubSignatureService activityPubSignatureService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  private Logger logger = LoggerFactory.getLogger(ExternalFollowService.class);

  @Value("${env.private-key-path}")
  private String privateKeyPath;

  /**
   * フォローリクエストを承認する
   * 
   * @param followeeId
   * @param follow
   */
  public void acceptRequest(String followeeId, ActivityPubFollow follow) throws Exception {
    Accept accept = Accept.builder()
        .id(apiUrl + "/activity/" + UUID.randomUUID())
        .actor(apiUrl + "/user/" + followeeId)
        .object(follow)
        .build();

    String followerActorUrl = follow.getActor();
    Actor followerActor = activityPubCreateService.getActorFromUrl(followerActorUrl);
    String followerInboxUrl = followerActor.getInbox();

    // 署名されたリクエストを作成
    HttpEntity<String> entity;
    try {
      entity = activityPubSignatureService.createSignedHttpEntity(
          objectMapper.valueToTree(accept),
          followeeId,
          followerInboxUrl);
      logger.info("Accept HTTP Signature を作成しました");
    } catch (Exception e) {
      throw new RuntimeException("Accept HTTP Signature の作成に失敗しました", e);
    }

    // リクエストを送信
    ResponseEntity<JsonNode> response = restTemplate.postForEntity(followerInboxUrl, entity, JsonNode.class);
    logger.info("Accept を送信しました: Status Code: {}", response.getStatusCode());

    // Mastodon も Misskey も成功時は 202 ACCEPTED を返している
    if (response.getStatusCode().toString().equals("202 ACCEPTED")) {
      try {
        save(followerActor, followeeId);
        logger.info("外部フォローの情報を保存しました");
      } catch (Exception e) {
        throw new RuntimeException("外部フォローの情報保存に失敗しました", e);
      }
    }
  }

  /**
   * Undo リクエストに応じてフォロー関係を削除する
   * 
   * @param follow
   * @throws Exception
   */
  public void undo(String followeeId, ActivityPubFollow follow) throws Exception {
    String followerActorUrl = follow.getActor();
    Actor followerActor = activityPubCreateService.getActorFromUrl(followerActorUrl);

    // フォロー関係を削除
    followRepository.delete(
        followerActor.getPreferredUsername(),
        URI.create(followerActor.getId()).getHost(),
        followeeId,
        domain);

    // TODO: とりあえず外部ユーザーの情報は残している
  }

  /**
   * 外部ユーザーの情報を保存する
   * Accept の成功を確認してから実行してください
   * 
   * @param followerActor
   * @param followeeId
   * @throws Exception
   */
  private void save(Actor followerActor, String followeeId) throws Exception {
    String followerDomain = URI.create(followerActor.getId()).getHost();
    // 外部ユーザーの情報が存在しない (Glitter アカウントを初めてフォローする外部ユーザーの場合) 情報を保存
    Optional<User> externalUserOpt = userRepository.findByUserIdAndDomain(followerActor.getPreferredUsername(),
        followerDomain);
    if (externalUserOpt.isPresent()) {
      logger.info("フォローアクションを送信した外部ユーザーがすでに存在します: {}", objectMapper.writeValueAsString(externalUserOpt));
    } else {
      UserDto user = UserDto.builder()
          .userId(followerDomain)
          .domain(URI.create(followerActor.getId()).getHost())
          .actorUrl(followerActor.getId())
          .username(followerActor.getName())
          .profile(followerActor.getSummary())
          .icon(followerActor.getIcon().getUrl())
          .build();
      externalUserOpt = Optional.of(userRepository.insert(user.toEntity()));
      logger.info("外部ユーザーを追加しました", objectMapper.writeValueAsString(externalUserOpt));
    }
    User externalUser = externalUserOpt.get();
    // フォロー関係も保存
    FollowDto followDto = FollowDto.builder()
        .followerId(externalUser.getUserId())
        .followerDomain(externalUser.getDomain())
        .followeeId(followeeId)
        .followeeDomain(domain)
        .build();
    Follow resultFollow = followRepository.insert(followDto.toEntity());
    logger.info("フォロー関係を追加しました", objectMapper.writeValueAsString(resultFollow));
  }
}
