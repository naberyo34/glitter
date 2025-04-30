package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.ActivityPub.Activity;
import com.example.glitter.domain.ActivityPub.ActivityPubService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * TODO: とりあえず /activity/{id} を返せるように置いているもの。
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
  @Autowired
  private ActivityPubService activityPubService;

  @Operation(summary = "Activityを取得", description = "ユーザーの投稿をActivity形式で取得します。ActivityPub準拠のクライアントからのリクエスト用です。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/activity+json", schema = @Schema(implementation = Activity.class)),
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}")
  public Activity getActivityById(@PathVariable Long id) {
    return activityPubService.getActivityFromPost(id)
        .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
  }
}
