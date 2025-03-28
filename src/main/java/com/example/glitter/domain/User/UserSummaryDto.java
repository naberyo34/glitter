package com.example.glitter.domain.User;

import org.hibernate.validator.constraints.URL;

import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー Summary クラス
 * パスワードや認証情報を含まないユーザー情報です。
 * 認証情報を扱う必要があるときを除き、原則アプリケーションはこちらのクラスを用いてユーザーを扱います。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDto {
  @Pattern(regexp = "^[a-zA-Z0-9_]+$")
  @Size(min = 1, max = 20)
  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;

  @NotBlank
  @Size(min = 1, max = 40)
  @Schema(description = "ユーザー名", example = "太郎", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @Email
  @Schema(description = "メールアドレス", example = "example@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @Schema(description = "プロフィール", example = "こんにちは。", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String profile;

  @Schema(description = "プロフィールアイコン", example = "/test_user/example.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String icon;

  public static UserSummaryDto fromEntity(User user) {
    return new UserSummaryDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfile(), user.getIcon());
  }
}
