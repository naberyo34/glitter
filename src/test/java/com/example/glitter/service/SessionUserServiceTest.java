package com.example.glitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Follow;
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

  @Test
  void ログイン中にセッションユーザーを取得したとき正しいユーザーが返る() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));

    // テスト実行
    UserResponse user = sessionUserService.getMe();

    // 検証
    assertEquals("test_user", user.getId());
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
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    UserResponse mockUserResponse1 = new UserResponse();
    mockUserResponse1.setId("followee1");
    mockUserResponse1.setUsername("フォロー中1");

    UserResponse mockUserResponse2 = new UserResponse();
    mockUserResponse2.setId("followee2");
    mockUserResponse2.setUsername("フォロー中2");

    List<UserResponse> followingUsers = Arrays.asList(mockUserResponse1, mockUserResponse2);

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(followUserListService.getFollowing("test_user")).thenReturn(followingUsers);

    // テスト実行
    List<UserResponse> result = sessionUserService.getFollowing();

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("followee1", result.get(0).getId());
    assertEquals("フォロー中1", result.get(0).getUsername());
    assertEquals("followee2", result.get(1).getId());
    assertEquals("フォロー中2", result.get(1).getUsername());
  }

  @Test
  void 自身のフォロワー一覧を取得できる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    UserResponse mockUserResponse1 = new UserResponse();
    mockUserResponse1.setId("follower1");
    mockUserResponse1.setUsername("フォロワー1");

    UserResponse mockUserResponse2 = new UserResponse();
    mockUserResponse2.setId("follower2");
    mockUserResponse2.setUsername("フォロワー2");

    List<UserResponse> followers = Arrays.asList(mockUserResponse1, mockUserResponse2);

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(followUserListService.getFollowers("test_user")).thenReturn(followers);

    // テスト実行
    List<UserResponse> result = sessionUserService.getFollowers();

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("follower1", result.get(0).getId());
    assertEquals("フォロワー1", result.get(0).getUsername());
    assertEquals("follower2", result.get(1).getId());
    assertEquals("フォロワー2", result.get(1).getUsername());
  }

  @Test
  void 投稿を作成できる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    Post mockPost = new Post();
    mockPost.setId(1L);
    mockPost.setUserId("test_user");
    mockPost.setContent("テスト投稿");
    mockPost.setCreatedAt(new Date());

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(postRepository.insert(any(Post.class))).thenReturn(mockPost);

    // テスト実行
    Optional<PostDto> result = sessionUserService.addPost("テスト投稿");

    // 検証
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("test_user", result.get().getUserId());
    assertEquals("テスト投稿", result.get().getContent());
  }

  @Test
  void ユーザーをフォローできる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    User mockFollowee = new User();
    mockFollowee.setId("followee");
    mockFollowee.setUsername("フォロー対象");

    Follow mockFollow = new Follow();
    mockFollow.setFollowerId("test_user");
    mockFollow.setFolloweeId("followee");
    mockFollow.setTimestamp(new Date());

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(userRepository.findById("followee")).thenReturn(Optional.of(mockFollowee));
    when(followRepository.findByFollowerIdAndFolloweeId("test_user", "followee")).thenReturn(Optional.empty());
    when(followRepository.insert(any(Follow.class))).thenReturn(mockFollow);

    // テスト実行
    FollowDto result = sessionUserService.follow("followee");

    // 検証
    assertEquals("test_user", result.getFollowerId());
    assertEquals("followee", result.getFolloweeId());
  }

  @Test
  void すでにフォロー済みの場合は既存のフォロー情報が返る() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    Follow existingFollow = new Follow();
    existingFollow.setFollowerId("test_user");
    existingFollow.setFolloweeId("followee");
    existingFollow.setTimestamp(new Date());

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(userRepository.findById("followee")).thenReturn(Optional.of(new User()));
    when(followRepository.findByFollowerIdAndFolloweeId("test_user", "followee"))
        .thenReturn(Optional.of(existingFollow));

    // テスト実行
    FollowDto result = sessionUserService.follow("followee");

    // 検証
    assertEquals("test_user", result.getFollowerId());
    assertEquals("followee", result.getFolloweeId());
  }

  @Test
  void 自分自身をフォローしようとすると例外が発生する() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));

    // テスト実行と検証
    assertThrows(IllegalArgumentException.class, () -> {
      sessionUserService.follow("test_user");
    });
  }

  @Test
  void フォロー解除ができる() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));
    when(followRepository.delete("test_user", "followee")).thenReturn(1);

    // テスト実行
    boolean result = sessionUserService.unfollow("followee");

    // 検証
    assertTrue(result);
  }
}
