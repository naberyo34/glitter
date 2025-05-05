package com.example.glitter.domain.User;

import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー Response クラス
 * 必要な情報のみが抽出された user です。原則アプリケーションはこちらのクラスを用いてユーザーを扱います。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  @Pattern(regexp = "^[a-zA-Z0-9_]+$")
  @Size(min = 1, max = 20)
  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @Schema(description = "ユーザーのドメイン", example = "example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String domain;

  @Schema(description = "ユーザーの Actor URL", example = "https://example.com/user/example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actorUrl;

  @NotBlank
  @Size(min = 1, max = 40)
  @Schema(description = "ユーザー名", example = "太郎", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @Schema(description = "プロフィール", example = "こんにちは。", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String profile;

  @Schema(description = "プロフィールアイコン", example = "/test_user/example.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String icon;

  public static UserResponse fromEntity(User user) {
    return new UserResponse(user.getUserId(), user.getDomain(), user.getActorUrl(), user.getUsername(),
        user.getProfile(), user.getIcon());
  }
}
