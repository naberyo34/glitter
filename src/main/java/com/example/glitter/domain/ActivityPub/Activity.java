package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ActivityPub の Activity Object を表す。
 * アクティビティストリームの要素として使用します。
 * TODO: 本来、すべてのアクティビティを想定する必要がありますが、現状では Note (Post) を前提としています。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-activity
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Activity {
  @Schema(description = "アクティビティのID", example = "https://example.com/user/test_user/activity/123", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  
  @Schema(description = "アクティビティのタイプ", example = "Create", requiredMode = Schema.RequiredMode.REQUIRED)
  private String type;
  
  @Schema(description = "アクティビティの作成者", requiredMode = Schema.RequiredMode.REQUIRED)
  private Actor actor;
  
  @Schema(description = "アクティビティの対象オブジェクト", requiredMode = Schema.RequiredMode.REQUIRED)
  // TODO: いったん Note に固定
  private Note object;
  
  @Schema(description = "アクティビティの公開日時", example = "2025-04-29T12:34:56Z", requiredMode = Schema.RequiredMode.REQUIRED)
  private String published;
  
  @Schema(description = "アクティビティの公開範囲", example = "https://www.w3.org/ns/activitystreams#Public", requiredMode = Schema.RequiredMode.REQUIRED)
  private String[] to;
}
