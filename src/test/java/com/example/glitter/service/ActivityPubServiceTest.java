package com.example.glitter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.glitter.domain.ActivityPub.Activity;
import com.example.glitter.domain.ActivityPub.ActivityPubObject;
import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

@ExtendWith(MockitoExtension.class)
public class ActivityPubServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PostRepository postRepository;
  @InjectMocks
  private ActivityPubService activityPubService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(activityPubService, "apiUrl", "https://api.example.com");
    ReflectionTestUtils.setField(activityPubService, "domain", "example.com");
    ReflectionTestUtils.setField(activityPubService, "storageUrl", "https://storage.example.com");
  }

  @Test
  void 存在するユーザーIDを渡すと正しいActorオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");
    mockUser.setUsername("テストユーザー");
    mockUser.setProfile("テスト用のアカウントです。");
    mockUser.setIcon("test_user/icon.jpg");

    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain()))
        .thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<Actor> actorOpt = activityPubService.getActorObject(mockUser.getUserId());

    // 検証
    assertTrue(actorOpt.isPresent());
    Actor actor = actorOpt.get();

    assertEquals("https://api.example.com/user/test_user", actor.getId());
    assertEquals("Person", actor.getType());
    assertEquals("test_user", actor.getPreferredUsername());
    assertEquals("テストユーザー", actor.getName());
    assertEquals("テスト用のアカウントです。", actor.getSummary());
    assertEquals("https://api.example.com/user/test_user/inbox", actor.getInbox());
    assertEquals("https://api.example.com/user/test_user/outbox", actor.getOutbox());
    assertEquals("https://storage.example.com/test_user/icon.jpg", actor.getIcon()[0]);
  }

  @Test
  void 存在しないユーザーIDを渡すとEmptyが返る() {
    when(userRepository.findByUserIdAndDomain("not_exist_user", "example.com")).thenReturn(Optional.empty());

    Optional<Actor> actorOpt = activityPubService.getActorObject("not_exist_user");

    assertTrue(actorOpt.isEmpty());
  }

  @Test
  void 存在する投稿IDを渡すと正しいNoteオブジェクトが返る() {
    // モックの準備
    Post mockPost = new Post();
    mockPost.setUuid("uuid_1");
    mockPost.setContent("テスト投稿");
    mockPost.setUserId("test_user");
    mockPost.setDomain("example.com");
    mockPost.setCreatedAt(new Date());

    when(postRepository.findByUuid(mockPost.getUuid())).thenReturn(Optional.of(mockPost));

    // テスト実行
    Optional<Note> noteOpt = activityPubService.getNoteObject("uuid_1");

    // 検証
    assertTrue(noteOpt.isPresent());
    Note note = noteOpt.get();
    assertEquals("https://api.example.com/post/uuid_1", note.getId());
    assertEquals("Note", note.getType());
    assertEquals("テスト投稿", note.getContent());
    assertEquals("https://api.example.com/user/test_user", note.getAttributedTo());
  }

  @Test
  void 存在しない投稿IDを渡すとEmptyが返る() {
    when(postRepository.findByUuid("uuid_999")).thenReturn(Optional.empty());

    Optional<Note> noteOpt = activityPubService.getNoteObject("uuid_999");
    assertTrue(noteOpt.isEmpty());
  }

  @Test
  void 存在する投稿IDを渡すと正しいActivityオブジェクトが返る() {
    // モックの準備
    Post mockPost = new Post();
    mockPost.setUuid("uuid_1");
    mockPost.setContent("テスト投稿");
    mockPost.setUserId("test_user");
    mockPost.setDomain("example.com");
    mockPost.setCreatedAt(new Date());

    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    when(postRepository.findByUuid(mockPost.getUuid())).thenReturn(Optional.of(mockPost));
    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain()))
        .thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<Activity> activityOpt = activityPubService.getActivityFromPost("uuid_1");

    // 検証
    assertTrue(activityOpt.isPresent());
    Activity activity = activityOpt.get();

    assertEquals("https://api.example.com/activity/uuid_1", activity.getId());
    assertEquals("Create", activity.getType());
    assertEquals("https://api.example.com/user/test_user", activity.getActor());

    Note note = (Note) activity.getObject();
    assertEquals("Note", note.getType());
    assertEquals("テスト投稿", note.getContent());
    assertEquals("https://api.example.com/post/uuid_1", note.getId());
  }

  @Test
  void 存在するユーザーIDを渡すと正しいOutboxオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    Post mockPost1 = new Post();
    mockPost1.setUuid("uuid_1");
    mockPost1.setContent("テスト投稿1");
    mockPost1.setUserId(mockUser.getUserId());
    mockPost1.setDomain(mockUser.getDomain());
    mockPost1.setCreatedAt(new Date());

    Post mockPost2 = new Post();
    mockPost2.setUuid("uuid_2");
    mockPost2.setContent("テスト投稿2");
    mockPost2.setUserId(mockUser.getUserId());
    mockPost2.setDomain(mockUser.getDomain());
    mockPost2.setCreatedAt(new Date());

    List<Post> mockPosts = Arrays.asList(mockPost1, mockPost2);

    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(mockPosts);

    // テスト実行
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject(mockUser.getUserId());

    // 検証
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals("https://api.example.com/user/test_user/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(2, outbox.getTotalItems());

    assertTrue(outbox.getOrderedItems() != null && !outbox.getOrderedItems().isEmpty());
    ActivityPubObject firstItem = outbox.getOrderedItems().get(0);
    assertTrue(firstItem instanceof Activity);

    Activity activity = (Activity) firstItem;
    assertEquals("Create", activity.getType());
    assertEquals("https://api.example.com/user/test_user", activity.getActor());
    assertTrue(activity.getId().startsWith("https://api.example.com/activity/"));

    Note note = (Note) activity.getObject();
    assertEquals("Note", note.getType());
    assertEquals("https://api.example.com/user/test_user", note.getAttributedTo());
  }

  @Test
  void 投稿のないユーザーIDを渡すと空のOutboxオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    List<Post> emptyPosts = new ArrayList<>();

    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(emptyPosts);

    // テスト実行
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject(mockUser.getUserId());

    // 検証
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals("https://api.example.com/user/test_user/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(0, outbox.getTotalItems());
    assertTrue(outbox.getOrderedItems() == null || outbox.getOrderedItems().isEmpty());
  }

  @Test
  void 存在しないユーザーIDのOutboxを取得するとEmptyが返る() {
    when(userRepository.findByUserIdAndDomain("not_exist_user", "example.com")).thenReturn(Optional.empty());

    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("not_exist_user");

    assertTrue(outboxOpt.isEmpty());
  }
}
