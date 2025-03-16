package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.User.UserDto;
import com.example.demo.domain.User.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserService userService;

  @Operation(summary = "IDからユーザーを取得", description = "IDからユーザーを取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}")
  public UserDto findById(@PathVariable String id) throws ErrorResponseException {
    return userService.findById(id)
        .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "ユーザーの投稿を取得", description = "ユーザーの投稿を取得します。ユーザー自体が存在しない場合は404、ユーザーが1件も投稿を持たない場合は空配列を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class))),
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}/post")
  public List<PostDto> getUserPosts(@PathVariable String id) throws ErrorResponseException {
    // ユーザーの存在判定
    userService.findById(id).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    return userService.getUserPosts(id);
  }

  @Operation(summary = "セッションユーザーを取得", description = "セッションユーザーを取得します。ログインしていない場合は 401 が返ります。通常発生しませんが、ログインしているにもかかわらずユーザーのデータが見つからない場合、404 ではなく null を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
      }),
      @ApiResponse(responseCode = "401", description = "ログインしていないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public UserDto getSessionUser() throws ErrorResponseException {
    return userService.getSessionUser()
        .orElse(null);
  }
}
