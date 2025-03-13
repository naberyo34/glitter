package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Auth.TokenService;
import com.example.demo.domain.Auth.UserIdentity;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final TokenService tokenService;

  public AuthController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @PostMapping("/token")
  public String token(@RequestBody UserIdentity identity) {
    String token = tokenService.generateToken(identity);
    return token;
  }
}
