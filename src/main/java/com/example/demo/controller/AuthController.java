package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Auth.JwtToken;
import com.example.demo.domain.Auth.TokenService;
import com.example.demo.domain.Auth.UserIdentity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private TokenService tokenService;

  @Operation(summary = "ログイン JWT トークンの取得", description = "ユーザー ID とパスワードを照合し、ログインに成功した場合は JWT トークンを返却します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))
      }),
      @ApiResponse(responseCode = "401", description = "認証に失敗した場合", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @PostMapping("/token")
  public JwtToken token(@RequestBody UserIdentity identity) {
    return new JwtToken(tokenService.generateToken(identity));
  }
}
