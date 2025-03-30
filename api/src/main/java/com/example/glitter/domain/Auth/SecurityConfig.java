package com.example.glitter.domain.Auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

// @see https://www.danvega.dev/blog/spring-security-jwt
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Autowired
  private RsaKeyProperties rsaKeys;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }

  /**
   * セキュリティ設定
   * 
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {
    http.cors((cors) -> cors.configurationSource(corsConfigurationSource()))
        // stateless であれば CSRF 対策は切っても良い
        .csrf((csrf) -> csrf.disable())
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }

  /**
   * CORS 設定
   * 
   * @return
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
    corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
    corsConfiguration.setAllowedOrigins(Arrays.asList("${env.client-url}"));
    UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
    corsSource.registerCorsConfiguration("/**", corsConfiguration);
    return corsSource;
  }
}
