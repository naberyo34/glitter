package com.example.glitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

@ExtendWith(MockitoExtension.class)
public class SessionUserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PostRepository postRepository;
  @Mock
  private FollowRepository followRepository;
  @Mock
  private FollowUserListService followUserListService;
  @Mock
  private ImageWriteService imageWriteService;
  @InjectMocks
  private SessionUserService sessionUserService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(sessionUserService, "domain", "example.com");
  }

  @Test
  void ログイン中にセッションユーザーを取得したとき正しいユーザーが返る() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setUsername("テストユーザー");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));

    // テスト実行
    UserResponse user = sessionUserService.getMe();

    // 検証
    assertEquals("test_user", user.getUserId());
    assertEquals("テストユーザー", user.getUsername());
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したとき例外が発生する() throws Exception {
    when(userRepository.getSessionUser()).thenReturn(Optional.empty());

    assertThrows(NotLoginException.class, () -> {
      sessionUserService.getMe();
    });
  }

  @Test
  void 自身のフォロー一覧を取得できる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");
    mockUser.setUsername("テストユーザー");

    UserResponse mockUserResponse1 = new UserResponse();
    mockUserResponse1.setUserId("followee1");
    mockUserResponse1.setDomain("example.com");
    mockUserResponse1.setUsername("フォロー中1");

    UserResponse mockUserResponse2 = new UserResponse();
    mockUserResponse2.setUserId("followee2");
    mockUserResponse2.setDomain("example.com");
    mockUserResponse2.setUsername("フォロー中2");

    List<UserResponse> followingUsers = Arrays.asList(mockUserResponse1, mockUserResponse2);

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(followUserListService.getFollowing("test_user")).thenReturn(followingUsers);

    // テスト実行
    List<UserResponse> result = sessionUserService.getFollowing();

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("followee1", result.get(0).getUserId());
    assertEquals("フォロー中1", result.get(0).getUsername());
    assertEquals("followee2", result.get(1).getUserId());
    assertEquals("フォロー中2", result.get(1).getUsername());
  }

  @Test
  void 自身のフォロワー一覧を取得できる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");
    mockUser.setUsername("テストユーザー");

    UserResponse mockUserResponse1 = new UserResponse();
    mockUserResponse1.setUserId("follower1");
    mockUserResponse1.setDomain("example.com");
    mockUserResponse1.setUsername("フォロワー1");

    UserResponse mockUserResponse2 = new UserResponse();
    mockUserResponse2.setUserId("follower2");
    mockUserResponse2.setDomain("example.com");
    mockUserResponse2.setUsername("フォロワー2");

    List<UserResponse> followers = Arrays.asList(mockUserResponse1, mockUserResponse2);

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(followUserListService.getFollowers("test_user")).thenReturn(followers);

    // テスト実行
    List<UserResponse> result = sessionUserService.getFollowers();

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("follower1", result.get(0).getUserId());
    assertEquals("フォロワー1", result.get(0).getUsername());
    assertEquals("follower2", result.get(1).getUserId());
    assertEquals("フォロワー2", result.get(1).getUsername());
  }

  @Test
  void 投稿を作成できる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    Post mockPost = new Post();
    mockPost.setUserId("test_user");
    mockPost.setDomain("example.com");
    mockPost.setContent("テスト投稿");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(postRepository.insert(any(Post.class))).thenReturn(mockPost);

    // テスト実行
    Optional<PostDto> result = sessionUserService.addPost("テスト投稿");

    // 検証
    assertTrue(result.isPresent());
    assertEquals("test_user", result.get().getUserId());
    assertEquals("テスト投稿", result.get().getContent());
  }
}
