package com.example.glitter.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Auth.JwtTokenDto;
import com.example.glitter.domain.Auth.UserIdentity;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

@Service
public class CognitoService {
  @Autowired
  private CognitoIdentityProviderClient cognitoIdentityProviderClient;

  @Value("${env.cognito-client-id}")
  private String clientId;

  @Value("${env.cognito-client-secret}")
  private String clientSecret;

  /**
   * AWS Cognito でログインを行う
   * 
   * @param identity
   * @return jwt
   */
  public Optional<JwtTokenDto> login(UserIdentity identity) {
    InitiateAuthRequest request = InitiateAuthRequest.builder()
        .authFlow("USER_PASSWORD_AUTH")
        .clientId(clientId)
        .authParameters(Map.of(
            "USERNAME", identity.getId(),
            "PASSWORD", identity.getPassword(),
            "SECRET_HASH", calculateSecretHash(clientId, clientSecret, identity.getId())))
        .build();

    try {
      InitiateAuthResponse response = cognitoIdentityProviderClient.initiateAuth(request);
      return Optional.of(JwtTokenDto.builder()
          .token(response.authenticationResult().accessToken())
          .build());
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * SECRET_HASH を生成する
   * 
   * @see https://docs.aws.amazon.com/cognito/latest/developerguide/signing-up-users-in-your-app.html#cognito-user-pools-computing-secret-hash
   * @param userPoolClientId
   * @param userPoolClientSecret
   * @param userName
   * @return
   */
  private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
    final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    SecretKeySpec signingKey = new SecretKeySpec(
        userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
        HMAC_SHA256_ALGORITHM);
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);
      mac.update(userName.getBytes(StandardCharsets.UTF_8));
      byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(rawHmac);
    } catch (Exception e) {
      throw new RuntimeException("Error while calculating ");
    }
  }
}
