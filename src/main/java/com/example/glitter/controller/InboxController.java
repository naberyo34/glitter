package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.example.glitter.service.ActivityPubUtilService;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/user/{id}/inbox")
public class InboxController {
  @Autowired
  private ActivityPubUtilService activityPubUtilService;

  @Operation(summary = "フォロー依頼に対する応答", description = "外部からInbox宛に通知されたフォロー依頼に対して応答を行います。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json")
      }),
      @ApiResponse(responseCode = "400", description = "フォロー以外のアクションが通知されたとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "500", description = "フォローの承認が何らかの理由で失敗したとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
  })
  @PostMapping("")
  public ResponseEntity<Void> receiveInbox(@PathVariable String id, @RequestBody JsonNode requestBody) {
    try {
      // TODO: とりあえず Follow 以外のリクエストは無視する
      String type = requestBody.get("type").asText();
      if (!type.equals("Follow")) {
        return ResponseEntity.badRequest().build();
      }
      // Follow だった場合、受け取った JSON を詰め替える
      ActivityPubFollow follow = ActivityPubFollow.builder()
          .id(requestBody.get("id").asText())
          .actor(requestBody.get("actor").asText())
          .object(requestBody.get("object").asText())
          .build();
      activityPubUtilService.acceptFollowRequest(id, follow);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
