package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.domain.Auth.JwtTokenDto;
import com.example.demo.domain.Auth.JwtTokenService;
import com.example.demo.domain.Auth.UserIdentity;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class AuthControllerTest {
  private MockMvc mockMvc;

  @Mock
  private JwtTokenService jwtTokenService;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

    // 正しいユーザー情報を渡したときのみトークンを返すモック
    when(jwtTokenService.generateToken(any(UserIdentity.class)))
        .thenAnswer(invocation -> {
          UserIdentity identity = invocation.getArgument(0);
          if (identity.getId().equals("valid_user") && identity.getPassword().equals("password")) {
            return Optional.of(new JwtTokenDto("valid_token"));
          }
          return Optional.empty();
        });
  }

  @Test
  void 正しいユーザー情報を渡すとトークンを生成できる() throws Exception {
    UserIdentity validUser = new UserIdentity("valid_user", "password");
    ObjectMapper objectMapper = new ObjectMapper();
    mockMvc.perform(post("/auth/token").content(objectMapper.writeValueAsString(validUser))
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andExpect((result) -> {
          String content = result.getResponse().getContentAsString();
          assertEquals(content, "{\"token\":\"valid_token\"}");
        });
  }

  @Test
  void 不正なユーザー情報を渡すと401が返る() throws Exception {
    UserIdentity invalidUser = new UserIdentity("invalid_user", "password");
    ObjectMapper objectMapper = new ObjectMapper();
    mockMvc.perform(post("/auth/token").content(objectMapper.writeValueAsString(invalidUser))
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)).andExpect(status().isUnauthorized());
  }
}
