package com.example.glitter.domain.Auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTokenServiceTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void 正しいユーザーIDとメールアドレスを渡したときにトークンが生成される() throws Exception {
    UserIdentity userIdentity = new UserIdentity("test_user", "password");
    Optional<JwtTokenDto> jwtToken = jwtTokenService.generateToken(userIdentity);
    assertThat(jwtToken).isPresent();

    // 別のユーザーでも試してみる
    UserIdentity userIdentity2 = new UserIdentity("test_user_2", "password");
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
    UserIdentity userIdentity = new UserIdentity("invalid_user", "password");
    Optional<JwtTokenDto> invalidJwtToken = jwtTokenService.generateToken(userIdentity);
    assertThat(invalidJwtToken).isEmpty();
  }
}
