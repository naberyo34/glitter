package com.example.glitter.domain.Follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.User.UserSummaryDto;
import com.example.glitter.util.WithMockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FollowServiceTest {

  @Autowired
  private FollowService followService;

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

  @Test
  @Transactional
  @WithMockJwt
  void ユーザーをフォローできる() {
    FollowDto follow = followService.follow("test_user_3");

    assertThat(follow).isNotNull();
    assertEquals("test_user", follow.getFollowerId());
    assertEquals("test_user_3", follow.getFolloweeId());
  }

  @Test
  @Transactional
  @WithMockJwt
  void 自分自身をフォローしようとすると例外が発生する() {
    assertThrows(IllegalArgumentException.class, () -> {
      followService.follow("test_user");
    });
  }

  @Test
  @Transactional
  void 非ログイン状態でフォローしようとすると例外が発生する() {
    assertThrows(AccessDeniedException.class, () -> {
      followService.follow("test_user_2");
    });
  }

  @Test
  @Transactional
  @WithMockJwt
  void 存在しないユーザーをフォローしようとすると例外が発生する() {
    assertThrows(IllegalArgumentException.class, () -> {
      followService.follow("not_exist_user");
    });
  }

  @Test
  @Transactional
  @WithMockJwt
  void ユーザーのフォローを解除できる() {
    boolean result = followService.unfollow("test_user_2");

    assertTrue(result);

    List<UserSummaryDto> following = followService.getMyFollowing();
    assertThat(following).hasSize(1);
    assertEquals("test_user_3", following.get(0).getId());
  }

  @Test
  @Transactional
  void 非ログイン状態でフォロー解除しようとすると例外が発生する() {
    assertThrows(AccessDeniedException.class, () -> {
      followService.unfollow("test_user_2");
    });
  }

  @Test
  @Transactional
  @WithMockJwt
  void ユーザーのフォロー一覧を取得できる() {
    List<UserSummaryDto> myFollowing = followService.getMyFollowing();
    assertThat(myFollowing).hasSize(2);
    assertThat(myFollowing)
        .extracting(UserSummaryDto::getId)
        .containsExactlyInAnyOrder("test_user_2", "test_user_3");

    List<UserSummaryDto> userFollowing = followService.getFollowing("test_user");
    assertThat(userFollowing).hasSize(2);
  }

  @Test
  @Transactional
  @WithMockJwt
  void ユーザーのフォロワー一覧を取得できる() {
    List<UserSummaryDto> followers = followService.getFollowers("test_user");

    assertThat(followers).hasSize(2);
    assertThat(followers)
        .extracting(UserSummaryDto::getId)
        .containsExactlyInAnyOrder("test_user_2", "test_user_3");

    List<UserSummaryDto> myFollowers = followService.getMyFollowers();
    assertThat(myFollowers).hasSize(2);
  }

  @Test
  @Transactional
  void 非ログイン状態で自分のフォロー一覧を取得しようとすると例外が発生する() {
    assertThrows(AccessDeniedException.class, () -> {
      followService.getMyFollowing();
    });
  }

  @Test
  @Transactional
  void 非ログイン状態で自分のフォロワー一覧を取得しようとすると例外が発生する() {
    assertThrows(AccessDeniedException.class, () -> {
      followService.getMyFollowers();
    });
  }

  @Test
  @Transactional
  void 存在しないユーザーのフォロー一覧を取得しようとすると例外が発生する() {
    assertThrows(IllegalArgumentException.class, () -> {
      followService.getFollowing("not_exist_user");
    });
  }

  @Test
  @Transactional
  void 存在しないユーザーのフォロワー一覧を取得しようとすると例外が発生する() {
    assertThrows(IllegalArgumentException.class, () -> {
      followService.getFollowers("not_exist_user");
    });
  }
}
