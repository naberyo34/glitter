package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostParamsDto;
import com.example.glitter.domain.Post.PostService;
import com.example.glitter.domain.User.UserSummaryDto;
import com.example.glitter.domain.User.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/post")
public class PostController {
  @Autowired
  private PostService postService;
  @Autowired
  private UserService userService;

  @Operation(summary = "投稿の追加", description = "セッションユーザーの新規投稿を追加します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))
      }),
      @ApiResponse(responseCode = "401", description = "認証に失敗したかセッションユーザーを取得できなかった場合", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "500", description = "何らかの理由で投稿に失敗した場合", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
  })
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public PostDto addPost(@RequestBody String content) throws ErrorResponseException {
    try {
      UserSummaryDto sessionUser = userService.getSessionUser()
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
      PostParamsDto postParamsDto = new PostParamsDto(sessionUser.getId(), content);
      return postService.add(postParamsDto)
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR));
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
