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
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.ActivityPub.OrderedCollection;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserNotFoundException;
import com.example.glitter.service.ActivityPubCreateService;
import com.example.glitter.service.FollowListService;
import com.example.glitter.service.SessionUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class FollowController {

  private final ActivityPubCreateService activityPubCreateService;
  @Autowired
  private SessionUserService sessionUserService;
  @Autowired
  private FollowListService followListService;

  @Value("${env.domain}")
  private String domain;

  FollowController(ActivityPubCreateService activityPubCreateService) {
    this.activityPubCreateService = activityPubCreateService;
  }

  @Operation(summary = "ユーザーのフォロー一覧を取得", description = "指定したユーザーがフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/{id}/following")
  public List<UserDto> getFollowing(@PathVariable String id) throws ErrorResponseException {
    try {
      return followListService.getFollowing(id);
    } catch (UserNotFoundException e) {
      throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "ユーザーのフォロワー一覧を取得", description = "指定したユーザーをフォローしているユーザー一覧を取得します。Acceptヘッダーが application/activity+json の場合はActivityPub OrderedCollection形式でJSONを返します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
          @Content(mediaType = "application/activity+json", schema = @Schema(implementation = OrderedCollection.class))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/{id}/followers")
  public ResponseEntity<?> getFollowers(@PathVariable String id, HttpServletRequest request)
      throws ErrorResponseException {
    // Accept ヘッダーを確認
    String acceptHeader = request.getHeader("Accept");
    boolean isActivityPubRequest = acceptHeader != null && acceptHeader.contains("application/activity+json");
    if (isActivityPubRequest) {
      try {
        // ActivityPub OrderedCollection としてユーザー情報を返す
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/activity+json"))
            .body(activityPubCreateService.getFollowersFromUserId(id));
      } catch (UserNotFoundException e) {
        throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
      } catch (Exception e) {
        throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
      }
    } else {
      // 通常のユーザー情報を返す
      try {
        return ResponseEntity.ok(followListService.getFollowers(id));
      } catch (UserNotFoundException e) {
        throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
      } catch (Exception e) {
        throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
      }
    }
  }

  @Operation(summary = "セッションユーザーのフォロー一覧を取得", description = "セッションユーザーがフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      }),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/me/following")
  @PreAuthorize("isAuthenticated()")
  public List<UserDto> getMyFollowing() throws ErrorResponseException {
    try {
      return sessionUserService.getFollowing();
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "セッションユーザーのフォロワー一覧を取得", description = "セッションユーザーをフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      }),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/me/followers")
  @PreAuthorize("isAuthenticated()")
  public List<UserDto> getMyFollowers() throws ErrorResponseException {
    try {
      return sessionUserService.getFollowers();
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }
}
