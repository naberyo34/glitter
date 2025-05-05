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
   * ユーザー ID とドメインからユーザーを取得する
   * 
   * @param userId
   * @param userDomain
   * @return
   */
  public Optional<User> findByUserIdAndDomain(String userId, String userDomain) {
    return userMapper.selectOne(c -> c.where(UserDynamicSqlSupport.userId, isEqualTo(userId))
        .and(UserDynamicSqlSupport.domain, isEqualTo(userDomain)));
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

  /**
   * セッションユーザーを取得する
   * 
   * @return セッションユーザー (非ログイン時は empty)
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
   * ユーザーの総数を取得する
   * NodeInfo で使っている
   * 
   * @return ユーザーの総数
   */
  public long countAll() {
    return userMapper.count((c) -> c);
  }
}
