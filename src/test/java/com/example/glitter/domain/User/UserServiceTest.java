package com.example.glitter.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.glitter.generated.User;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");
    mockUser.setProfile("テスト用のアカウントです。");

    when(userRepository.findById("test_user")).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<UserSummaryDto> user = userService.findById("test_user");

    // 検証
    assertThat(user).isPresent().get().isInstanceOf(UserSummaryDto.class);
    user.ifPresent((u) -> {
      assertEquals("テストユーザー", u.getUsername());
    });
  }

  @Test
  void 存在しないユーザーを取得したときemptyが返る() throws Exception {
    // モックの準備
    when(userRepository.findById("not_exist_user")).thenReturn(Optional.empty());

    // テスト実行
    Optional<UserSummaryDto> user = userService.findById("not_exist_user");

    // 検証
    assertThat(user).isEmpty();
  }

  @Test
  void ログイン中にセッションユーザーを取得したときユーザーDTOが返る() throws Exception {
    // モックの準備
    User mockUser = new User();
    mockUser.setId("test_user");
    mockUser.setUsername("テストユーザー");
    mockUser.setProfile("テスト用のアカウントです。");

    when(userRepository.getSessionUser()).thenReturn(Optional.of(mockUser));

    // テスト実行
    Optional<UserSummaryDto> user = userService.getSessionUser();

    // 検証
    assertThat(user).isPresent().get().isInstanceOf(UserSummaryDto.class);
    user.ifPresent((u) -> {
      assertEquals("テストユーザー", u.getUsername());
    });
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したときemptyが返る() throws Exception {
    // モックの準備 - NullPointerExceptionをスローする動作をモック
    when(userRepository.getSessionUser()).thenThrow(new NullPointerException());

    // テスト実行
    Optional<UserSummaryDto> user = userService.getSessionUser();

    // 検証
    assertThat(user).isEmpty();
  }

  @Test
  void ユーザーを更新できる() throws Exception {
    // モックの準備
    User originalUser = new User();
    originalUser.setId("test_user");
    originalUser.setUsername("テストユーザー");
    originalUser.setEmail("test@example.com");

    User updatedUser = new User();
    updatedUser.setId("test_user");
    updatedUser.setUsername("更新されたユーザー");
    updatedUser.setEmail("test@example.com");

    when(userRepository.findById("test_user")).thenReturn(Optional.of(originalUser));
    when(userRepository.update(any(User.class))).thenReturn(updatedUser);

    // テスト用DTOの作成
    UserDto userDto = new UserDto();
    userDto.setId("test_user");
    userDto.setUsername("更新されたユーザー");
    userDto.setEmail("test@example.com");

    // テスト実行
    UserSummaryDto result = userService.update(userDto);

    // 検証
    assertNotNull(result);
    assertEquals("更新されたユーザー", result.getUsername());
  }

  @Test
  void 不正なパラメータでユーザーを更新しようとしたとき例外が発生する() throws Exception {
    // モックの準備
    User originalUser = new User();
    originalUser.setId("test_user");
    originalUser.setUsername("テストユーザー");
    originalUser.setEmail("test@example.com");

    when(userRepository.findById("test_user")).thenReturn(Optional.of(originalUser));

    UserDto userDto = new UserDto();
    userDto.setId("test_user");
    userDto.setUsername(""); // 空文字はバリデーションエラー
    userDto.setEmail("test@example.com");

    doThrow(new ConstraintViolationException("バリデーションエラー", null))
        .when(userRepository).update(any(User.class));

    // テスト実行と検証
    try {
      userService.update(userDto);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  void 存在しないユーザーを更新しようとしたとき例外が発生する() throws Exception {
    // テスト用DTOの作成
    UserDto userDto = new UserDto();
    userDto.setId("not_exist_user");
    userDto.setUsername("存在しないユーザー");
    userDto.setEmail("not_exist@example.com");

    // テスト実行と検証
    try {
      userService.update(userDto);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  void UserSummaryからユーザーを更新できる() throws Exception {
    // モックの準備
    User originalUser = new User();
    originalUser.setId("test_user");
    originalUser.setUsername("テストユーザー");
    originalUser.setEmail("test@example.com");

    User updatedUser = new User();
    updatedUser.setId("test_user");
    updatedUser.setUsername("更新されたユーザー");
    updatedUser.setEmail("test@example.com");

    when(userRepository.findById("test_user")).thenReturn(Optional.of(originalUser));
    when(userRepository.update(any(User.class))).thenReturn(updatedUser);

    // テスト用DTOの作成
    UserSummaryDto userSummaryDto = new UserSummaryDto();
    userSummaryDto.setId("test_user");
    userSummaryDto.setUsername("更新されたユーザー");

    // テスト実行
    UserSummaryDto result = userService.updateFromSummary(userSummaryDto);

    // 検証
    assertNotNull(result);
    assertEquals("更新されたユーザー", result.getUsername());
    verify(userRepository).update(any(User.class));
  }

  @Test
  void 不正なパラメータでUserSummaryからユーザーを更新しようとしたとき例外が発生する() throws Exception {
    // モックの準備
    User originalUser = new User();
    originalUser.setId("test_user");
    originalUser.setUsername("テストユーザー");

    when(userRepository.findById("test_user")).thenReturn(Optional.of(originalUser));

    UserSummaryDto userSummaryDto = new UserSummaryDto();
    userSummaryDto.setId("test_user");
    userSummaryDto.setUsername(""); // 空文字はバリデーションエラー

    doThrow(new ConstraintViolationException("バリデーションエラー", null))
        .when(userRepository).update(any(User.class));

    // テスト実行と検証
    try {
      userService.updateFromSummary(userSummaryDto);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  void 存在しないユーザーをUserSummaryから更新しようとしたとき例外が発生する() throws Exception {
    // モックの準備
    when(userRepository.findById("not_exist_user")).thenReturn(Optional.empty());

    // テスト用DTOの作成
    UserSummaryDto userSummaryDto = new UserSummaryDto();
    userSummaryDto.setId("not_exist_user");
    userSummaryDto.setUsername("存在しないユーザー");

    // テスト実行と検証
    try {
      userService.updateFromSummary(userSummaryDto);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
