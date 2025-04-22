package com.example.glitter.domain.ActivityPub;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;

/**
 * ActivityPubServiceの実装クラス
 */
@Service
public class ActivityPubServiceImpl implements ActivityPubService {

  @Autowired
  private UserService userService;

  @Value("${env.api-url}")
  private String apiUrl;

  @Value("${env.storage-url}")
  private String storageUrl;

  @Value("${env.storage-bucket-name}")
  private String bucketName;

  /**
   * ユーザーIDからActivityPub Actor オブジェクトを生成する
   * 
   * @param userId ユーザーID
   * @return Actorオブジェクト
   */
  @Override
  public Optional<Actor> getActorObject(String userId) {
    return userService.findById(userId).map(user -> createActorFromUser(user));
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
}
