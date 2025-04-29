package com.example.glitter.domain.ActivityPub;

import java.util.Optional;

/**
 * ActivityPub の Actor オブジェクトなどを生成するサービス
 */
public interface ActivityPubService {
  /**
   * ユーザーIDからActivityPub Actor オブジェクトを生成する
   * 
   * @param userId ユーザーID
   * @return Actorオブジェクト
   */
  Optional<Actor> getActorObject(String userId);

  /**
   * ユーザーIDからActivityPub Outbox オブジェクトを生成する
   * 
   * @param userId ユーザーID
   * @return Outboxオブジェクト
   */
  Optional<OrderedCollection> getOutboxObject(String userId);
}
