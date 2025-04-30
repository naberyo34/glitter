package com.example.glitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;

@ExtendWith(MockitoExtension.class)
public class FollowUserListServiceTest {
  @Mock
  private FollowRepository followRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private FollowUserListService followUserListService;

  @Test
  void ユーザーのフォロー一覧を取得できる() {
    // モックの準備
    String userId = "test_user";

    Follow mockFollow1 = new Follow();
    mockFollow1.setFollowerId(userId);
    mockFollow1.setFolloweeId("followee1");
    mockFollow1.setTimestamp(new Date());

    Follow mockFollow2 = new Follow();
    mockFollow2.setFollowerId(userId);
    mockFollow2.setFolloweeId("followee2");
    mockFollow2.setTimestamp(new Date());

    List<Follow> mockFollows = Arrays.asList(mockFollow1, mockFollow2);

    User mockUser1 = new User();
    mockUser1.setId("followee1");
    mockUser1.setUsername("フォロー1");
    mockUser1.setProfile("プロフィール1");

    User mockUser2 = new User();
    mockUser2.setId("followee2");
    mockUser2.setUsername("フォロー2");
    mockUser2.setProfile("プロフィール2");

    when(followRepository.findFollowing(userId)).thenReturn(mockFollows);
    // 対象ユーザーの存在判定もしているため userId もモックしておく
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
    when(userRepository.findById("followee1")).thenReturn(Optional.of(mockUser1));
    when(userRepository.findById("followee2")).thenReturn(Optional.of(mockUser2));

    // テスト実行
    List<UserResponse> result = followUserListService.getFollowing(userId);

    // 検証
    assertThat(result).hasSize(2);

    assertEquals("followee1", result.get(0).getId());
    assertEquals("フォロー1", result.get(0).getUsername());
    assertEquals("プロフィール1", result.get(0).getProfile());

    assertEquals("followee2", result.get(1).getId());
    assertEquals("フォロー2", result.get(1).getUsername());
    assertEquals("プロフィール2", result.get(1).getProfile());
  }

  @Test
  void ユーザーのフォロワー一覧を取得できる() {
    // モックの準備
    String userId = "test_user";

    Follow mockFollow1 = new Follow();
    mockFollow1.setFollowerId("follower1");
    mockFollow1.setFolloweeId(userId);
    mockFollow1.setTimestamp(new Date());

    Follow mockFollow2 = new Follow();
    mockFollow2.setFollowerId("follower2");
    mockFollow2.setFolloweeId(userId);
    mockFollow2.setTimestamp(new Date());

    List<Follow> mockFollows = Arrays.asList(mockFollow1, mockFollow2);

    User mockUser1 = new User();
    mockUser1.setId("follower1");
    mockUser1.setUsername("フォロワー1");
    mockUser1.setProfile("プロフィール1");

    User mockUser2 = new User();
    mockUser2.setId("follower2");
    mockUser2.setUsername("フォロワー2");
    mockUser2.setProfile("プロフィール2");

    when(followRepository.findFollowers(userId)).thenReturn(mockFollows);
    // 対象ユーザーの存在判定もしているため userId もモックしておく
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
    when(userRepository.findById("follower1")).thenReturn(Optional.of(mockUser1));
    when(userRepository.findById("follower2")).thenReturn(Optional.of(mockUser2));

    // テスト実行
    List<UserResponse> result = followUserListService.getFollowers(userId);

    // 検証
    assertThat(result).hasSize(2);

    assertEquals("follower1", result.get(0).getId());
    assertEquals("フォロワー1", result.get(0).getUsername());
    assertEquals("プロフィール1", result.get(0).getProfile());

    assertEquals("follower2", result.get(1).getId());
    assertEquals("フォロワー2", result.get(1).getUsername());
    assertEquals("プロフィール2", result.get(1).getProfile());
  }
}
