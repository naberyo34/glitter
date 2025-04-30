package com.example.glitter.service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.ActivityPub.Activity;
import com.example.glitter.domain.ActivityPub.ActivityPubObject;
import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

/**
 * ActivityPubService の実装
 */
@Service
public class ActivityPubService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;

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
   * @return Actor オブジェクト
   */
  public Optional<Actor> getActorObject(String userId) {
    return userRepository.findById(userId).map(user -> createActorFromUser(user));
  }

  /**
   * 投稿 ID から ActivityPub Note オブジェクトを取得する
   * 
   * @param postId
   * @return Note オブジェクト
   */
  public Optional<Note> getNoteObject(Long postId) {
    return postRepository.findById(postId).map(post -> {
      return createNoteFromPost(post);
    });
  }

  /**
   * 投稿 ID から ActivityPub Activity オブジェクトを取得する
   * 
   * @param postId
   * @return
   */
  public Optional<Activity> getActivityFromPost(Long postId) {
    return postRepository.findById(postId).map(post -> {
      User user = userRepository.findById(post.getUserId()).orElseThrow();
      Note note = createNoteFromPost(post);
      Activity activity = createActivityFromNote(user, note);
      return activity;
    });
  }

  /**
   * ユーザー ID から ActivityPub Outbox オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Outbox オブジェクト
   */
  public Optional<OrderedCollection> getOutboxObject(String userId) {
    return userRepository.findById(userId).map(user -> {
      List<Post> posts = postRepository.findPostsByUserId(userId);
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
   * @return Actor オブジェクト
   */
  private Actor createActorFromUser(User user) {
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
   * @return Note オブジェクト
   */
  private Note createNoteFromPost(Post post) {
    // ISO-8601 形式の日時文字列に変換
    String published = post.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String userId = post.getUserId();
    String actorUrl = apiUrl + "/user/" + userId;
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
   * @return OrderedCollection オブジェクト
   */
  private OrderedCollection createOutboxFromPosts(User user, List<Note> notes) {
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
  private Activity createActivityFromNote(User user, Note note) {
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
