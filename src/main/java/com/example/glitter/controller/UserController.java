package com.example.glitter.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostService;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserIconService;
import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private PostService postService;
  @Autowired
  private UserIconService userIconService;

  @Operation(summary = "IDからユーザーを取得", description = "IDからユーザーを取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserSummaryDto.class))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}")
  public UserSummaryDto findById(@PathVariable String id) throws ErrorResponseException {
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
    return postService.getPostsByUserId(id);
  }

  @Operation(summary = "セッションユーザーを取得", description = "セッションユーザーを取得します。ログインしていない場合は 401 が返ります。通常発生しませんが、ログインしているにもかかわらずユーザーのデータが見つからない場合、404 ではなく null を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserSummaryDto.class))
      }),
      @ApiResponse(responseCode = "401", description = "ログインしていないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public UserSummaryDto getSessionUser() throws ErrorResponseException {
    return userService.getSessionUser()
        .orElse(null);
  }

  @Operation(summary = "ユーザーを追加", description = "ユーザーを追加します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserSummaryDto.class))
      }),
      @ApiResponse(responseCode = "400", description = "無効な値を渡したとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "409", description = "ユーザーやメールアドレスが重複しているとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "500", description = "サーバーエラーが発生したとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @PostMapping("")
  public UserSummaryDto add(@RequestBody @Valid UserDto user) throws ErrorResponseException {
    try {
      return userService.add(user);
    } catch (DuplicateKeyException e) {
      throw new ErrorResponseException(HttpStatus.CONFLICT);
    } catch (DataIntegrityViolationException e) {
      throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "アイコン画像の更新", description = "アイコン画像を更新します。ログインが必須です。成功時はアイコン画像のパス情報を含む UserSummary を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserSummaryDto.class))
      }),
      @ApiResponse(responseCode = "401", description = "ログインしていないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "500", description = "画像のアップロードに失敗したとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
  })
  // Put だと MultiPartFile が受け取れないため Post で実装
  @PostMapping("/me/icon")
  @PreAuthorize("isAuthenticated()")
  public UserSummaryDto updateIcon(@RequestParam("file") MultipartFile file) throws ErrorResponseException {
    try {
      UserSummaryDto sessionUser = userService.getSessionUser()
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
      return userIconService.updateIcon(file, sessionUser);
    } catch (Exception e) {
      Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
      logger.error("Failed to update icon", e);
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
