package com.example.glitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowService;
import com.example.glitter.domain.User.UserSummaryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class FollowController {

  @Autowired
  private FollowService followService;

  @Operation(summary = "ユーザーをフォローする", description = "指定したユーザーをフォローします。ログインが必須です。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = FollowDto.class))
      }),
      @ApiResponse(responseCode = "400", description = "不正なリクエスト（自分自身をフォローしようとした場合など）", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @PostMapping("/user/{id}/follow")
  @PreAuthorize("isAuthenticated()")
  public FollowDto follow(@PathVariable String id) throws ErrorResponseException {
    try {
      return followService.follow(id);
    } catch (IllegalArgumentException e) {
      throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "ユーザーのフォローを解除する", description = "指定したユーザーのフォローを解除します。ログインが必須です。", responses = {
      @ApiResponse(responseCode = "200", description = "フォロー解除成功"),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      }),
      @ApiResponse(responseCode = "404", description = "フォロー関係が見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @DeleteMapping("/user/{id}/follow")
  @PreAuthorize("isAuthenticated()")
  public void unfollow(@PathVariable String id) throws ErrorResponseException {
    try {
      boolean success = followService.unfollow(id);
      if (!success) {
        throw new ErrorResponseException(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      if (e instanceof ErrorResponseException) {
        throw (ErrorResponseException) e;
      }
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "ユーザーのフォロー一覧を取得", description = "指定したユーザーがフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSummaryDto.class)))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/{id}/following")
  public List<UserSummaryDto> getFollowing(@PathVariable String id) throws ErrorResponseException {
    try {
      return followService.getFollowing(id);
    } catch (IllegalArgumentException e) {
      throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "ユーザーのフォロワー一覧を取得", description = "指定したユーザーをフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSummaryDto.class)))
      }),
      @ApiResponse(responseCode = "404", description = "ユーザーが見つからない", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/{id}/followers")
  public List<UserSummaryDto> getFollowers(@PathVariable String id) throws ErrorResponseException {
    try {
      return followService.getFollowers(id);
    } catch (IllegalArgumentException e) {
      throw new ErrorResponseException(HttpStatus.NOT_FOUND, e);
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "セッションユーザーのフォロー一覧を取得", description = "セッションユーザーがフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSummaryDto.class)))
      }),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/me/following")
  @PreAuthorize("isAuthenticated()")
  public List<UserSummaryDto> getMyFollowing() throws ErrorResponseException {
    try {
      return followService.getMyFollowing();
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }

  @Operation(summary = "セッションユーザーのフォロワー一覧を取得", description = "セッションユーザーをフォローしているユーザー一覧を取得します。", responses = {
      @ApiResponse(responseCode = "200", description = "OK", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSummaryDto.class)))
      }),
      @ApiResponse(responseCode = "401", description = "認証エラー", content = {
          @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
      })
  })
  @GetMapping("/user/me/followers")
  @PreAuthorize("isAuthenticated()")
  public List<UserSummaryDto> getMyFollowers() throws ErrorResponseException {
    try {
      return followService.getMyFollowers();
    } catch (Exception e) {
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
  }
}
