package com.blakekhan.surl.aws.redirect;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.blakekhan.surl.aws.common.link.ShortLink;
import com.blakekhan.surl.aws.common.proxy.ProxyUtil;
import com.blakekhan.surl.aws.common.util.DynamoUtil;
import com.blakekhan.surl.aws.common.util.Header;
import java.util.Optional;
import lombok.NonNull;

public class App implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private static final String KEY_NICE;
  private static final String BASE_DOMAIN;
  private static final String NOT_FOUND_DOMAIN;
  private static final String HTML_TEMPLATE;
  private static final String BODY_NOT_FOUND;
  private static final int HTTP_CODE_FOUND;
  private static final long CACHE_SEC;

  static {
    KEY_NICE = "niceName";
    BASE_DOMAIN = System.getenv("BASE_DOMAIN");
    NOT_FOUND_DOMAIN = System.getenv("404_REDIRECT");
    HTML_TEMPLATE = "<a href=\"%s\">Click here if you have not been redirected.</a>";
    BODY_NOT_FOUND = "Not Found :(";
    HTTP_CODE_FOUND = 302;
    long cache = 300L; // 5 min default

    try {
      cache = Long.parseLong(System.getenv("CACHE_SECONDS"));
    } catch (NumberFormatException ignored) {
    }

    CACHE_SEC = cache;
  }

  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent request, Context context) {
    String niceName = request.getPathParameters().getOrDefault(KEY_NICE, null);

    // Sanitize input
    if (niceName == null || (niceName = niceName.strip()).isBlank()) {
      return ProxyUtil.createResponse(HTTP_CODE_FOUND, BODY_NOT_FOUND, BASE_DOMAIN);
    }

    // Get short link from db
    Optional<ShortLink> shortLinkOptional = DynamoUtil.getShortLink(niceName);

    if (shortLinkOptional.isEmpty()) {
      return ProxyUtil.createResponse(HTTP_CODE_FOUND, BODY_NOT_FOUND, NOT_FOUND_DOMAIN);
    }

    // Verify link is not expired
    ShortLink shortLink = shortLinkOptional.get();

    if (shortLink.isExpired()) {
      // Remove if expired
      DynamoUtil.removeShortLink(shortLink);
      return ProxyUtil.createResponse(HTTP_CODE_FOUND, BODY_NOT_FOUND, NOT_FOUND_DOMAIN);
    }

    // Return temp redirect
    APIGatewayV2ProxyResponseEvent response = ProxyUtil.createResponse(HTTP_CODE_FOUND, generateHTML(shortLink), shortLink.getDestination());
    response.getHeaders().put(Header.CACHE_CONTROL.getKey(), String.format("max-age=%d", CACHE_SEC));
    return response;
  }

  private String generateHTML(@NonNull ShortLink shortLink) {
    return String.format(HTML_TEMPLATE, shortLink.getDestination());
  }
}
