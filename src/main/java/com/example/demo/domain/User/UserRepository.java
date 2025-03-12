package com.example.demo.domain.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.generated.User;
import com.example.demo.generated.UserMapper;

@Repository
public class UserRepository {
  @Autowired
  private UserMapper userMapper;

  /**
   * ID からユーザーを取得する
   * 
   * @param id
   * @return 合致するユーザー (存在しない場合は null)
   */
  @Transactional
  public Optional<User> findById(String id) {
    return userMapper.selectByPrimaryKey(id);
  }

  /**
   * セッションユーザーを取得する
   * @return セッションユーザー (存在しない場合は null)
   */
  @Transactional
  public Optional<User> getSessionUser() {
    return userMapper.selectByPrimaryKey(SecurityContextHolder.getContext().getAuthentication().getName());
  }
}
