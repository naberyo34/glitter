package com.example.demo.domain.Auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
  @Autowired
  private JwtEncoder encoder;
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public Optional<JwtTokenDto> generateToken(UserIdentity identity) {
    try {
      // provider を作る
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(userDetailsService);
      provider.setPasswordEncoder(passwordEncoder);

      // 受け取ったユーザー情報を使って認証
      Authentication authentication = provider
          .authenticate(new UsernamePasswordAuthenticationToken(identity.getId(), identity.getPassword()));
      // 認証情報からトークンを生成
      Instant now = Instant.now();
      String scope = authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining(" "));
      JwtClaimsSet claims = JwtClaimsSet.builder()
          .issuer("self")
          .issuedAt(now)
          .expiresAt(now.plus(1, ChronoUnit.HOURS))
          .subject(authentication.getName())
          .claim("scope", scope)
          .build();
      String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
      return Optional.of(new JwtTokenDto(token));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
