package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.User.UserDto;
import com.example.demo.domain.User.UserService;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "${env.client-url}")
@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserService userService;

  @Operation(summary = "IDからユーザーを取得", description = "IDからユーザーを取得します。")
  @GetMapping("/{id}")
  public UserDto findById(@PathVariable String id) throws ResponseStatusException {
    return userService.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
  }

  @Operation(summary = "セッションユーザーを取得", description = "セッションユーザーを取得します。")
  @GetMapping("/me")
  public UserDto getSessionUser() throws ResponseStatusException {
    return userService.getSessionUser()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "セッションユーザーが見つかりません"));
  }
}
