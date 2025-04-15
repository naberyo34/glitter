package com.example.glitter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.WebFinger.WebFingerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class WebFingerController {
  @Autowired
  private WebFingerService webFingerService;

  @Operation(summary = "WebFinger の応答を取得する", description = "ActivityPub 向けに WebFinger リソースを提供します。acct:username@domain 形式のリソースパラメータが必要です。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/jrd+json")
      }),
      @ApiResponse(responseCode = "400", description = "リソースパラメータが不正または存在しない場合"),
      @ApiResponse(responseCode = "404", description = "指定されたユーザーが見つからない場合")
  })
  @GetMapping(value = "/.well-known/webfinger", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> getWebFinger(@RequestParam(name = "resource", required = true) String resource)
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
