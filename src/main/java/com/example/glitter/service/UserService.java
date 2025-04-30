package com.example.glitter.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.User;

import jakarta.validation.Valid;

@Service
@Validated
@Transactional
public class UserService {
  @Autowired
  private UserRepository userRepository;

  /**
   * ID からユーザー Response を取得する
   * 
   * @param userId
   * @return 合致するユーザー Response (存在しない場合は null)
   */
  public Optional<UserResponse> findById(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(UserResponse::fromEntity);
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
   * セッションユーザー Response を取得する
   * 
   * @return セッションユーザー Response (存在しない場合は null)
   */
  public Optional<UserResponse> getSessionUser() {
    try {
      Optional<User> user = userRepository.getSessionUser();
      return user.map(UserResponse::fromEntity);
    } catch (NullPointerException e) {
      // セッションユーザーが存在しない場合ぬるぽが出る、想定済みとして空の Optional を返す
      return Optional.empty();
    } catch (Exception e) {
      // それ以外の例外は throw する
      throw e;
    }
  }

  /**
   * ユーザーを更新する
   * 更新に成功した場合、更新したユーザー Response を返す
   * 
   * @param userDto
   * @return 更新したユーザー Response (更新に失敗した場合は null)
   */
  public UserResponse update(@Valid UserDto userDto) throws Exception {
    try {
      User user = userDto.toEntity();
      // userRepository.updateByPrimaryKey は更新対象のユーザーが存在しない場合新規にユーザーを作ってしまう
      // 更新対象のユーザーが存在しない場合は例外を投げておく
      userRepository.findById(user.getId()).orElseThrow();
      User result = userRepository.update(user);
      return UserResponse.fromEntity(result);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * ユーザー Response からユーザーを更新する
   * 更新に成功した場合、更新したユーザー Response を返す
   * 
   * @param userResponse
   * @return
   * @throws Exception
   */
  public UserResponse updateFromSummary(@Valid UserResponse userResponse) throws Exception {
    try {
      // userResponse には パスワードが含まれないため、一度 User を取得する
      User user = userRepository.findById(userResponse.getId()).orElseThrow();
      user.setUsername(userResponse.getUsername());
      user.setProfile(userResponse.getProfile());
      user.setIcon(userResponse.getIcon());
      User result = userRepository.update(user);
      return UserResponse.fromEntity(result);
    } catch (Exception e) {
      throw e;
    }
  }
}
