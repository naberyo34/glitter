package com.example.glitter.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * JWT 認証のテスト用セキュリティコンテキストファクトリ
 */
public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {
  @Override
  public SecurityContext createSecurityContext(WithMockJwt annotation) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", annotation.sub());

    Jwt jwt = new Jwt(
        "dummy-token",
        Instant.now(),
        Instant.now().plusSeconds(60 * 60),
        // ダミー署名
        Map.of("dummy-algorithm", "none"),
        claims);
    // 権限は今のところ利用していないため空のリストを渡す
    List<GrantedAuthority> authorities = List.of();
    JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);

    return context;
  }
}
