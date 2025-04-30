package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Activity を表す
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-activity
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Activity extends ActivityPubObject {
  @Schema(description = "アクティビティの作成者の Actor Endpoint", example = "http://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;
  
  @Schema(description = "アクティビティの対象オブジェクト", requiredMode = Schema.RequiredMode.REQUIRED)
  private ActivityPubObject object;
  
  @Schema(description = "アクティビティの公開日時", example = "2025-04-29T12:34:56Z", requiredMode = Schema.RequiredMode.REQUIRED)
  private String published;
  
  @Schema(description = "アクティビティの公開範囲", example = "https://www.w3.org/ns/activitystreams#Public", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String[] to = {"https://www.w3.org/ns/activitystreams#Public"};

  // TODO: cc
}
