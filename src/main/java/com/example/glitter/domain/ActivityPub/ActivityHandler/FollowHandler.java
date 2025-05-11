package com.example.glitter.domain.ActivityPub.ActivityHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.example.glitter.service.ExternalFollowService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * type: Follow のアクティビティを処理するクラス
 */
@Component
public class FollowHandler implements ActivityHandler {
  @Autowired
  private ExternalFollowService externalFollowService;

  private Logger logger = LoggerFactory.getLogger(FollowHandler.class);

  @Override
  public String getType() {
    return "Follow";
  }

  @Override
  public void handle(String userId, JsonNode activity) {
    logger.info("Follow アクティビティを受信しました: " + activity.toString());
    // 受け取った JSON を ActivityPubFollow に詰め替える
    ActivityPubFollow follow = ActivityPubFollow.builder()
        .id(activity.get("id").asText())
        .actor(activity.get("actor").asText())
        .object(activity.get("object").asText())
        .build();
    // 今のところは、フォローリクエストを無条件に受け入れる
    try {
      externalFollowService.acceptFollowRequest(userId, follow);
    } catch (Exception e) {
      throw new RuntimeException("フォローリクエストの承認に失敗しました", e);
    }
  }
}
