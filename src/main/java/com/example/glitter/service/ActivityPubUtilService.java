package com.example.glitter.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

import com.example.glitter.domain.ActivityPub.Accept;
import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ActivityPubUtilService {
  @Value("${env.api-url}")
  private String apiUrl;

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Value("${env.private-key-path}")
  private String privateKeyPath;

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

    ResponseEntity<JsonNode> response = restTemplate.exchange(
        actorUrl,
        HttpMethod.GET,
        request,
        JsonNode.class);

    JsonNode responseBody = response.getBody();
    if (responseBody == null || !responseBody.has("inbox")) {
      throw new RuntimeException("Inbox の URL が取得できません");
    }
    return responseBody.get("inbox").asText();
  }

  /**
   * フォローリクエストを承認する
   * 
   * @param followeeId
   * @param follow
   */
  public ResponseEntity<JsonNode> acceptFollowRequest(String followeeId, ActivityPubFollow follow) throws Exception {
    try {
      Accept accept = Accept.builder()
          // ID は適当でいいらしい
          .id(apiUrl + "/activity/" + UUID.randomUUID())
          .actor(apiUrl + "/user/" + followeeId)
          .object(follow)
          .build();
      String followerActorUrl = follow.getActor();
      String followerInboxUrl = getInboxFromActor(followerActorUrl);

      // ヘッダーを作成して署名する
      String body = objectMapper.writeValueAsString(accept);
      String digest = "SHA-256=" + Base64.getEncoder().encodeToString(
          MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8)));
      Map<String, String> headers = new LinkedHashMap<>();
      String method = "post";
      String requestUri = URI.create(followerInboxUrl).getRawPath();
      headers.put("host", URI.create(followerInboxUrl).getHost());
      headers.put("date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
      headers.put("digest", digest);
      headers.put("content-type", "application/activity+json");

      String keyId = apiUrl + "/user/" + followeeId + "#main-key";
      List<String> signedHeaders = Arrays.asList("(request-target)", "host", "date", "digest", "content-type");
      PrivateKey privateKey = loadPrivateKey();
      Signature signature = new Signature(keyId, "hs2019", "rsa-sha256", null, signedHeaders);
      Signer signer = new Signer(privateKey, signature);
      Signature signed = signer.sign(method, requestUri, headers);

      // フォローの承認を送り返す
      String authorizationHeader = signed.toString();
      String signatureHeader = authorizationHeader.substring("Signature ".length());
      HttpHeaders httpHeaders = new HttpHeaders();
      headers.forEach(httpHeaders::set);
      httpHeaders.set("authorization", authorizationHeader);
      httpHeaders.set("signature", signatureHeader);
      HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
      ResponseEntity<JsonNode> response = restTemplate.postForEntity(followerInboxUrl, entity, JsonNode.class);
      return response;
    } catch (Exception e) {
      throw new RuntimeException("フォローリクエストの承認に失敗しました", e);
    }
  }

  private PrivateKey loadPrivateKey() throws Exception {
    String privateKeyPem = new String(Files.readAllBytes(Paths.get(privateKeyPath)),
        StandardCharsets.UTF_8);
    String key = privateKeyPem
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    byte[] keyBytes = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
  }
}
