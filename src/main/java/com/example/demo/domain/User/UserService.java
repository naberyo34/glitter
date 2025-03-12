package com.example.demo.domain.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.generated.User;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  /**
   * ID からユーザー DTO を取得する
   * 
   * @param id
   * @return 合致するユーザー DTO  (存在しない場合は null)
   */
  @Transactional
  public Optional<UserDto> findById(String id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * セッションユーザー DTO を取得する
   * @return セッションユーザー DTO  (存在しない場合は null)
   */
  @Transactional
  public Optional<UserDto> getSessionUser() {
    Optional<User> user = userRepository.getSessionUser();
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }
}
