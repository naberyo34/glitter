package com.example.glitter.domain.ActivityPub.ActivityReceiveHandler;

import com.fasterxml.jackson.databind.JsonNode;

public interface ActivityReceiveHandler {
  String getType();
  void handle(String userId, JsonNode activity);
}
