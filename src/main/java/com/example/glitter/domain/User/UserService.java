package com.example.glitter.domain.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.glitter.generated.User;

import jakarta.validation.Valid;

@Service
@Validated
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * ID からユーザー Summary を取得する
   * 
   * @param userId
   * @return 合致するユーザー Summary (存在しない場合は null)
   */
  public Optional<UserSummaryDto> findById(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(UserSummaryDto::fromEntity);
  }

  /**
   * ユーザーの総数を取得する
   * 
   * @return ユーザーの総数
   */
  public long countAll() {
    return userRepository.countAll();
  }

  /**
   * セッションユーザー Summary を取得する
   * 
   * @return セッションユーザー Summary (存在しない場合は null)
   */
  public Optional<UserSummaryDto> getSessionUser() {
    try {
      Optional<User> user = userRepository.getSessionUser();
      return user.map(UserSummaryDto::fromEntity);
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
   * 追加に成功した場合、追加したユーザー Summary を返す
   * 
   * @param userDto
   * @return 追加したユーザー Summary (追加に失敗した場合は null)
   */
  public UserSummaryDto add(@Valid UserDto userDto) throws Exception {
    try {
      User user = userDto.toEntity();
      // パスワードはハッシュ化して保存する
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      User result = userRepository.insert(user);
      return UserSummaryDto.fromEntity(result);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * ユーザーを更新する
   * 更新に成功した場合、更新したユーザー Summary を返す
   * 
   * @param userDto
   * @return 更新したユーザー Summary (更新に失敗した場合は null)
   */
  public UserSummaryDto update(@Valid UserDto userDto) throws Exception {
    try {
      User user = userDto.toEntity();
      // userRepository.updateByPrimaryKey は更新対象のユーザーが存在しない場合新規にユーザーを作ってしまう
      // 更新対象のユーザーが存在しない場合は例外を投げておく
      userRepository.findById(user.getId()).orElseThrow();
      User result = userRepository.update(user);
      return UserSummaryDto.fromEntity(result);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * ユーザー Summary からユーザーを更新する
   * 更新に成功した場合、更新したユーザー Summary を返す
   * 
   * @param userSummaryDto
   * @return
   * @throws Exception
   */
  public UserSummaryDto updateFromSummary(@Valid UserSummaryDto userSummaryDto) throws Exception {
    try {
      // UserSummaryDto には パスワードが含まれないため、一度 User を取得する
      User user = userRepository.findById(userSummaryDto.getId()).orElseThrow();
      user.setUsername(userSummaryDto.getUsername());
      user.setProfile(userSummaryDto.getProfile());
      user.setEmail(userSummaryDto.getEmail());
      user.setIcon(userSummaryDto.getIcon());
      User result = userRepository.update(user);
      return UserSummaryDto.fromEntity(result);
    } catch (Exception e) {
      throw e;
    }
  }
}
