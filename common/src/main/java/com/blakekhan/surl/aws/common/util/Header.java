package com.blakekhan.surl.aws.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Header names.
 */
@Getter
@RequiredArgsConstructor
public enum Header {

  NICE_NAME("Nicename"),
  DESTINATION("Destination"),
  EXPIRE_AT("ExpireAt"),
  LOCATION("Location"),
  CONTENT_TYPE("Content-Type"),
  SERVER("Server"),
  CACHE_CONTROL("Cache-Control");

  private final String key;
}
