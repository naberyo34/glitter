package com.example.demo.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.generated.User;

@SpringBootTest
public class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;

  private User setNewUser(String id, String email) {
    User user = new User();
    user.setId(id);
    user.setUsername("新しいユーザー");
    user.setEmail(email);
    user.setPassword("password");
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
  @WithMockUser(username = "test_user")
  void ログイン中にセッションユーザーを取得したときそのユーザーが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isPresent().get().isInstanceOf(User.class);

    // 取得ユーザーが正しいことも確認しておく
    user.ifPresent((u) -> {
      assertEquals(u.getUsername(), "テストユーザー");
    });
  }

  @Test
  @WithMockUser(username = "not_exist_user")
  void 不正なユーザーがセッションユーザーを取得したときemptyが返る() throws Exception {
    Optional<User> user = userRepository.getSessionUser();
    assertThat(user).isEmpty();
  }

  @Test
  @Transactional
  void 正しいIDとメールアドレスとパスワードを指定して新規のユーザーが作成できる() throws Exception {
    User user = setNewUser("new_user", "new@example.com");
    userRepository.insert(user);

    // 追加されたことを確認
    Optional<User> newUser = userRepository.findById("new_user");
    assertThat(newUser).isNotEmpty();

    // 取得ユーザーが正しいことも確認しておく
    newUser.ifPresent((u) -> {
      assertEquals(u.getUsername(), "新しいユーザー");
    });
  }

  @Test
  @Transactional
  void ユーザー作成時にIDが重複しているとき例外が発生する() throws Exception {
    User user = setNewUser("new_user", "new@example.com");
    userRepository.insert(user);

    try {
      User user2 = setNewUser("new_user", "unique@example.com");
      userRepository.insert(user2);
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(org.springframework.dao.DuplicateKeyException.class);
    }

    // 追加されていないことを確認
    Optional<User> newUser = userRepository.findByEmail("unique@example.com");
    assertThat(newUser).isEmpty();
  }

  @Test
  @Transactional
  void ユーザー作成時にメールアドレスが重複しているとき例外が発生する() throws Exception {
    User user = setNewUser("new_user", "new@example.com");
    userRepository.insert(user);

    try {
      User user2 = setNewUser("unique_user", "new@example.com");
      userRepository.insert(user2);
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(org.springframework.dao.DuplicateKeyException.class);
    }

    // 追加されていないことを確認
    Optional<User> newUser = userRepository.findById("unique_user");
    assertThat(newUser).isEmpty();
  }

  @Test
  @Transactional
  void ユーザー作成時に渡すパラメータが誤っているとき例外が発生する() throws Exception {
    // ID とメールアドレスが null のユーザーを作成
    User user = new User();
    user.setUsername("新しいユーザー");
    user.setPassword("password");

    try {
      userRepository.insert(user);
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
  }
}
