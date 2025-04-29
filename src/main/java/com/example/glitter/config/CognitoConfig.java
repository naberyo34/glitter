package com.example.glitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * AWS Cognito クライアント設定
 */
@Configuration
public class CognitoConfig {
  @Bean
  CognitoIdentityProviderClient cognitoIdentityProviderClient() {
    return CognitoIdentityProviderClient.builder().region(Region.AP_NORTHEAST_1)
        .build();
  }
}
