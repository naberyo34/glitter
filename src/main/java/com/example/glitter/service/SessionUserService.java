package com.example.glitter.service;

import java.util.List;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Create;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class SessionUserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private FollowListService followListService;
  @Autowired
  private ImageWriteService imageWriteService;
  @Autowired
  private ActivityPubCreateService activityPubCreateService;
  @Autowired
  private ActivityPubSignatureService activityPubSignatureService;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  private Logger logger = LoggerFactory.getLogger(SessionUserService.class);

  @Value("${env.domain}")
  private String domain;

  /**
   * 自身のユーザー情報を取得する
   * 
   * @return セッションユーザー
   * @throws NotLoginException セッションユーザーが存在しない場合
   */
  public UserDto getMe() {
    User user = userRepository.getSessionUser().orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    return UserDto.fromEntity(user);
  }

  /**
   * 自身のフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserDto> getFollowing() {
    UserDto me = getMe();
    return followListService.getFollowing(me.getUserId());
  }

  /**
   * 自身のフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserDto> getFollowers() {
    UserDto me = getMe();
    return followListService.getFollowers(me.getUserId());
  }

  /**
   * 投稿する
   * 
   * @param content
   * @return
   * @throws Exception
   */
  public Optional<PostDto> addPost(String content) throws Exception {
    UserDto me = getMe();
    Post post = new Post();
    post.setUserId(me.getUserId());
    post.setDomain(me.getDomain());
    post.setContent(content);
    // 先に投稿を保存する
    Post result = postRepository.insert(post);

    logger.info("Post Object: {}", objectMapper.writeValueAsString(result));

    // 投稿をフォロワーに通知
    Note note = activityPubCreateService.getNoteFromPost(PostDto.fromEntity(result));
    Create create = Create.builder()
        .actor(me.getActorUrl())
        .object(note)
        .cc(me.getActorUrl() + "/followers")
        .build();
    logger.info("Create Object: {}", objectMapper.writeValueAsString(create));
    Optional<OrderedCollection> followers = activityPubCreateService.getFollowersFromUserId(me.getUserId());
    followers.ifPresent(f -> f.getOrderedItems().forEach(follower -> {
      Actor followerActor = (Actor) follower;
      String followerInbox = followerActor.getInbox();
      HttpEntity<String> entity;
      try {
        entity = activityPubSignatureService.createSignedHttpEntity(
            objectMapper.valueToTree(create),
            me.getUserId(),
            followerInbox);
        logger.info("Create HTTP Signature を作成しました: {}", followerInbox);
      } catch (Exception e) {
        throw new RuntimeException("Create HTTP Signature の作成に失敗しました", e);
      }
      ResponseEntity<JsonNode> response = restTemplate.postForEntity(followerInbox, entity, JsonNode.class);
      logger.info("Create を送信しました: Status Code: {}", response.getStatusCode());
    }));

    return Optional.of(PostDto.fromEntity(result));
  }

  /**
   * アイコン画像を更新する
   * 
   * @param file
   * @return userDto
   * @throws Exception
   */
  public UserDto updateIcon(MultipartFile file) throws Exception {
    User me = userRepository.getSessionUser()
        .orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(me.getIcon())) {
      imageWriteService.delete(me.getIcon());
    }
    // ファイル名、パスを作成
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID().toString() + "." + extension;
    String key = me.getUserId() + "/" + fileName;
    // 画像をアップロード
    imageWriteService.upload(file, key);
    // データベース上のアイコン画像のパスを更新
    me.setIcon(key);
    return UserDto.fromEntity(userRepository.update(me));
  }
}
