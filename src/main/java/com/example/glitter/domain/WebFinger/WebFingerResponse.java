package com.example.glitter.domain.WebFinger;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * WebFinger プロトコルに準拠した、リソースの URI を返すためのクラス。
 * @see https://scrapbox.io/activitypub/webfinger (非公式情報)
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class WebFingerResponse {
  @Schema(description = "acct (アクターエンドポイントを取得するために使う)", example = "acct:example@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String subject;
  private List<Link> links;
  
  @Data
  @Builder
  @JsonInclude(Include.NON_NULL)
  @Schema(description = "アクターエンドポイントを含むリンクの情報", requiredMode = Schema.RequiredMode.REQUIRED)
  public static class Link {
    @Schema(description = "self", example = "self", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rel;
    @Schema(description = "application/activity+json", example = "application/activity+json", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(description = "アクターエンドポイント", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String href;
  }
}
