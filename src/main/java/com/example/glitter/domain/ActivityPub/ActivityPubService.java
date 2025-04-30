package com.example.glitter.domain.ActivityPub;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Post.PostResponseDto;
import com.example.glitter.domain.Post.PostService;
import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserResponse;

/**
 * ActivityPubService の実装
 */
@Service
public class ActivityPubService {
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
   * ユーザー ID から ActivityPub Actor オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Actorオブジェクト
   */
  public Optional<Actor> getActorObject(String userId) {
    return userService.findById(userId).map(user -> createActorFromUser(user));
  }

  /**
   * 投稿 ID から ActivityPub Note オブジェクトを取得する
   * 
   * @param postId
   * @return
   */
  public Optional<Note> getNoteObject(Long postId) {
    return postService.findById(postId).map(post -> {
      return createNoteFromPost(post);
    });
  }

  /**
   * 投稿 ID から ActivityPub Activity オブジェクトを取得する
   * TODO: これは仮置きです
   * 
   * @param postId
   * @return
   */
  public Optional<Activity> getActivityFromPost(Long postId) {
    return postService.findById(postId).map(post -> {
      Note note = createNoteFromPost(post);
      Activity activity = createActivityFromNote(post.getUser(), note);
      return activity;
    });
  }

  /**
   * ユーザー ID から ActivityPub Outbox オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Outboxオブジェクト
   */
  public Optional<OrderedCollection> getOutboxObject(String userId) {
    return userService.findById(userId).map(user -> {
      List<PostResponseDto> posts = postService.getPostsByUserId(userId);
      List<Note> notes = posts.stream()
          .map(post -> createNoteFromPost(post))
          .toList();
      return createOutboxFromPosts(user, notes);
    });
  }

  /**
   * ユーザー情報から ActivityPub Actor オブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @return Actorオブジェクト
   */
  private Actor createActorFromUser(UserResponse user) {
    // Actorオブジェクトを構築
    Actor.ActorBuilder builder = Actor.builder()
        .id(apiUrl + "/user/" + user.getId())
        .preferredUsername(user.getId())
        .inbox(apiUrl + "/user/" + user.getId() + "/inbox")
        .outbox(apiUrl + "/user/" + user.getId() + "/outbox")
        .name(user.getUsername());

    // プロフィール情報があれば追加
    if (user.getProfile() != null && !user.getProfile().isEmpty()) {
      builder.summary(user.getProfile());
    }

    // アイコン情報があれば追加
    if (user.getIcon() != null && !user.getIcon().isEmpty()) {
      builder.icon(new String[] { storageUrl + "/" + user.getIcon() });
    }

    return builder.build();
  }

  /**
   * 投稿から ActivityPub Note オブジェクトを生成する
   * 
   * @param post 投稿情報
   * @return Noteオブジェクト
   */
  private Note createNoteFromPost(PostResponseDto post) {
    // ISO-8601 形式の日時文字列に変換
    String published = post.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    UserResponse user = post.getUser();

    String actorUrl = apiUrl + "/user/" + user.getId();
    // TODO: 現状、投稿IDはユーザーと紐付かない一意な値である。後々 user/{id}/post/{postId} にしたほうがよいかも
    String noteUrl = apiUrl + "/post/" + post.getId();

    return Note.builder()
        .id(noteUrl)
        .content(post.getContent())
        .published(published)
        .attributedTo(actorUrl)
        .build();
  }

  /**
   * 投稿のリストから ActivityPub OrderedCollection オブジェクトを生成する
   * Outbox 向けに利用します。
   * 
   * @param user  ユーザー情報
   * @param notes ノートのリスト
   * @return OrderedCollectionオブジェクト
   */
  private OrderedCollection createOutboxFromPosts(UserResponse user, List<Note> notes) {
    String outboxUrl = apiUrl + "/user/" + user.getId() + "/outbox";

    // OrderedCollection オブジェクトを作成
    OrderedCollection.OrderedCollectionBuilder<?, ?> builder = OrderedCollection.builder()
        .id(outboxUrl)
        .totalItems(notes.size());

    // Note を Activity に変換
    if (!notes.isEmpty()) {
      List<Activity> activities = new ArrayList<>();
      notes.forEach(note -> {
        Activity activity = createActivityFromNote(user, note);
        activities.add(activity);
      });
      builder.orderedItems(new ArrayList<ActivityPubObject>(activities));
    }

    return builder.build();
  }

  /**
   * ノートから ActivityPub Activity オブジェクトを生成する
   * 
   * @param user ユーザー情報
   * @param note ノート
   * @return Activityオブジェクト
   */
  private Activity createActivityFromNote(UserResponse user, Note note) {
    String actorUrl = apiUrl + "/user/" + user.getId();
    // TODO: 雑
    String postId = note.getId().substring(note.getId().lastIndexOf("/") + 1);
    String activityUrl = apiUrl + "/activity/" + postId;
    String published = note.getPublished();

    return Activity.builder()
        .id(activityUrl)
        .type("Create")
        .actor(actorUrl)
        .object(note)
        .published(published)
        .build();
  }
}
