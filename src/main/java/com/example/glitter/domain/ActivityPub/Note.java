package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ActivityPub の Note Object
 * アプリ上で post と呼称しているものと同等です。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-note
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Note {
  @Schema(description = "ノートのID", example = "https://example.com/user/test_user/post/123", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  
  @Schema(description = "ノートのタイプ", example = "Note", requiredMode = Schema.RequiredMode.REQUIRED)
  private String type;
  
  @Schema(description = "ノートの内容", example = "こんにちは。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;
  
  @Schema(description = "ノートの公開日時", example = "2025-04-29T12:34:56Z", requiredMode = Schema.RequiredMode.REQUIRED)
  private String published;
  
  @Schema(description = "ノートの作成者", requiredMode = Schema.RequiredMode.REQUIRED)
  private String attributedTo;
  
  @Schema(description = "ノートの公開範囲", example = "https://www.w3.org/ns/activitystreams#Public", requiredMode = Schema.RequiredMode.REQUIRED)
  private String[] to;
}
