package com.example.glitter.domain.Follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.generated.Follow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class FollowRepositoryTest {

  @Autowired
  private FollowRepository followRepository;

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
  void フォロー情報を追加できる() {
    Follow follow = new Follow();
    follow.setFollowerId("test_user_2");
    follow.setFollowerDomain("example.com");
    follow.setFolloweeId("test_user_3");
    follow.setFolloweeDomain("example.com");

    Follow result = followRepository.insert(follow);

    assertThat(result).isNotNull();
    assertEquals("test_user_2", result.getFollowerId());
    assertEquals("test_user_3", result.getFolloweeId());
  }

  @Test
  void ユーザーのフォロー一覧を取得できる() {
    List<Follow> follows = followRepository.findFollowing("test_user", "example.com");

    assertThat(follows).hasSize(2);
    assertThat(follows)
        .extracting(Follow::getFolloweeId)
        .containsExactlyInAnyOrder("test_user_2", "test_user_3");
  }

  @Test
  void ユーザーのフォロワー一覧を取得できる() {
    List<Follow> followers = followRepository.findFollowers("test_user", "example.com");

    assertThat(followers).hasSize(2);
    assertThat(followers)
        .extracting(Follow::getFollowerId)
        .containsExactlyInAnyOrder("test_user_2", "test_user_3");
  }

  @Test
  void フォロー関係を削除できる() {
    int deleted = followRepository.delete("test_user", "example.com", "test_user_2", "example.com");

    assertEquals(1, deleted);

    Optional<Follow> follow = followRepository.findFollowRelation("test_user", "example.com", "test_user_2",
        "example.com");
    assertTrue(follow.isEmpty());
  }

  @Test
  void 特定のフォロー関係を検索できる() {
    Optional<Follow> found = followRepository.findFollowRelation("test_user", "example.com", "test_user_2",
        "example.com");

    assertTrue(found.isPresent());
    assertEquals("test_user", found.get().getFollowerId());
    assertEquals("test_user_2", found.get().getFolloweeId());

    Optional<Follow> notFound = followRepository.findFollowRelation("test_user", "example.com", "not_exist",
        "example.com");
    assertTrue(notFound.isEmpty());
  }
}
