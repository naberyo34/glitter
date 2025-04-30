package com.example.glitter.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.User.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class NodeInfoController {
  @Autowired
  private UserRepository userRepository;

  @Value("${env.api-url}")
  private String apiUrl;

  @Operation(summary = "NodeInfo の通信先を取得する", description = "ActivityPub 向けに NodeInfo に対応していること、その通信先を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json")
      }) })
  @GetMapping("/.well-known/nodeinfo")
  public Map<String, Object> getNodeInfoDiscovery() {
    return Map.of(
        "links", List.of(
            Map.of(
                "rel", "http://nodeinfo.diaspora.software/ns/schema/2.1",
                "href", apiUrl + "/nodeinfo/2.1")));
  }

  @Operation(summary = "NodeInfo を取得する", description = "ActivityPub 向けに NodeInfo の実体を返します。", responses = {
    @ApiResponse(responseCode = "200", description = "OK", content = {
        @Content(mediaType = "application/json")
    }) })
  @GetMapping("/nodeinfo/2.1")
  public Map<String, Object> getNodeInfo() {
    return Map.of(
        "openRegistrations", false,
        "protocols", List.of("activitypub"),
        "software", Map.of(
            "name", "glitter",
            "version", "0.0.1"),
        "usage", Map.of(
            "users", Map.of(
                "total", userRepository.countAll())),
        "services", Map.of(
            "inbound", List.of(),
            "outbound", List.of()),
        "metadata", Map.of(),
        "version", "2.1");
  }
}
