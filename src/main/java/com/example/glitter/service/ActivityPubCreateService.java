package com.example.glitter.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

/**
 * Activity 関連オブジェクトを生成するサービス
 */
@Service
public class ActivityPubCreateService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;

  @Value("${env.api-url}")
  private String apiUrl;
  @Value("${env.domain}")
  private String domain;
  @Value("${env.storage-url}")
  private String storageUrl;
  @Value("${env.public-key-path}")
  private String publicKeyPath;

  /**
   * ユーザー ID から ActivityPub Actor オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Actor オブジェクト
   */
  public Optional<Actor> getActorObject(String userId) {
    return userRepository.findByUserIdAndDomain(userId, domain).map(user -> createActorFromUser(user));
  }

  /**
   * 投稿 ID から ActivityPub Note オブジェクトを取得する
   * 
   * @param postId
   * @return Note オブジェクト
   */
  public Optional<Note> getNoteObject(String postId) {
    return postRepository.findByUuid(postId).map(post -> {
      return createNoteFromPost(post);
    });
  }

  /**
   * ユーザー ID から ActivityPub Outbox オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Outbox オブジェクト
   */
  public Optional<OrderedCollection> getOutboxObject(String userId) {
    return userRepository.findByUserIdAndDomain(userId, domain).map(user -> {
      List<Post> posts = postRepository.findPostsByUserIdAndDomain(userId, domain);
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
    // 公開鍵を取得
    String publicKeyPem = "";
    try {
      publicKeyPem = new String(Files.readAllBytes(Paths.get(publicKeyPath)),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("公開鍵の読み込みに失敗しました", e);
    }
    String url = apiUrl + "/user/" + user.getUserId();
    // Actor オブジェクトを構築
    Actor.ActorBuilder builder = Actor.builder()
        .id(url)
        .preferredUsername(user.getUserId())
        .inbox(url + "/inbox")
        .outbox(url + "/outbox")
        .name(user.getUsername())
        .publicKey(
            Actor.PublicKey.builder()
                .id(url + "#main-key")
                .owner(url)
                .publicKeyPem(publicKeyPem)
                .build());

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
    String noteUrl = apiUrl + "/post/" + post.getUuid();

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
    String outboxUrl = apiUrl + "/user/" + user.getUserId() + "/outbox";

    // OrderedCollection オブジェクトを作成
    OrderedCollection.OrderedCollectionBuilder<?, ?> builder = OrderedCollection.builder()
        .id(outboxUrl)
        .totalItems(notes.size())
        // TODO: outbox を真面目に参照している ActivityPub 実装は少ないようなので、今は totalItems だけ返しておく
        .orderedItems(Collections.emptyList());

    return builder.build();
  }
}
