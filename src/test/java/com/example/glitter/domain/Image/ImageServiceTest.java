package com.example.glitter.domain.Image;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageServiceTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");
  static MinIOContainer minio = new MinIOContainer("minio/minio");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
    minio.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
    minio.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("env.storage-url", minio::getS3URL);
    registry.add("env.minio-username", minio::getUserName);
    registry.add("env.minio-password", minio::getPassword);
    registry.add("env.storage-bucket-name", () -> "test");
  }

  @Autowired
  private ImageService imageService;
  @Autowired
  private S3Client s3Client;

  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  @Transactional
  void 画像をアップロードおよび削除できる() throws Exception {
    try {
      MultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
          Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
      String key = "example.jpg";
      imageService.upload(mockMultipartFile, key);

      // 存在確認
      HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
          .bucket("test")
          .key(key)
          .build());
      assertNotNull(headResponse);

      // 削除して、存在しないことを確認
      imageService.delete(key);
      // headObject は存在しなければ例外を返すため、 throw を期待する
      assertThrows(S3Exception.class, () -> {
        s3Client.headObject(HeadObjectRequest.builder()
            .bucket("test")
            .key(key)
            .build());
      });

    } catch (Exception e) {
      fail(e);
    }
  }
}
