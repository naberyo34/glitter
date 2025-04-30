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

  private final String TEST_API_URL = "https://api.example.com";
  private final String TEST_STORAGE_URL = "https://storage.example.com";
  private final String TEST_BUCKET_NAME = "test-bucket";
  private final String TEST_USER_ID = "test_user";
  private final String TEST_USER_NAME = "テストユーザー";
  private final String TEST_USER_PROFILE = "テスト用のアカウントです。";
  private final String TEST_USER_ICON = "test_user/icon.jpg";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(activityPubService, "apiUrl", TEST_API_URL);
    ReflectionTestUtils.setField(activityPubService, "storageUrl", TEST_STORAGE_URL);
    ReflectionTestUtils.setField(activityPubService, "bucketName", TEST_BUCKET_NAME);
  }

  @Test
  void 存在するユーザーIDを渡すと正しいActorオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setId(TEST_USER_ID);
    mockUser.setUsername(TEST_USER_NAME);
    mockUser.setProfile(TEST_USER_PROFILE);
    mockUser.setIcon(TEST_USER_ICON);

    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<Actor> actorOpt = activityPubService.getActorObject(TEST_USER_ID);

    // 検証
    assertTrue(actorOpt.isPresent());
    Actor actor = actorOpt.get();

    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, actor.getId());
    assertEquals("Person", actor.getType());
    assertEquals(TEST_USER_ID, actor.getPreferredUsername());
    assertEquals(TEST_USER_NAME, actor.getName());
    assertEquals(TEST_USER_PROFILE, actor.getSummary());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID + "/inbox", actor.getInbox());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID + "/outbox", actor.getOutbox());
    assertEquals(TEST_STORAGE_URL + "/" + TEST_USER_ICON, actor.getIcon()[0]);
  }

  @Test
  void 存在しないユーザーIDを渡すとEmptyが返る() {
    when(userRepository.findById("not_exist_user")).thenReturn(Optional.empty());

    Optional<Actor> actorOpt = activityPubService.getActorObject("not_exist_user");

    assertTrue(actorOpt.isEmpty());
  }

  @Test
  void 存在する投稿IDを渡すと正しいNoteオブジェクトが返る() {
    // モックの準備
    Post mockPost = new Post();
    mockPost.setId(1L);
    mockPost.setContent("テスト投稿");
    mockPost.setCreatedAt(new Date());
    mockPost.setUserId(TEST_USER_ID);

    when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

    // テスト実行
    Optional<Note> noteOpt = activityPubService.getNoteObject(1L);

    // 検証
    assertTrue(noteOpt.isPresent());
    Note note = noteOpt.get();
    assertEquals(TEST_API_URL + "/post/1", note.getId());
    assertEquals("Note", note.getType());
    assertEquals("テスト投稿", note.getContent());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, note.getAttributedTo());
  }

  @Test
  void 存在しない投稿IDを渡すとEmptyが返る() {
    when(postRepository.findById(999L)).thenReturn(Optional.empty());

    Optional<Note> noteOpt = activityPubService.getNoteObject(999L);
    assertTrue(noteOpt.isEmpty());
  }

  @Test
  void 存在する投稿IDを渡すと正しいActivityオブジェクトが返る() {
    // モックの準備
    Post mockPost = new Post();
    mockPost.setId(1L);
    mockPost.setContent("テスト投稿");
    mockPost.setCreatedAt(new Date());
    mockPost.setUserId(TEST_USER_ID);

    User mockUser = new User();
    mockUser.setId(TEST_USER_ID);
    mockUser.setUsername(TEST_USER_NAME);

    when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<Activity> activityOpt = activityPubService.getActivityFromPost(1L);

    // 検証
    assertTrue(activityOpt.isPresent());
    Activity activity = activityOpt.get();

    assertEquals(TEST_API_URL + "/activity/1", activity.getId());
    assertEquals("Create", activity.getType());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, activity.getActor());

    Note note = (Note) activity.getObject();
    assertEquals("Note", note.getType());
    assertEquals("テスト投稿", note.getContent());
    assertEquals(TEST_API_URL + "/post/1", note.getId());
  }

  @Test
  void 存在するユーザーIDを渡すと正しいOutboxオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setId(TEST_USER_ID);
    mockUser.setUsername(TEST_USER_NAME);

    Post mockPost1 = new Post();
    mockPost1.setId(1L);
    mockPost1.setContent("テスト投稿1");
    mockPost1.setCreatedAt(new Date());
    mockPost1.setUserId(TEST_USER_ID);

    Post mockPost2 = new Post();
    mockPost2.setId(2L);
    mockPost2.setContent("テスト投稿2");
    mockPost2.setCreatedAt(new Date());
    mockPost2.setUserId(TEST_USER_ID);

    List<Post> mockPosts = Arrays.asList(mockPost1, mockPost2);

    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserId(TEST_USER_ID)).thenReturn(mockPosts);

    // テスト実行
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject(TEST_USER_ID);

    // 検証
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID + "/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(2, outbox.getTotalItems());

    assertTrue(outbox.getOrderedItems() != null && !outbox.getOrderedItems().isEmpty());
    ActivityPubObject firstItem = outbox.getOrderedItems().get(0);
    assertTrue(firstItem instanceof Activity);

    Activity activity = (Activity) firstItem;
    assertEquals("Create", activity.getType());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, activity.getActor());
    assertTrue(activity.getId().startsWith(TEST_API_URL + "/activity/"));

    Note note = (Note) activity.getObject();
    assertEquals("Note", note.getType());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, note.getAttributedTo());
  }

  @Test
  void 投稿のないユーザーIDを渡すと空のOutboxオブジェクトが返る() {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user_2");
    mockUser.setUsername("投稿なしユーザー");

    List<Post> emptyPosts = new ArrayList<>();

    when(userRepository.findById("test_user_2")).thenReturn(Optional.of(mockUser));
    when(postRepository.findPostsByUserId("test_user_2")).thenReturn(emptyPosts);

    // テスト実行
    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("test_user_2");

    // 検証
    assertTrue(outboxOpt.isPresent());
    OrderedCollection outbox = outboxOpt.get();

    assertEquals(TEST_API_URL + "/user/test_user_2/outbox", outbox.getId());
    assertEquals("OrderedCollection", outbox.getType());
    assertEquals(0, outbox.getTotalItems());
    assertTrue(outbox.getOrderedItems() == null || outbox.getOrderedItems().isEmpty());
  }

  @Test
  void 存在しないユーザーIDのOutboxを取得するとEmptyが返る() {
    when(userRepository.findById("not_exist_user")).thenReturn(Optional.empty());

    Optional<OrderedCollection> outboxOpt = activityPubService.getOutboxObject("not_exist_user");

    assertTrue(outboxOpt.isEmpty());
  }
}
