package com.example.glitter.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * 開発環境用の MinIO 向け S3 クライアント 設定
 */
@Configuration
@Profile("development")
public class DevelopmentS3Config {
  @Value("${env.storage-url}")
  private String url;
  @Value("${env.minio-username}")
  private String username;
  @Value("${env.minio-password}")
  private String password;

  @Bean
  S3Client s3Client() {
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(username, password));
    return S3Client.builder()
        .endpointOverride(URI.create(url))
        .credentialsProvider(credentialsProvider)
        .region(Region.AP_NORTHEAST_1)
        .serviceConfiguration(
            S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .build();
  }
}
