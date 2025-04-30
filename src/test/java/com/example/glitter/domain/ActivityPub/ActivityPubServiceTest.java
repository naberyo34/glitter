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
  void 存在する投稿IDを渡すと正しいNoteオブジェクトが返る() {
    Optional<Note> noteOpt = activityPubService.getNoteObject(1L);
    assertTrue(noteOpt.isPresent());
    Note note = noteOpt.get();
    assertEquals(apiUrl + "/post/1", note.getId());
    assertEquals("Note", note.getType());
    assertEquals("テスト投稿", note.getContent());
    assertEquals(apiUrl + "/user/test_user", note.getAttributedTo());
  }

  @Test
  void 存在しない投稿IDを渡すとEmptyが返る() {
    Optional<Note> noteOpt = activityPubService.getNoteObject(999L);
    assertTrue(noteOpt.isEmpty());
  }

  @Test
  void 存在するユーザーIDを渡すと正しいOutboxオブジェクトが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("test_user");
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals(apiUrl + "/user/test_user/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    // シードデータには2件の投稿が含まれるため、2件のtotalItemsが含まれることを確認
    assertTrue(outbox.getTotalItems() >= 2);

    // orderedItemsにActivityが含まれている
    assertTrue(outbox.getOrderedItems() != null && !outbox.getOrderedItems().isEmpty());
    ActivityPubObject firstItem = outbox.getOrderedItems().get(0);
    assertTrue(firstItem instanceof Activity);

    // Activityの内容が正しいことを確認
    Activity activity = (Activity) firstItem;
    assertEquals("Create", activity.getType());
    assertEquals(apiUrl + "/user/test_user", activity.getActor());
    assertTrue(activity.getId().startsWith(apiUrl + "/activity/"));

    // Noteの内容が正しいことを確認
    Note note = (Note) activity.getObject();
    assertEquals("Note", note.getType());
    assertEquals(apiUrl + "/user/test_user", note.getAttributedTo());
    assertEquals("テスト投稿2", note.getContent());
  }

  @Test
  void 投稿のないユーザーIDを渡すと空のOutboxオブジェクトが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("test_user_2");

    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals(apiUrl + "/user/test_user_2/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(0, outbox.getTotalItems());

    assertTrue(outbox.getOrderedItems() == null || outbox.getOrderedItems().isEmpty());
  }

  @Test
  void 存在しないユーザーIDのOutboxを取得するとEmptyが返る() {
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("not_exist_user");

    assertTrue(outboxOpt.isEmpty());
  }
}
