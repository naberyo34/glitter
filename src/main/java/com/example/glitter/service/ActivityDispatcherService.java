package com.example.glitter.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.ActivityPub.ActivityHandler.ActivityHandler;
import com.example.glitter.domain.ActivityPub.ActivityHandler.FollowHandler;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Inbox に送られてきた Activity をハンドリングするクラス
 */
@Service
public class ActivityDispatcherService {
  private final Map<String, ActivityHandler> handlerMap;
  private Logger logger = LoggerFactory.getLogger(FollowHandler.class);

  public ActivityDispatcherService(List<ActivityHandler> handlers) {
    this.handlerMap = handlers.stream()
        .collect(Collectors.toMap(ActivityHandler::getType, h -> h));
  }

  public void dispatch(String userId, JsonNode activity) {
    String type = activity.get("type").asText();
    ActivityHandler handler = handlerMap.get(type);
    if (handler == null) {
      logger.info("未知のアクティビティを受信しました: " + type);
      throw new UnsupportedOperationException("対応するアクションがありません: " + type);
    }
    handler.handle(userId, activity);
  }
}
