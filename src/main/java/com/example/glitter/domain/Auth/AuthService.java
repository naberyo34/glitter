package com.example.glitter.domain.Auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  @Autowired
  private CognitoService cognitoService;

  /**
   * ユーザー認証を行い、JWT トークンを取得します。
   *
   * @param identity 認証情報
   * @return JWT トークン
   */
  public Optional<JwtTokenDto> login(UserIdentity identity) {
    return cognitoService.login(identity);
  }
}
