package com.example.glitter.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
   * アクターエンドポイントに問い合わせてアクター情報を取得する
   * 
   * @param actorUrl
   * @return JsonNode
   */
  public Actor getActorFromUrl(String actorUrl) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.parseMediaType("application/activity+json")));
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<JsonNode> response = restTemplate.exchange(
        actorUrl,
        HttpMethod.GET,
        request,
        JsonNode.class);

    JsonNode actorNode = response.getBody();
    if (actorNode == null) {
      throw new RuntimeException("アクター情報の取得に失敗しました");
    }

    // icon は直で URL が入っている場合 (Glitter) と、type と url に分かれている (外部サービス) 場合がある
    // とりあえずここで確実に取得できるようにしている
    String iconUrl;
    JsonNode iconNode = actorNode.get("icon");
    if (iconNode == null) {
      iconUrl = "";
    } else {
      if (iconNode.has("url")) {
        iconUrl = iconNode.get("url").asText();
      } else {
        iconUrl = iconNode.asText();
      }
    }

    // 取得した JSON を Actor に詰め替えて返す
    Actor actor = Actor.builder()
        .id(actorNode.get("id").asText())
        .preferredUsername(actorNode.get("preferredUsername").asText())
        .name(actorNode.get("name").asText())
        .summary(actorNode.has("summary") ? actorNode.get("summary").asText() : "")
        .inbox(actorNode.get("inbox").asText())
        .outbox(actorNode.get("outbox").asText())
        .icon(Actor.Icon.builder()
            .type("Image")
            .url(iconUrl)
            .build())
        .build();
    return actor;
  }

  /**
   * フォローリクエストを承認する
   * 
   * @param followeeId
   * @param follow
   */
  public void acceptFollowRequest(String followeeId, ActivityPubFollow follow) throws Exception {
    Accept accept = Accept.builder()
        .id(apiUrl + "/activity/" + UUID.randomUUID())
        .actor(apiUrl + "/user/" + followeeId)
        .object(follow)
        .build();

    String followerActorUrl = follow.getActor();
    Actor followerActor = getActorFromUrl(followerActorUrl);
    String followerInboxUrl = followerActor.getInbox();

    // 署名されたリクエストを作成
    HttpEntity<String> entity;
    try {
      entity = activityPubSignatureService.createSignedHttpEntity(
          objectMapper.valueToTree(accept),
          followeeId,
          followerInboxUrl);
    } catch (Exception e) {
      throw new RuntimeException("Accept HTTP Signature の作成に失敗しました", e);
    }

    // リクエストを送信
    ResponseEntity<JsonNode> response = restTemplate.postForEntity(followerInboxUrl, entity, JsonNode.class);
    logger.info("Status Code: {}", response.getStatusCode());

    // Mastodon も Misskey も成功時は 202 ACCEPTED を返している
    if (response.getStatusCode().toString().equals("202 ACCEPTED")) {
      try {
        saveExternalFollow(followerActor, followeeId);
      } catch (Exception e) {
        throw new RuntimeException("外部フォローの情報保存に失敗しました", e);
      }
    }
  }

  /**
   * 外部ユーザーの情報を保存する
   * Accept の成功を確認してから実行してください
   * 
   * @param followerActor
   * @param followeeId
   * @throws Exception
   */
  private void saveExternalFollow(Actor followerActor, String followeeId) throws Exception {
    // 外部ユーザーの情報を保存
    UserDto user = UserDto.builder()
        .userId(followerActor.getPreferredUsername())
        .domain(URI.create(followerActor.getId()).getHost())
        .actorUrl(followerActor.getId())
        .username(followerActor.getName())
        .profile(followerActor.getSummary())
        .icon(followerActor.getIcon().getUrl())
        .build();
    User resultUser = userRepository.insert(user.toEntity());
    logger.info("Inserted user: {}", objectMapper.writeValueAsString(resultUser));

    // フォロー関係も保存
    FollowDto followDto = FollowDto.builder()
        .followerId(user.getUserId())
        .followerDomain(user.getDomain())
        .followeeId(followeeId)
        .followeeDomain(domain)
        .build();
    Follow resultFollow = followRepository.insert(followDto.toEntity());
    logger.info("Inserted follow: {}", objectMapper.writeValueAsString(resultFollow));
  }
}
