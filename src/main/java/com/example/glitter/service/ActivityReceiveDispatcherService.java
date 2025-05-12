package com.example.glitter.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.ActivityPub.ActivityReceiveHandler.ActivityReceiveHandler;
import com.example.glitter.domain.ActivityPub.ActivityReceiveHandler.FollowHandler;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Inbox に送られてきた Activity をハンドリングするクラス
 */
@Service
public class ActivityReceiveDispatcherService {
  private final Map<String, ActivityReceiveHandler> handlerMap;
  private Logger logger = LoggerFactory.getLogger(FollowHandler.class);

  public ActivityReceiveDispatcherService(List<ActivityReceiveHandler> handlers) {
    this.handlerMap = handlers.stream()
        .collect(Collectors.toMap(ActivityReceiveHandler::getType, h -> h));
  }

  public void dispatch(String userId, JsonNode activity) {
    String type = activity.get("type").asText();
    ActivityReceiveHandler handler = handlerMap.get(type);
    if (handler == null) {
      logger.info("未知のアクティビティを受信しました: " + type);
      throw new UnsupportedOperationException("対応するアクションがありません: " + type);
    }
    handler.handle(userId, activity);
  }
}
