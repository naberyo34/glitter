package com.example.glitter.domain.ActivityPub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の OrderedCollection Object を表す。
 * Outbox Endpoint からの応答としてこれを返します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-orderedcollection
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class OrderedCollection extends ActivityPubObject {    
  @Schema(description = "オブジェクトのタイプ", example = "OrderedCollection", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "OrderedCollection";
  
  @Schema(description = "アイテムの総数", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
  private int totalItems;
  
  @Schema(description = "アイテムの配列", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<ActivityPubObject> orderedItems;
}
