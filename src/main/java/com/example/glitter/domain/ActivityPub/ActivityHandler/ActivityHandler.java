package com.example.glitter.domain.ActivityPub.ActivityHandler;

import com.fasterxml.jackson.databind.JsonNode;

public interface ActivityHandler {
  String getType();
  void handle(String userId, JsonNode activity);
}
