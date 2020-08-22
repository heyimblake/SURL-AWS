package com.blakekhan.surl.aws.delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.blakekhan.surl.aws.common.link.ShortLink;
import com.blakekhan.surl.aws.common.proxy.ProxyUtil;
import com.blakekhan.surl.aws.common.util.DynamoUtil;
import java.util.Optional;

public class App implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private static final int SUCCESS;
  private static final int BAD_REQUEST;
  private static final String KEY_NICE;

  static {
    SUCCESS = 204;
    BAD_REQUEST = 400;
    KEY_NICE = "niceName";
  }

  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent request, Context context) {
    String niceName = request.getPathParameters().getOrDefault(KEY_NICE, null);

    // Sanitize nicename
    if (niceName == null || (niceName = niceName.strip()).isBlank()) {
      return ProxyUtil.createResponse(BAD_REQUEST, "Nice Name not found.");
    }

    // Get from db
    Optional<ShortLink> shortLinkOptional = DynamoUtil.getShortLink(niceName);

    if (shortLinkOptional.isEmpty()) {
      return ProxyUtil.createResponse(BAD_REQUEST, "Short link does not exist.");
    }

    // Delete from db
    DynamoUtil.removeShortLink(shortLinkOptional.get());
    return ProxyUtil.createResponse(SUCCESS, String.format("Deleting %s.", niceName));
  }
}
