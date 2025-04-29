package com.example.glitter.domain.ActivityPub;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Post.PostResponseDto;
import com.example.glitter.domain.Post.PostService;
import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;

/**
 * ActivityPubServiceの実装クラス
 */
@Service
public class ActivityPubServiceImpl implements ActivityPubService {

  @Autowired
  private UserService userService;
  
  @Autowired
  private PostService postService;

  @Value("${env.api-url}")
  private String apiUrl;

  @Value("${env.storage-url}")
  private String storageUrl;

  @Value("${env.storage-bucket-name}")
  private String bucketName;

  /**
   * ユーザーIDから ActivityPub Actor オブジェクトを生成する
   * 
   * @param userId ユーザーID
   * @return Actorオブジェクト
   */
  @Override
  public Optional<Actor> getActorObject(String userId) {
    return userService.findById(userId).map(user -> createActorFromUser(user));
  }

  /**
   * ユーザーIDから ActivityPub Outbox オブジェクトを生成する
   * 
   * @param userId ユーザーID
   * @return Outboxオブジェクト
   */
  @Override
  public Optional<OrderedCollection> getOutboxObject(String userId) {
    return userService.findById(userId).map(user -> {
      List<PostResponseDto> posts = postService.getPostsByUserId(userId);
      return createOutboxFromPosts(user, posts);
    });
  }

  /**
   * UserSummaryDtoからActivityPub Actorオブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @return Actorオブジェクト
   */
  private Actor createActorFromUser(UserSummaryDto user) {
    // Actorオブジェクトを構築
    Actor.ActorBuilder builder = Actor.builder()
        .context(Arrays.asList("https://www.w3.org/ns/activitystreams", 
            "https://w3id.org/security/v1"))
        .id(apiUrl + "/user/" + user.getId())
        .type("Person")
        .preferredUsername(user.getId())
        .inbox(apiUrl + "/user/" + user.getId() + "/inbox")
        .outbox(apiUrl + "/user/" + user.getId() + "/outbox")
        .name(user.getUsername())
        .discoverable(true);

    // プロフィール情報があれば追加
    if (user.getProfile() != null && !user.getProfile().isEmpty()) {
      builder.summary(user.getProfile());
    }

    // アイコン情報があれば追加
    if (user.getIcon() != null && !user.getIcon().isEmpty()) {
      builder.icon(new String[]{storageUrl + "/" + user.getIcon()});
    }

    return builder.build();
  }

  /**
   * 投稿リストから ActivityPub OrderedCollection オブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @param posts 投稿リスト
   * @return OrderedCollectionオブジェクト
   */
  private OrderedCollection createOutboxFromPosts(UserSummaryDto user, List<PostResponseDto> posts) {
    String outboxUrl = apiUrl + "/user/" + user.getId() + "/outbox";
    
    // OrderedCollection オブジェクトを作成
    OrderedCollection.OrderedCollectionBuilder builder = OrderedCollection.builder()
        .context(Arrays.asList("https://www.w3.org/ns/activitystreams"))
        .id(outboxUrl)
        .type("OrderedCollection")
        .totalItems(posts.size())
        // TODO: ページネーションは実装していないため、outboxUrl と同等とする
        .first(outboxUrl);
    
    // Post を ActivityPub Activity オブジェクトに変換
    if (!posts.isEmpty()) {
      List<Activity> activities = new ArrayList<>();
      
      for (PostResponseDto post : posts) {
        Activity activity = createActivityFromPost(user, post);
        activities.add(activity);
      }
      
      builder.orderedItems(new ArrayList<>(activities));
    }
    
    return builder.build();
  }

  /**
   * 投稿から ActivityPub Activity オブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @param post 投稿情報
   * @return Activityオブジェクト
   */
  private Activity createActivityFromPost(UserSummaryDto user, PostResponseDto post) {
    // ISO-8601 形式の日時文字列に変換
    String published = post.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    
    Actor actor = createActorFromUser(user);
    String activityUrl = apiUrl + "/user/" + user.getId() + "/activity/" + post.getId();
    String noteUrl = apiUrl + "/user/" + user.getId() + "/post/" + post.getId();
    
    Note note = Note.builder()
        .id(noteUrl)
        .type("Note")
        .content(post.getContent())
        .published(published)
        .attributedTo(actor.getId())
        .to(new String[]{"https://www.w3.org/ns/activitystreams#Public"})
        .build();
    
    return Activity.builder()
        .id(activityUrl)
        .type("Create")
        .actor(actor)
        .object(note)
        .published(published)
        .to(new String[]{"https://www.w3.org/ns/activitystreams#Public"})
        .build();
  }
}
