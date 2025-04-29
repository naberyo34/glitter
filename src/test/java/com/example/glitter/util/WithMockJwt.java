package com.example.glitter.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * JWT 認証のテスト用セキュリティコンテキストアノテーション
 * @ WithMockJwt で認証を必要とする機能のテストが可能です。
 * @see WithMockJwtSecurityContextFactory
 */
@WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMockJwt {
  String sub() default "test_user_sub";
}
