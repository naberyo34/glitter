package com.example.glitter.domain.User;

import java.util.Optional;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.glitter.generated.User;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * ID からユーザー DTO を取得する
   * 
   * @param userId
   * @return 合致するユーザー DTO (存在しない場合は null)
   */
  public Optional<UserDto> findById(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * セッションユーザー DTO を取得する
   * 
   * @return セッションユーザー DTO (存在しない場合は null)
   */
  public Optional<UserDto> getSessionUser() {
    try {
      Optional<User> user = userRepository.getSessionUser();
      return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
    } catch (NullPointerException e) {
      // セッションユーザーが存在しない場合ぬるぽが出る、想定済みとして空の Optional を返す
      return Optional.empty();
    } catch (Exception e) {
      // それ以外の例外は throw する
      throw e;
    }
  }

  /**
   * ユーザーを追加する
   * 追加に成功した場合、追加したユーザー DTO を返す
   * 
   * @param user
   * @return 追加したユーザー DTO (追加に失敗した場合は null)
   */
  public Optional<UserDto> add(User user) throws Exception {
    try {
      // バリデーション
      // id が半角英数字+ハイフン+アンダースコアでない場合は失敗
      if (!user.getId().matches("^[a-zA-Z0-9_-]+$")) {
        throw new UserInvalidException("ID is invalid.");
      }
      // ユーザー名が空白の場合は失敗
      if (user.getUsername().isBlank()) {
        throw new UserInvalidException("Username is empty.");
      }
      // email が正規表現に合致しない場合は失敗
      EmailValidator emailValidator = new EmailValidator();
      if (!emailValidator.isValid(user.getEmail(), null)) {
        throw new UserInvalidException("Email is invalid.");
      }
      // パスワードが 6文字以上でない場合は失敗
      if (user.getPassword().length() < 6) {
        throw new UserInvalidException("Password is too short.");
      }
      // パスワードが半角英数字+記号でない場合は失敗
      if (!user.getPassword().matches("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$")) {
        throw new UserInvalidException("Password is invalid.");
      }

      // パスワードをハッシュ化して保存する
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userRepository.insert(user);
    } catch (Exception e) {
      throw e;
    }
    return Optional.of(new UserDto(user.getId(), user.getUsername(), user.getProfile(), user.getEmail()));
  }
}
