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
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserService;
import com.example.glitter.generated.User;

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

  @Autowired
  private UserService userService;

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    Optional<UserDto> user = userService.findById("test_user");
    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserDto.class);
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    Optional<UserDto> user = userService.findById("not_exist_user");
    assertThat(user).isEmpty();
  }

  @Test
  @WithMockUser(username = "test_user")
  void ログイン中にセッションユーザーを取得したときユーザーDTOが返る() throws Exception {
    Optional<UserDto> user = userService.getSessionUser();
    // User ではなく UserDto が返っていることを確認
    assertThat(user).isPresent().get().isInstanceOf(UserDto.class);
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したときemptyが返る() throws Exception {
    Optional<UserDto> user = userService.getSessionUser();
    assertThat(user).isEmpty();
  }

  @Test
  void ユーザーを追加できる() throws Exception {
    User user = new User();
    user.setId("new_user");
    user.setUsername("新規ユーザー");
    user.setPassword("password");
    user.setEmail("new@example.com");

    Optional<UserDto> newUserDto = userService.add(user);

    assertThat(newUserDto).isPresent();
    newUserDto.ifPresent((u) -> {
      assertEquals(u.getUsername(), "新規ユーザー");
    });
  }

  @Test
  void IDが重複する場合ユーザー追加に失敗する() throws Exception {
    User user = new User();
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
  void IDが不正な形式の場合ユーザー追加に失敗する() throws Exception {
    User user = new User();
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
  void ユーザー名が空白のときユーザー追加に失敗する() throws Exception {
    User user = new User();
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
  void パスワードが6文字未満のときユーザー追加に失敗する() throws Exception {
    User user = new User();
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
  void メールアドレスが重複する場合ユーザー追加に失敗する() throws Exception {
    User user = new User();
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
  void メールアドレスが不正な形式のときユーザー追加に失敗する() throws Exception {
    User user = new User();
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
}
