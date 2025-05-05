package com.example.glitter.domain.Post;

import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 投稿にユーザー情報を結合したレスポンス DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostWithAuthor extends PostDto {
  @Schema(description = "ユーザー情報", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserResponse user;

  public static PostWithAuthor fromEntity(PostDto post, User user) {
    PostWithAuthor postResponseDto = new PostWithAuthor();
    postResponseDto.setUuid(post.getUuid());
    postResponseDto.setDomain(post.getDomain());
    postResponseDto.setUserId(post.getUserId());
    postResponseDto.setContent(post.getContent());
    postResponseDto.setCreatedAt(post.getCreatedAt());
    postResponseDto.setUser(UserResponse.fromEntity(user));
    return postResponseDto;
  }
}
