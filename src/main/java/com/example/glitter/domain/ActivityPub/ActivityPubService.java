package com.example.glitter.domain.ActivityPub;

import java.util.Optional;

/**
 * ActivityPub の Actor オブジェクトなどを生成するサービス
 */
public interface ActivityPubService {
  /**
   * ユーザー ID から ActivityPub Actor オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Actorオブジェクト
   */
  Optional<Actor> getActorObject(String userId);

  /**
   * 投稿 ID から ActivityPub Note オブジェクトを取得する
   * 
   * @param postId
   * @return
   */
  Optional<Note> getNoteObject(Long postId);

  /**
   * 投稿 ID から ActivityPub Activity オブジェクトを取得する
   * TODO: これは仮置きです
   * 
   * @param postId
   * @return
   */
  Optional<Activity> getActivityFromPost(Long postId);

  /**
   * ユーザー ID から ActivityPub Outbox オブジェクトを取得する
   * 
   * @param userId ユーザーID
   * @return Outboxオブジェクト
   */
  Optional<OrderedCollection> getOutboxObject(String userId);
}
