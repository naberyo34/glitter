package com.example.glitter.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.glitter.domain.ActivityPub.Accept;
import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.example.glitter.domain.ActivityPub.Actor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ActivityPubUtilService {
  @Value("${env.api-url}")
  private String apiUrl;

  @Autowired
  private RestTemplate restTemplate;

  /**
   * アクターエンドポイントに問い合わせて inbox の URL を取得する
   * 
   * @param actorUrl
   * @return inbox の URL
   */
  public String getInboxFromActor(String actorUrl) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.parseMediaType("application/activity+json")));
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Actor> response = restTemplate.exchange(
        actorUrl,
        HttpMethod.GET,
        request,
        Actor.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new RuntimeException("アクターの取得に失敗しました" + actorUrl);
    }

    Actor actor = response.getBody();
    if (actor == null || actor.getInbox() == null) {
      throw new RuntimeException("アクターに inbox の情報が含まれていません" + actorUrl);
    }

    return actor.getInbox();
  }

  /**
   * フォローリクエストを承認する
   * 
   * @param followeeId
   * @param follow
   */
  public void acceptFollowRequest(String followeeId, ActivityPubFollow follow) throws Exception {
    try {
      Accept accept = Accept.builder()
          // ID は適当でいいらしい
          .id(apiUrl + "/activity/" + UUID.randomUUID())
          .actor(apiUrl + "/user/" + followeeId)
          .object(follow)
          .build();
      String followerActorUrl = follow.getActor();
      String followerInboxUrl = getInboxFromActor(followerActorUrl);

      // ヘッダー
      HttpHeaders headers = new HttpHeaders();
      headers.set("Host", URI.create(followerInboxUrl).getHost());
      headers.set("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
      headers.setContentType(MediaType.parseMediaType("application/activity+json"));
      // Digest の作成
      String body = new ObjectMapper().writeValueAsString(accept);
      String digest = "SHA-256=" + Base64.getEncoder().encodeToString(
          MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8)));
      headers.set("Digest", digest);

      // 署名の作成

      // フォローの承認を送り返す
      HttpEntity<String> entity = new HttpEntity<>(body, headers);
      restTemplate.postForEntity(followerInboxUrl, entity, String.class);
    } catch (Exception e) {
      throw new RuntimeException("フォローリクエストの承認に失敗しました", e);
    }
  }
}
