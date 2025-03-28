package com.example.glitter.domain.Image;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageServiceTest {
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
  public ImageService imageService;
  // セッションユーザーの取得に使う
  @Autowired
  public UserService userService;

  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  @WithMockUser(username = "test_user")
  void 画像のアップロードと削除ができる() throws Exception {
    try {
      UserSummaryDto user = userService.getSessionUser().orElseThrow();
      MultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
          Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
      Optional<String> resultPathString = imageService.upload(mockMultipartFile, user);
      Path resultPath = Path.of(resultPathString.get());
      // 追加した画像の存在確認
      assertTrue(Files.exists(resultPath));
      // 後始末ついでに削除もテストする
      imageService.delete(resultPathString.get());
      assertTrue(Files.notExists(resultPath));
    } catch (Exception e) {
      fail(e);
    }
  }
}
