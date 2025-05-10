package com.example.glitter.domain.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostRequest {
  @NotBlank
  @Schema(description = "本文", example = "がんばります。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;
}
