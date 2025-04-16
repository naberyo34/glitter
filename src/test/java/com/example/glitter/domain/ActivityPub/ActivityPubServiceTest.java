package com.example.glitter.domain.ActivityPub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActivityPubServiceTest {
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
  private ActivityPubService activityPubService;

  @Value("${env.api-url}")
  private String apiUrl;

  @Test
  void 存在するユーザーIDを渡すと正しいActorオブジェクトが返る() {
    Optional<Actor> actorOpt = activityPubService.getActorObject("test_user");

    assertTrue(actorOpt.isPresent());
    Actor actor = actorOpt.get();

    assertEquals(apiUrl + "/user/test_user", actor.getId());
    assertEquals("Person", actor.getType());
    assertEquals("test_user", actor.getPreferredUsername());
    assertEquals("テストユーザー", actor.getName());
    assertEquals("テスト用のアカウントです。", actor.getSummary());
    assertEquals(apiUrl + "/user/test_user/inbox", actor.getInbox());
    assertEquals(apiUrl + "/user/test_user/outbox", actor.getOutbox());
  }

  @Test
  void 存在しないユーザーIDを渡すとEmptyが返る() {
    Optional<Actor> actorOpt = activityPubService.getActorObject("not_exist_user");

    assertTrue(actorOpt.isEmpty());
  }
}
