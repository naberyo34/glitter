package com.example.glitter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.ActivityPub.Actor;

/**
 * 通信を伴う処理のため 結合テストとして実施
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActivityPubCreateServiceIntegrationTest {
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

  @Value("${env.api-url}")
  private String apiUrl;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  ActivityPubCreateService activityPubCreateService;

  @Test
  void 存在するアクターエンドポイントからアクター情報が取得できる() throws Exception {
    // サーバー自身の存在するアクターポイントに問い合わせる
    // テスト実行時はポートがランダムで変わるため、ここで取得した値を用いる
    String currentApiUrl = "http://localhost:" + port;
    Actor actor = activityPubCreateService.getActorFromUrl(currentApiUrl + "/user/test_user");

    // とりあえず inbox が取れていればよしとする
    assertEquals("https://example.com/user/test_user/inbox", actor.getInbox());
  }

  @Test
  void 存在しないアクターエンドポイントからアクター情報を取得しようとしたとき例外が返る() throws Exception {
    try {
      activityPubCreateService.getActorFromUrl("http://localhost:" + port + "/user/not_exist_user");
      fail();
    } catch (RuntimeException e) {
      assertNotNull(e);
    }
  }
}
