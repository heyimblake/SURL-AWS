package com.blakekhan.surl.aws.common.proxy;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.blakekhan.surl.aws.common.util.Header;
import java.util.HashMap;
import java.util.Map;

public class ProxyUtil {

  private static final String CONTENT_TYPE_VALUE = "text/plain";
  private static final String SERVER_VALUE = "SURL-AWS";

  /**
   * Creates an APIGateway Proxy Response.
   *
   * @param statusCode http status code of the response
   * @param body plaintext body of response
   * @return proxy response object
   */
  public static APIGatewayV2ProxyResponseEvent createResponse(int statusCode, String body) {
    return createResponse(statusCode, body, null);
  }

  /**
   * Creates an APIGateway Proxy Response.
   *
   * @param statusCode http status code of the response
   * @param body plaintext body of response
   * @param location the url to put in the location header
   * @return proxy response object
   */
  public static APIGatewayV2ProxyResponseEvent createResponse(
      int statusCode, String body, String location) {
    Map<String, String> headers = new HashMap<>();
    APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();

    // Set Attributes
    response.setStatusCode(statusCode);
    response.setBody(body);
    headers.put(Header.CONTENT_TYPE.getKey(), CONTENT_TYPE_VALUE);
    headers.put(Header.SERVER.getKey(), SERVER_VALUE);

    if (location != null) {
      headers.put(Header.LOCATION.getKey(), location);
    }

    // Set headers in response
    response.setHeaders(headers);

    // Return response
    return response;
  }
}
