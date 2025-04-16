package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.WebFinger.WebFinger;
import com.example.glitter.domain.WebFinger.WebFingerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class WebFingerController {
  @Autowired
  private WebFingerService webFingerService;

  @Operation(summary = "WebFinger の応答を取得する", description = "ActivityPub 向けに WebFinger リソースを提供します。acct:username@domain 形式のリソースパラメータが必要です。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/jrd+json", schema = @Schema(implementation = WebFinger.class))
      }),
      @ApiResponse(responseCode = "400", description = "リソースパラメータが不正または存在しない場合", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "404", description = "指定されたユーザーが見つからない場合", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
  })
  @GetMapping(value = "/.well-known/webfinger", produces = "application/jrd+json")
  public ResponseEntity<WebFinger> getWebFinger(@RequestParam(name = "resource", required = true) String resource)
      throws ErrorResponseException {
    if (resource == null || resource.isEmpty()) {
      throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
    }

    return webFingerService.getJrd(resource)
        .map(jrd -> ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType("application/jrd+json"))
            .body(jrd))
        .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
  }
}
