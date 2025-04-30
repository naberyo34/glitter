package com.example.glitter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.ActivityPub.ActivityPubService;
import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostParamsDto;
import com.example.glitter.domain.Post.PostRequestDto;
import com.example.glitter.domain.Post.PostService;
import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/post")
public class PostController {
  @Autowired
  private PostService postService;
  @Autowired
  private UserService userService;
  @Autowired
  private ActivityPubService activityPubService;

  @Operation(summary = "IDから投稿を取得", description = "IDから投稿を取得します。Acceptヘッダーが application/activity+json の場合はActivityPub Note形式でJSONを返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class)),
          @Content(mediaType = "application/activity+json", schema = @Schema(implementation = Note.class))
      }),
      @ApiResponse(responseCode = "404", description = "投稿が見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)),
      }) })
  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) throws ErrorResponseException {
    // Accept ヘッダーを確認
    String acceptHeader = request.getHeader("Accept");
    boolean isActivityPubRequest = acceptHeader != null && acceptHeader.contains("application/activity+json");

    if (isActivityPubRequest) {
      // ActivityPub Noteとして投稿情報を返す
      return activityPubService.getNoteObject(id)
          .map(actor -> ResponseEntity.ok()
              .contentType(MediaType.parseMediaType("application/activity+json"))
              .body(actor))
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    } else {
      // 通常の投稿情報を返す
      return ResponseEntity.ok(postService.findById(id)
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)));
    }
  }

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
  public PostDto addPost(@RequestBody PostRequestDto content) throws ErrorResponseException {
    try {
      UserSummaryDto sessionUser = userService.getSessionUser()
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
      PostParamsDto postParamsDto = new PostParamsDto(sessionUser.getId(), content.getContent());
      return postService.add(postParamsDto)
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR));
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
