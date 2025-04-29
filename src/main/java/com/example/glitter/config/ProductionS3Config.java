package com.example.glitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 本番環境用の AWS 向け S3 クライアント 設定
 */
@Configuration
@Profile("production")
public class ProductionS3Config {
  @Bean
  S3Client s3Client() {
    return S3Client.builder()
        .region(Region.AP_NORTHEAST_1)
        .build();
  }
}
