package com.example.glitter.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.generated.User;
import com.example.glitter.util.WithMockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

  private User createNewUser(String id, String email, String sub) {
    User user = new User();
    user.setId(id);
    user.setUsername("新しいユーザー");
    user.setEmail(email);
    user.setSub(sub);
    return user;
  }

  @Test
  void IDからユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.findById("test_user");
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    Optional<User> user = userRepository.findById("not_exist_user");
    assertThat(user).isEmpty();
  }

  @Test
  void メールアドレスからユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.findByEmail("test@example.com");
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  @WithMockJwt
  void ログイン中にセッションユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したときemptyが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isEmpty();
  }

  @Test
  @Transactional
  void 正しいIDとメールアドレスとパスワードを指定して新規のユーザーが作成できる() throws Exception {
    try {
      User newUser = userRepository.insert(createNewUser("new_user", "new@example.com", "new_user_sub"));
      assertEquals(newUser.getUsername(), "新しいユーザー");
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  @Transactional
  void ユーザー作成時にIDが重複しているとき例外が発生する() throws Exception {
    try {
      userRepository.insert(createNewUser("new_user", "new@example.com", "new_user_sub"));
      userRepository.insert(createNewUser("new_user", "unique@example.com", "unique_user_sub"));
      fail();
    } catch (Exception e) {
      assertInstanceOf(DuplicateKeyException.class, e);
    }
  }

  @Test
  @Transactional
  void ユーザー作成時にメールアドレスが重複しているとき例外が発生する() throws Exception {
    try {
      userRepository.insert(createNewUser("new_user", "new@example.com", "new_user_sub"));
      userRepository.insert(createNewUser("unique_user", "new@example.com", "unique_user_sub"));
      fail();
    } catch (Exception e) {
      assertInstanceOf(DuplicateKeyException.class, e);
    }
  }

  @Test
  @Transactional
  void ユーザー作成時にCognitoユーザーの一意のIDが重複しているとき例外が発生する() throws Exception {
    try {
      userRepository.insert(createNewUser("new_user", "new@example.com", "new_user_sub"));
      userRepository.insert(createNewUser("unique_user", "unique@example.com", "new_user_sub"));
      fail();
    } catch (Exception e) {
      assertInstanceOf(DuplicateKeyException.class, e);
    }
  }

  @Test
  @Transactional
  void ユーザー作成時に渡すパラメータが誤っているとき例外が発生する() throws Exception {
    try {
      // ID とメールアドレスが null のユーザーを作成
      User user = new User();
      user.setUsername("新しいユーザー");
      userRepository.insert(user);
      fail();
    } catch (Exception e) {
      assertInstanceOf(DataIntegrityViolationException.class, e);
    }
  }

  @Test
  @Transactional
  void ユーザーを更新できる() throws Exception {
    try {
      User user = userRepository.findById("test_user").orElseThrow();
      user.setUsername("更新されたユーザー");
      userRepository.update(user);

      User updatedUser = userRepository.findById("test_user").orElseThrow();
      assertEquals(updatedUser.getUsername(), "更新されたユーザー");
    } catch (Exception e) {
      fail(e);
    }
  }
}
