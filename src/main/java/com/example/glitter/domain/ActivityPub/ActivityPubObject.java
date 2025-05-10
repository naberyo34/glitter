package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Object を表す基底クラス
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#object-types
 */
@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ActivityPubObject {
  @JsonProperty("@context")
  @Schema(description = "コンテキスト", example = "https://www.w3.org/ns/activitystreams", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String context = "https://www.w3.org/ns/activitystreams";

  @Schema(description = "オブジェクトのタイプ", example = "Object", requiredMode = Schema.RequiredMode.REQUIRED)
  private String type;

  @Schema(description = "オブジェクトのID", example = "https://example.com/user/test_user/object/123", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
}
