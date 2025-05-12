package com.example.glitter.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Create;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.generated.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PostService {
  @Autowired
  private SessionUserService sessionUserService;
  @Autowired
  private ActivityPubCreateService activityPubCreateService;
  @Autowired
  private ActivityPubSignatureService activityPubSignatureService;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Value("${env.api-url}")
  private String apiUrl;
  private Logger logger = LoggerFactory.getLogger(PostService.class);

  /**
   * 投稿する
   * 
   * @param content
   * @return
   * @throws Exception
   */
  public Optional<PostDto> add(String content) throws Exception {
    UserDto me = sessionUserService.getMe();
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
        .id(apiUrl + "/activity/" + UUID.randomUUID())
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
}
