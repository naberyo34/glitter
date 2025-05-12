package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.service.ActivityReceiveDispatcherService;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/user/{id}/inbox")
public class InboxController {
  @Autowired
  private ActivityReceiveDispatcherService activityReceiveDispatcherService;

  @Operation(summary = "アクションに対する応答", description = "外部からInbox宛に通知されたアクションに対して応答を行います。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json")
      }),
      @ApiResponse(responseCode = "400", description = "未対応のアクションが通知されたとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "500", description = "アクションの処理が何らかの理由で失敗したとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
  })
  @PostMapping("")
  public ResponseEntity<Void> receiveInbox(@PathVariable String id, @RequestBody JsonNode requestBody) {
    try {
      activityReceiveDispatcherService.dispatch(id, requestBody);
      return ResponseEntity.ok().build();
    } catch (UnsupportedOperationException e) {
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
