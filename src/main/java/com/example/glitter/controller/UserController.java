package com.example.glitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.ActivityPub.Actor;
import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.Post.PostWithAuthor;
import com.example.glitter.domain.User.UserNotFoundException;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.service.ActivityPubCreateService;
import com.example.glitter.service.PostWithAuthorService;
import com.example.glitter.service.SessionUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private SessionUserService sessionUserService;
  @Autowired
  private PostWithAuthorService postWithAuthorService;
  @Autowired
  private ActivityPubCreateService activityPubCreateService;

  @Value("${env.domain}")
  private String domain;

  @Operation(summary = "IDからユーザーを取得", description = "IDからユーザーを取得します。Acceptヘッダーが application/activity+json の場合はActivityPub Actor形式でJSONを返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)),
          @Content(mediaType = "application/activity+json", schema = @Schema(implementation = Actor.class))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)),
      }) })
  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable String id, HttpServletRequest request) throws ErrorResponseException {
    // Accept ヘッダーを確認
    String acceptHeader = request.getHeader("Accept");
    boolean isActivityPubRequest = acceptHeader != null && acceptHeader.contains("application/activity+json");

    if (isActivityPubRequest) {
      // ActivityPub Actorとしてユーザー情報を返す
      return activityPubCreateService.getActorFromUserId(id)
          .map(actor -> ResponseEntity.ok()
              .contentType(MediaType.parseMediaType("application/activity+json"))
              .body(actor))
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
    } else {
      // 通常のユーザー情報を返す
      return ResponseEntity.ok(userRepository.findByUserIdAndDomain(id, domain)
          .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)));
    }
  }

  @Operation(summary = "ユーザーのOutboxを取得", description = "ユーザーの投稿をActivityPubのOutbox形式で取得します。ActivityPub準拠のクライアントからのリクエスト用です。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/activity+json", schema = @Schema(implementation = OrderedCollection.class)),
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}/outbox")
  public ResponseEntity<OrderedCollection> getOutbox(@PathVariable String id) throws ErrorResponseException {
    return activityPubCreateService.getNotesFromUserId(id)
        .map(outbox -> ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/activity+json"))
            .body(outbox))
        .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "ユーザーの投稿を全件取得", description = "ユーザーの投稿を全件取得します。ユーザー自体が存在しない場合は404、ユーザーが1件も投稿を持たない場合は空配列を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostWithAuthor.class))),
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからないとき", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }) })
  @GetMapping("/{id}/post")
  public List<PostWithAuthor> getUserPosts(@PathVariable String id) throws ErrorResponseException {
    try {
      return postWithAuthorService.findPostsByUserIdAndDomain(id, domain);
    } catch (UserNotFoundException e) {
      throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
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
    return sessionUserService.getMe();
  }

  @Operation(summary = "アイコン画像の更新", description = "アイコン画像を更新します。ログインが必須です。成功時はアイコン画像のパス情報を含む userDto を返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
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
  public UserDto updateIcon(@RequestParam("file") MultipartFile file) throws ErrorResponseException {
    try {
      return sessionUserService.updateIcon(file);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
