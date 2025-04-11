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

  /**
   * メールアドレスからユーザーを取得する
   * 
   * @param email
   * @return 合致するユーザー (存在しない場合は null)
   */
  @Transactional
  public Optional<User> findByEmail(String email) {
    return userMapper.selectOne((c) -> c.where(UserDynamicSqlSupport.email, isEqualTo(email)));
  }

  /**
   * ユーザーの総数を取得する
   * 
   * @return ユーザーの総数
   */
  @Transactional
  public long countAll() {
    return userMapper.count((c) -> c);
  }

  /**
   * セッションユーザーを取得する
   * 
   * @return セッションユーザー (存在しない場合は null)
   */
  @Transactional
  public Optional<User> getSessionUser() {
    return userMapper.selectByPrimaryKey(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  /**
   * ユーザーを追加する
   * 
   * @param user
   */
  @Transactional
  public User insert(User user) {
    userMapper.insert(user);
    return user;
  }

  /**
   * ユーザーを更新する
   * 
   * @param user
   */
  @Transactional
  public User update(User user) {
    userMapper.updateByPrimaryKey(user);
    return user;
  }
}
