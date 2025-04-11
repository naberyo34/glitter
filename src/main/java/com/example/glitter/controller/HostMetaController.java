package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class HostMetaController {
  @Value("${env.api-url}")
  private String apiUrl;

  @Operation(summary = "WebFinger の通信先を取得する", description = "ActivityPub 向けに WebFinger に対応していること、その通信先を返します。", responses = {
    @ApiResponse(responseCode = "200", description = "OK", content = {
        @Content(mediaType = "application/json")
    }) })
  @GetMapping(value = "/.well-known/host-meta")
  public ResponseEntity<String> getHostMeta() {
    String xml = String.format("""
        <?xml version="1.0" encoding="UTF-8"?>
        <XRD xmlns="http://docs.oasis-open.org/ns/xri/xrd-1.0">
          <Link rel="lrdd" type="application/xrd+xml" template="%s/.well-known/webfinger?resource={uri}" />
        </XRD>
        """, apiUrl);

    return ResponseEntity
        .ok()
        .header("Content-Type", "application/xrd+xml; charset=utf-8")
        .body(xml);
  }
}
