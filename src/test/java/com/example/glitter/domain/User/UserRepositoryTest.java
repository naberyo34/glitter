package com.example.glitter.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.generated.User;
import com.example.glitter.util.WithMockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserRepositoryTest {
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
  private UserRepository userRepository;

  @Test
  void IDからユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.findByUserIdAndDomain("test_user", "example.com");
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals("テストユーザー", u.getUsername());
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    Optional<User> user = userRepository.findByUserIdAndDomain("not_exist_user", "example.com");
    assertThat(user).isEmpty();
  }

  @Test
  @WithMockJwt
  void ログイン中にセッションユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals("テストユーザー", u.getUsername());
    });
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したときemptyが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isEmpty();
  }

  @Test
  void ユーザーを更新できる() throws Exception {
    try {
      User user = userRepository.findByUserIdAndDomain("test_user", "example.com").orElseThrow();
      user.setUsername("更新されたユーザー");
      userRepository.update(user);

      User updatedUser = userRepository.findByUserIdAndDomain("test_user", "example.com").orElseThrow();
      assertEquals("更新されたユーザー", updatedUser.getUsername());
    } catch (Exception e) {
      fail(e);
    }
  }
}
