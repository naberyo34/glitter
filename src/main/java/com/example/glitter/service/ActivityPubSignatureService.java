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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ActivityPub の HTTP Signature 関連サービス
 */
@Service
public class ActivityPubSignatureService {
  @Autowired
  private ObjectMapper objectMapper;

  @Value("${env.api-url}")
  private String apiUrl;
  @Value("${env.private-key-path}")
  private String privateKeyPath;

  /**
   * HTTP Signature で署名された HTTP Entity を作成する
   * 
   * @param requestBody    送信するリクエストボディ
   * @param userId         リクエストを送信するユーザーの ID
   * @param targetInboxUrl リクエストの送信先 Inbox URL
   * @return HTTP Entity
   */
  public HttpEntity<String> createSignedHttpEntity(JsonNode requestBody, String userId, String targetInboxUrl)
      throws Exception {
    {
      // リクエストボディから Digest を作成
      String bodyString = objectMapper.writeValueAsString(requestBody);
      String digest = "SHA-256=" + Base64.getEncoder().encodeToString(
          MessageDigest.getInstance("SHA-256").digest(bodyString.getBytes(StandardCharsets.UTF_8)));

      // リクエストヘッダーを作成
      Map<String, String> headers = new LinkedHashMap<>();
      headers.put("host", URI.create(targetInboxUrl).getHost());
      headers.put("date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)));
      headers.put("digest", digest);
      headers.put("content-type", "application/activity+json");

      // Signature を作成
      String keyId = apiUrl + "/user/" + userId + "#main-key";
      List<String> signedHeaders = Arrays.asList("(request-target)", "host", "date", "digest", "content-type");
      PrivateKey privateKey = loadPrivateKey();
      Signature signature = new Signature(keyId, "hs2019", "rsa-sha256", null, signedHeaders);
      Signer signer = new Signer(privateKey, signature);

      String method = "post";
      String requestUri = URI.create(targetInboxUrl).getRawPath();
      Signature signed = signer.sign(method, requestUri, headers);

      // リクエストボディとヘッダーを組み合わせて HttpEntity を作成
      String authorizationHeader = signed.toString();
      // TODO: もうちょっといい方法ないのか?
      String signatureHeader = authorizationHeader.substring("Signature ".length());
      HttpHeaders httpHeaders = new HttpHeaders();
      headers.forEach(httpHeaders::set);
      httpHeaders.set("authorization", authorizationHeader);
      httpHeaders.set("signature", signatureHeader);
      return new HttpEntity<String>(bodyString, httpHeaders);
    }
  }

  /**
   * 秘密鍵を読み込む
   * 
   * @return
   * @throws Exception
   */
  public PrivateKey loadPrivateKey() throws Exception {
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
