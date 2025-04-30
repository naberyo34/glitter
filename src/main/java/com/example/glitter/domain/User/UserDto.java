package com.example.glitter.domain.User;

import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
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

  @Schema(description = "Cognito の一意なユーザーID", example = "e7441ae8-a0a1-7062-2e61-571da628a8be", requiredMode = Schema.RequiredMode.REQUIRED)
  private String sub;

  public User toEntity() {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setEmail(email);
    user.setProfile(profile);
    user.setIcon(icon);
    user.setSub(sub);
    return user;
  }

  public static UserDto fromEntity(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setUsername(user.getUsername());
    userDto.setEmail(user.getEmail());
    userDto.setProfile(user.getProfile());
    userDto.setIcon(user.getIcon());
    userDto.setSub(user.getSub());
    return userDto;
  }
}
