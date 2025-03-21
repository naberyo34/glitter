package com.example.glitter.domain.User;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.glitter.generated.User;
import com.example.glitter.generated.UserDynamicSqlSupport;
import com.example.glitter.generated.UserMapper;

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

  @Transactional
  public Optional<User> findByEmail(String email) {
    return userMapper.selectOne((c) -> c.where(UserDynamicSqlSupport.email, isEqualTo(email)));
  }

  /**
   * セッションユーザーを取得する
   * @return セッションユーザー (存在しない場合は null)
   */
  @Transactional
  public Optional<User> getSessionUser() {
    return userMapper.selectByPrimaryKey(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  /**
   * ユーザーを追加する
   * @param user
   */
  @Transactional
  public User insert(User user) {
    userMapper.insert(user);
    return user;
  }
}
