package com.example.glitter.domain.User;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.example.glitter.util.WithMockJwt;

/**
 * ストレージの操作ができることを確認するため 結合テストとして実施
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserIconServiceTest {
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
  private UserIconService userIconService;

  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  @WithMockJwt
  void ログインユーザーがアイコンを変更できる() throws Exception {
    MultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    UserResponse resultUser = userIconService.updateIcon(mockMultipartFile);
    assertTrue(resultUser.getIcon().endsWith(".jpg"));
  }

  @Test
  void 非ログインユーザーはアイコンの変更に失敗する() throws Exception {
    MultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    try {
      userIconService.updateIcon(mockMultipartFile);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
