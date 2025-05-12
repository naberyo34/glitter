package com.example.glitter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;

@ExtendWith(MockitoExtension.class)
public class FollowListServiceTest {
  @Mock
  private FollowRepository followRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private FollowListService followListService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(followListService, "domain", "example.com");
  }

  @Test
  void ユーザーのフォロー一覧を取得できる() {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    User mockFollowee1 = new User();
    mockFollowee1.setUserId("followee1");
    mockFollowee1.setDomain("example.com");

    User mockFollowee2 = new User();
    mockFollowee2.setUserId("followee2");
    mockFollowee2.setDomain("example.com");

    Follow mockFollow1 = new Follow();
    mockFollow1.setFollowerId(mockUser.getUserId());
    mockFollow1.setFollowerDomain(mockUser.getDomain());
    mockFollow1.setFolloweeId("followee1");
    mockFollow1.setFolloweeDomain("example.com");

    Follow mockFollow2 = new Follow();
    mockFollow2.setFollowerId(mockUser.getUserId());
    mockFollow2.setFollowerDomain(mockUser.getDomain());
    mockFollow2.setFolloweeId("followee2");
    mockFollow2.setFolloweeDomain("example.com");

    List<Follow> mockFollows = Arrays.asList(mockFollow1, mockFollow2);

    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(Optional.of(mockUser));
    when(userRepository.findByUserIdAndDomain("followee1", "example.com")).thenReturn(Optional.of(mockFollowee1));
    when(userRepository.findByUserIdAndDomain("followee2", "example.com")).thenReturn(Optional.of(mockFollowee2));
    when(followRepository.findFollowing(mockUser.getUserId(), mockUser.getDomain())).thenReturn(mockFollows);

    // テスト実行
    List<UserDto> result = followListService.getFollowing(mockUser.getUserId());

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("followee1", result.get(0).getUserId());
    assertEquals("followee2", result.get(1).getUserId());
  }

  @Test
  void ユーザーのフォロワー一覧を取得できる() {
    // モックの準備
    User mockUser = new User();
    mockUser.setUserId("test_user");
    mockUser.setDomain("example.com");

    User mockFollower1 = new User();
    mockFollower1.setUserId("follower1");
    mockFollower1.setDomain("example.com");

    User mockFollower2 = new User();
    mockFollower2.setUserId("follower2");
    mockFollower2.setDomain("example.com");

    Follow mockFollow1 = new Follow();
    mockFollow1.setFollowerId("follower1");
    mockFollow1.setFollowerDomain("example.com");
    mockFollow1.setFolloweeId(mockUser.getUserId());
    mockFollow1.setFolloweeDomain(mockUser.getDomain());

    Follow mockFollow2 = new Follow();
    mockFollow2.setFollowerId("follower2");
    mockFollow2.setFollowerDomain("example.com");
    mockFollow2.setFolloweeId(mockUser.getUserId());
    mockFollow2.setFolloweeDomain(mockUser.getDomain());

    List<Follow> mockFollows = Arrays.asList(mockFollow1, mockFollow2);

    when(userRepository.findByUserIdAndDomain(mockUser.getUserId(), mockUser.getDomain())).thenReturn(Optional.of(mockUser));
    when(userRepository.findByUserIdAndDomain("follower1", "example.com")).thenReturn(Optional.of(mockFollower1));
    when(userRepository.findByUserIdAndDomain("follower2", "example.com")).thenReturn(Optional.of(mockFollower2));
    when(followRepository.findFollowers(mockUser.getUserId(), mockUser.getDomain())).thenReturn(mockFollows);

    // テスト実行
    List<UserDto> result = followListService.getFollowers(mockUser.getUserId());

    // 検証
    assertThat(result).hasSize(2);
    assertEquals("follower1", result.get(0).getUserId());
    assertEquals("follower2", result.get(1).getUserId());
  }
}
