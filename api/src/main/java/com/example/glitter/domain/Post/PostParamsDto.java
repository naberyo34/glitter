package com.example.glitter.domain.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * ID と 投稿日時を持たない 投稿 DTO
 */
public class PostParamsDto {
  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @NotBlank
  @Schema(description = "本文", example = "がんばります。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;
}
