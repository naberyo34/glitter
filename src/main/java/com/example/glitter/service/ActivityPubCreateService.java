package com.example.glitter.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Activity 関連オブジェクトを生成するサービス
 */
@Service
public class ActivityPubCreateService {
  @Autowired
  private FollowListService followListService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private RestTemplate restTemplate;

  @Value("${env.api-url}")
  private String apiUrl;
  @Value("${env.domain}")
  private String domain;
  @Value("${env.storage-url}")
  private String storageUrl;
  @Value("${env.public-key-path}")
  private String publicKeyPath;

  /**
   * アクターエンドポイントに問い合わせて ActivityPub Actor オブジェクトを取得する
   * 
   * @param actorUrl
   * @return Actor オブジェクト
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
   * 内部ユーザー ID から ActivityPub Actor オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Actor オブジェクト
   */
  public Optional<Actor> getActorFromUserId(String userId) {
    return userRepository.findByUserIdAndDomain(userId, domain).map(user -> createActorFromUser(user));
  }

  /**
   * 投稿 ID から ActivityPub Note オブジェクトを取得する
   * 
   * @param postId
   * @return Note オブジェクト
   */
  public Optional<Note> getNoteFromPostId(String postId) {
    return postRepository.findByUuid(postId).map(post -> {
      return createNoteFromPost(post);
    });
  }

  /**
   * 投稿 DTO から ActivityPub Note オブジェクトを取得する
   * 
   * @param post 投稿情報
   * @return Note オブジェクト
   */
  public Note getNoteFromPost(PostDto post) {
    return createNoteFromPost(post.toEntity());
  }

  /**
   * ユーザー ID から ActivityPub Followers OrderedCollection オブジェクトを取得する
   * 
   * @param userId
   * @return Followers オブジェクトの OrderedCollection
   */
  public Optional<OrderedCollection> getFollowersFromUserId(String userId) {
    return userRepository.findByUserIdAndDomain(userId, domain).map(_ -> {
      List<UserDto> followers = followListService.getFollowers(userId);
      List<Actor> actors = followers.stream()
          .map(follower -> createActorFromUser(follower.toEntity()))
          .toList();
      return createFollowersOrderedCollection(userId, actors);
    });
  }

  /**
   * ユーザー ID から ActivityPub Note OrderedCollection オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Note オブジェクトの OrderedCollection
   */
  public Optional<OrderedCollection> getNotesFromUserId(String userId) {
    return userRepository.findByUserIdAndDomain(userId, domain).map(_ -> {
      List<Post> posts = postRepository.findPostsByUserIdAndDomain(userId, domain);
      List<Note> notes = posts.stream()
          .map(post -> createNoteFromPost(post))
          .toList();
      return createNotesOrderedCollection(userId, notes);
    });
  }

  /**
   * ユーザー情報から ActivityPub Actor オブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @return Actor オブジェクト
   */
  private Actor createActorFromUser(User user) {
    // 公開鍵を取得
    String publicKeyPem = "";
    try {
      publicKeyPem = new String(Files.readAllBytes(Paths.get(publicKeyPath)),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("公開鍵の読み込みに失敗しました", e);
    }
    String actorUrl = user.getActorUrl();
    // Actor オブジェクトを構築
    Actor.ActorBuilder builder = Actor.builder()
        .id(actorUrl)
        .preferredUsername(user.getUserId())
        .inbox(actorUrl + "/inbox")
        .outbox(actorUrl + "/outbox")
        .name(user.getUsername())
        .publicKey(
            Actor.PublicKey.builder()
                .id(actorUrl + "#main-key")
                .owner(actorUrl)
                .publicKeyPem(publicKeyPem)
                .build());

    // プロフィール情報があれば追加
    if (user.getProfile() != null && !user.getProfile().isEmpty()) {
      builder.summary(user.getProfile());
    }

    // アイコン情報があれば追加
    if (user.getIcon() != null && !user.getIcon().isEmpty()) {
      builder.icon(Actor.Icon.builder()
          .type("Image")
          .url(storageUrl + "/" + user.getIcon())
          .build());
    }

    return builder.build();
  }

  /**
   * 投稿から ActivityPub Note オブジェクトを生成する
   * 
   * @param post 投稿情報
   * @return Note オブジェクト
   */
  private Note createNoteFromPost(Post post) {
    // ISO-8601 形式の日時文字列に変換
    String published = post.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String userId = post.getUserId();
    String noteUrl = apiUrl + "/post/" + post.getUuid();
    String actorUrl = apiUrl + "/user/" + userId;
    String ccUrl = apiUrl + "/user/" + userId + "/followers";

    return Note.builder()
        .id(noteUrl)
        .content(post.getContent())
        .published(published)
        .attributedTo(actorUrl)
        .cc(ccUrl)
        .build();
  }

  /**
   * フォロワーのリストから ActivityPub OrderedCollection オブジェクトを生成する
   * 
   * @param userId ユーザー ID
   * @param actors フォロワーのリスト
   * @return OrderedCollection オブジェクト
   */
  private OrderedCollection createFollowersOrderedCollection(String userId, List<Actor> actors) {
    String followersUrl = apiUrl + "/user/" + userId + "/followers";

    // OrderedCollection オブジェクトを作成
    // あまり行儀はよくないが とりあえず キャスト
    List<Object> orderedItems = new ArrayList<>(actors);
    OrderedCollection orderedCollection = OrderedCollection.builder()
        .id(followersUrl)
        .totalItems(actors.size())
        .orderedItems(orderedItems)
        .build();

    return orderedCollection;
  }

  /**
   * 投稿のリストから ActivityPub OrderedCollection オブジェクトを生成する
   * 
   * @param userId ユーザー ID
   * @param notes  ノートのリスト
   * @return OrderedCollection オブジェクト
   */
  private OrderedCollection createNotesOrderedCollection(String userId, List<Note> notes) {
    String outboxUrl = apiUrl + "/user/" + userId + "/outbox";

    // OrderedCollection オブジェクトを作成
    // あまり行儀はよくないが とりあえず キャスト
    List<Object> orderedItems = new ArrayList<>(notes);
    OrderedCollection orderedCollection = OrderedCollection.builder()
        .id(outboxUrl)
        .totalItems(notes.size())
        .orderedItems(orderedItems)
        .build();

    return orderedCollection;
  }
}
