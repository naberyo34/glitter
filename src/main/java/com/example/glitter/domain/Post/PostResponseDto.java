package com.example.glitter.domain.Post;

import java.util.Date;

import com.example.glitter.domain.User.UserSummaryDto;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投稿にユーザー情報を結合したレスポンス DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
  @Schema(description = "投稿 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long id;

  @NotBlank
  @Schema(description = "本文", example = "がんばります。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;

  @Schema(description = "投稿日時", example = "2025-03-10T08:14:12.451+00:00", requiredMode = Schema.RequiredMode.REQUIRED)
  private Date createdAt;

  @Schema(description = "ユーザー情報", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserSummaryDto user;

  public static PostResponseDto fromEntity(Post post, User user) {
    PostResponseDto postResponseDto = new PostResponseDto();
    postResponseDto.setId(post.getId());
    postResponseDto.setContent(post.getContent());
    postResponseDto.setCreatedAt(post.getCreatedAt());
    postResponseDto.setUser(UserSummaryDto.fromEntity(user));
    return postResponseDto;
  }
}
