package com.example.demo.domain.Auth;

import io.swagger.v3.oas.annotations.media.Schema;

public class JwtToken {
  @Schema(description = "トークン", example = "$2a$12$PTFZW06XLYYrjGXQkRv14.F.hO1GRy.79pvMauUV0Clc6cSsquuOu")
  private String token;

  public JwtToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
