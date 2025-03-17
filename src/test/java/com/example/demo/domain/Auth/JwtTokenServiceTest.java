package com.example.demo.domain.Auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTokenServiceTest {
  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void 正しいユーザーIDとメールアドレスを渡したときにトークンが生成される() throws Exception {
    UserIdentity userIdentity = new UserIdentity("test_user", "test");
    Optional<JwtTokenDto> jwtToken = jwtTokenService.generateToken(userIdentity);
    assertThat(jwtToken).isPresent();

    // 別のユーザーでも試してみる
    UserIdentity userIdentity2 = new UserIdentity("test_user_2", "test");
    Optional<JwtTokenDto> jwtToken2 = jwtTokenService.generateToken(userIdentity2);
    assertThat(jwtToken2).isPresent();
  };

  @Test
  void 不正なパスワードを渡したときにトークン生成が失敗する() throws Exception {
    UserIdentity userIdentity = new UserIdentity("test_user", "invalid_password");
    Optional<JwtTokenDto> invalidJwtToken = jwtTokenService.generateToken(userIdentity);
    assertThat(invalidJwtToken).isEmpty();
  }

  @Test
  void 存在しないユーザーを渡したときにトークン生成が失敗する() throws Exception {
    UserIdentity userIdentity = new UserIdentity("invalid_user", "test");
    Optional<JwtTokenDto> invalidJwtToken = jwtTokenService.generateToken(userIdentity);
    assertThat(invalidJwtToken).isEmpty();
  }
}
