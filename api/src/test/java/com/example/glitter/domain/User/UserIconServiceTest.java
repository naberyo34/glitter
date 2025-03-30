package com.example.glitter.domain.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIconServiceTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private UserIconService userIconService;

  @Autowired
  private UserService userService;

  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  @Transactional
  @WithMockUser(username = "test_user")
  void ユーザーのアイコンを変更できる() throws Exception {
    UserSummaryDto sessionUser = userService.getSessionUser().orElseThrow();
    MultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    UserSummaryDto result = userIconService.updateIcon(mockMultipartFile, sessionUser);
    Path resultPath = Path.of(result.getIcon());
    // 追加した画像の存在確認
    assertTrue(Files.exists(resultPath));
    Logger logger = LoggerFactory.getLogger(UserIconServiceTest.class);
    logger.info("resultPath: " + resultPath);
    // 後始末
    Files.delete(resultPath);
    assertTrue(Files.notExists(resultPath));
  }
}
