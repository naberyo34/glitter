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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

import com.example.glitter.domain.ActivityPub.Accept;
import com.example.glitter.domain.ActivityPub.ActivityPubFollow;
import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class ActivityPubUtilService {
  @Value("${env.api-url}")
  private String apiUrl;
  @Value("${env.domain}")
  private String domain;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  private Logger logger = LoggerFactory.getLogger(ActivityPubUtilService.class);

  @Value("${env.private-key-path}")
  private String privateKeyPath;

  /**
   * アクターエンドポイントに問い合わせてアクター情報を取得する
   * 
   * @param actorUrl
   * @return JsonNode
   */
  public Actor getActorFromUrl(String actorUrl) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.parseMediaType("application/activity+json")));
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<JsonNode> response = restTemplate.exchange(
        actorUrl,
        HttpMethod.GET,
        request,
        JsonNode.class);

    JsonNode actorNode = response.getBody();
    if (actorNode == null) {
      throw new RuntimeException("アクター情報の取得に失敗しました");
    }

    Actor actor = Actor.builder()
        .id(actorNode.get("id").asText())
        .preferredUsername(actorNode.get("preferredUsername").asText())
        .name(actorNode.get("name").asText())
        .summary(actorNode.get("summary").asText())
        .inbox(actorNode.get("inbox").asText())
        .outbox(actorNode.get("outbox").asText())
        .icon(Actor.Icon.builder()
            .type(actorNode.get("icon").get("type").asText())
            .url(actorNode.get("icon").get("url").asText())
            .build())
        .build();
    return actor;
  }

  /**
   * フォローリクエストを承認する
   * 
   * @param followeeId
   * @param follow
   */
  public void acceptFollowRequest(String followeeId, ActivityPubFollow follow) throws Exception {
    Accept accept = Accept.builder()
        // ID は適当でいいらしい
        .id(apiUrl + "/activity/" + UUID.randomUUID())
        .actor(apiUrl + "/user/" + followeeId)
        .object(follow)
        .build();

    String followerActorUrl = follow.getActor();
    Actor followerActor = getActorFromUrl(followerActorUrl);
    String followerInboxUrl = followerActor.getInbox();

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

    logger.info("Status Code: {}", response.getStatusCode());

    // Mastodon も Misskey も成功時は 202 ACCEPTED を返している
    if (response.getStatusCode().toString().equals("202 ACCEPTED")) {
      String followerId = followerActor.getPreferredUsername();
      String followerDomain = URI.create(followerActor.getId()).getHost();
      try {
        // 成功した場合はフォロワーを外部ユーザーとして追加
        UserDto user = UserDto.builder()
            .userId(followerId)
            .domain(followerDomain)
            .actorUrl(followerActorUrl)
            .username(followerActor.getName())
            .profile(followerActor.getSummary())
            .icon(followerActor.getIcon().getUrl())
            .build();
        User resultUser = userRepository.insert(user.toEntity());
        logger.info("Inserted user: {}", objectMapper.writeValueAsString(resultUser));

        // フォロー関係も追加
        FollowDto followDto = FollowDto.builder()
            .followerId(user.getUserId())
            .followerDomain(user.getDomain())
            .followeeId(followeeId)
            .followeeDomain(domain)
            .build();
        Follow resultFollow = followRepository.insert(followDto.toEntity());
        logger.info("Inserted follow: {}", objectMapper.writeValueAsString(resultFollow));
      } catch (Exception e) {
        throw new RuntimeException("フォローのDB登録に失敗しました。", e);
      }
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
