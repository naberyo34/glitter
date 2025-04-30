package com.example.glitter.domain.User;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;

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
  public Optional<User> findById(String id) {
    return userMapper.selectByPrimaryKey(id);
  }

  /**
   * メールアドレスからユーザーを取得する
   * 
   * @param email
   * @return 合致するユーザー (存在しない場合は null)
   */
  public Optional<User> findByEmail(String email) {
    return userMapper.selectOne((c) -> c.where(UserDynamicSqlSupport.email, isEqualTo(email)));
  }

  /**
   * ユーザーの総数を取得する
   * 
   * @return ユーザーの総数
   */
  public long countAll() {
    return userMapper.count((c) -> c);
  }

  /**
   * セッションユーザーを取得する
   * 
   * @return セッションユーザー (存在しない場合は null)
   */
  public Optional<User> getSessionUser() {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (principal instanceof Jwt jwt) {
        String sub = jwt.getClaimAsString("sub");
        return userMapper.selectOne((c) -> c.where(UserDynamicSqlSupport.sub, isEqualTo(sub)));
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * ユーザーを追加する
   * 
   * @param user
   */
  public User insert(User user) {
    userMapper.insert(user);
    return user;
  }

  /**
   * ユーザーを更新する
   * 
   * @param user
   */
  public User update(User user) {
    userMapper.updateByPrimaryKey(user);
    return user;
  }
}
