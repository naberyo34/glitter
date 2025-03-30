package com.example.glitter.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
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

  // User 型のユーザー情報を直で取得するために必要
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    Optional<UserSummaryDto> user = userService.findById("test_user");

    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserSummaryDto.class);
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    Optional<UserSummaryDto> user = userService.findById("not_exist_user");
    assertThat(user).isEmpty();
  }

  @Test
  @WithMockUser(username = "test_user")
  void ログイン中にセッションユーザーを取得したときユーザーDTOが返る() throws Exception {
    Optional<UserSummaryDto> user = userService.getSessionUser();

    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserSummaryDto.class);
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したときemptyが返る() throws Exception {
    Optional<UserSummaryDto> user = userService.getSessionUser();
    assertThat(user).isEmpty();
  }

  @Test
  @Transactional
  void ユーザーを追加できる() throws Exception {
    UserDto user = new UserDto();
    user.setId("new_user");
    user.setUsername("新規ユーザー");
    user.setPassword("password");
    user.setEmail("new@example.com");

    UserSummaryDto result = userService.add(user);
    assertNotNull(result);
    assertEquals(result.getUsername(), "新規ユーザー");
  }

  @Test
  @Transactional
  void IDが重複する場合ユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("test_user");
    user.setUsername("ID重複ユーザー");
    user.setPassword("password");
    user.setEmail("new@example.com");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void IDが不正な形式の場合ユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("$%&*");
    user.setUsername("IDが不正なユーザー");
    user.setPassword("password");
    user.setEmail("invalid_id@example.com");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void ユーザー名が空白のときユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("no_username_user");
    user.setUsername(" ");
    user.setPassword("password");
    user.setEmail("nousername@example.com");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void パスワードが6文字未満のときユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("short_pass_user");
    user.setUsername("パスワードが短いユーザー");
    user.setPassword("12345");
    user.setEmail("shortpass@example.com");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void メールアドレスが重複する場合ユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("new_user");
    user.setUsername("メールアドレス重複ユーザー");
    user.setPassword("password");
    user.setEmail("test@example.com");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void メールアドレスが不正な形式のときユーザー追加に失敗する() throws Exception {
    UserDto user = new UserDto();
    user.setId("invalid_email_user");
    user.setUsername("メールアドレスが不正なユーザー");
    user.setPassword("password");
    user.setEmail("invalid_email");

    try {
      userService.add(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void ユーザーを更新できる() throws Exception {
    UserDto user = UserDto.fromEntity(userRepository.findById("test_user").orElseThrow());
    user.setUsername("更新されたユーザー");

    UserSummaryDto result = userService.update(user);
    assertNotNull(result);
    assertEquals(result.getUsername(), "更新されたユーザー");
  }

  @Test
  @Transactional
  void 不正なパラメータでユーザーを更新しようとしたとき例外が発生する() throws Exception {
    UserDto user = UserDto.fromEntity(userRepository.findById("test_user").orElseThrow());
    // 空白のユーザー名は invalid である
    user.setUsername("");

    try {
      userService.update(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void 存在しないユーザーを更新しようとしたとき例外が発生する() throws Exception {
    // 内容自体は valid な User を作る
    UserDto user = new UserDto();
    user.setId("not_exist_user");
    user.setPassword("password");
    user.setUsername("存在しないユーザー");
    user.setEmail("not_exist@example.com");

    try {
      userService.update(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void UserSummaryからユーザーを更新できる() throws Exception {
    UserSummaryDto user = userService.findById("test_user").orElseThrow();
    user.setUsername("更新されたユーザー");

    UserSummaryDto result = userService.updateFromSummary(user);
    assertNotNull(result);
    assertEquals(result.getUsername(), "更新されたユーザー");
  }

  @Test
  @Transactional
  void 不正なパラメータでUserSummaryからユーザーを更新しようとしたとき例外が発生する() throws Exception {
    UserSummaryDto user = userService.findById("test_user").orElseThrow();
    // 空白のユーザー名は invalid である
    user.setUsername("");

    try {
      userService.updateFromSummary(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  @Transactional
  void 存在しないユーザーをUserSummaryから更新しようとしたとき例外が発生する() throws Exception {
    // 内容自体は valid な UserSummary を作る
    UserSummaryDto user = new UserSummaryDto();
    user.setId("not_exist_user");
    user.setUsername("存在しないユーザー");
    user.setEmail("not_exist@example.com");

    try {
      userService.updateFromSummary(user);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
