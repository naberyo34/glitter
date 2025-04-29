package com.example.glitter.domain.Auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {
  /**
   * ユーザー認証を行い、JWT トークンを取得します。
   *
   * @param identity 認証情報
   * @return JWT トークン
   */
  Optional<JwtTokenDto> login(UserIdentity identity);
}
