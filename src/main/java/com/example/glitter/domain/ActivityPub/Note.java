package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Note Object
 * アプリ上で post と呼称しているものと同等です。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-note
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Note extends ActivityPubObject {
  @Schema(description = "オブジェクトのタイプ", example = "Note", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Note";
  
  @Schema(description = "ノートの内容", example = "こんにちは。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;
  
  @Schema(description = "ノートの公開日時", example = "2025-04-29T12:34:56Z", requiredMode = Schema.RequiredMode.REQUIRED)
  private String published;
  
  @Schema(description = "ノートの作成者のActor Endpoint", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String attributedTo;
  
  @Schema(description = "ノートの公開範囲", example = "https://www.w3.org/ns/activitystreams#Public", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String[] to = {"https://www.w3.org/ns/activitystreams#Public"};

  @Schema(description = "ノートの cc (フォロワー)", example = "https://example.com/user/test_user/followers", requiredMode = Schema.RequiredMode.REQUIRED)
  private String cc;
}
