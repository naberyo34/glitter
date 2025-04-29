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

  @Test
  void 存在するユーザーIDを渡すと正しいOutboxオブジェクトが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("test_user");
    
    // outbox が存在する
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();
    
    assertEquals(apiUrl + "/user/test_user/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    // シードデータには2件の投稿が含まれるため、2件のtotalItemsが含まれることを確認
    assertTrue(outbox.getTotalItems() >= 2);
    assertEquals(apiUrl + "/user/test_user/outbox", outbox.getFirst());
    
    // orderedItemsにActivityが含まれている
    assertTrue(outbox.getOrderedItems() != null && !outbox.getOrderedItems().isEmpty());
    Object firstItem = outbox.getOrderedItems().get(0);
    assertTrue(firstItem instanceof Activity);
    
    Activity activity = (Activity) firstItem;
    assertEquals("Create", activity.getType());
    assertTrue(activity.getId().startsWith(apiUrl + "/user/test_user/post/"));
    

    Note note = activity.getObject();
    assertTrue(note.getId().startsWith(apiUrl + "/user/test_user/note/"));
    assertEquals("Note", note.getType());
    assertTrue(note.getContent() != null && !note.getContent().isEmpty());
  }
  
  @Test
  void 投稿のないユーザーIDを渡すと空のOutboxオブジェクトが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("test_user_2");
    
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();
    
    assertEquals(apiUrl + "/user/test_user_2/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(0, outbox.getTotalItems());
    assertEquals(apiUrl + "/user/test_user_2/outbox", outbox.getFirst());
    
    assertTrue(outbox.getOrderedItems() == null || outbox.getOrderedItems().isEmpty());
  }
  
  @Test
  void 存在しないユーザーIDのOutboxを取得するとEmptyが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("not_exist_user");
    
    assertTrue(outboxOpt.isEmpty());
  }
}
