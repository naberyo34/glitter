package com.example.glitter.domain.ActivityPub.ActivityHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * type: Undo のアクティビティを処理するクラス
 * 現状は Follow の Undo のみに対応
 */
@Component
public class UndoHandler implements ActivityHandler {
  private Logger logger = LoggerFactory.getLogger(UndoHandler.class);

  @Override
  public String getType() {
    return "Undo";
  }

  @Override
  public void handle(String userId, JsonNode activity) {
    // Undo アクティビティの処理を実装する
    String undoType = activity.get("object").get("type").asText();
    if (undoType.equals("Follow")) {
      JsonNode object = activity.get("object");
      logger.info("Undo Follow アクティビティを受信しました: " + object.toString());
    } else {
      // noop
      logger.info("未対応の Undo アクティビティを受信しました" + undoType);
    }
  }
}
