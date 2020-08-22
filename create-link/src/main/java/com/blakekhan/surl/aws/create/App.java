package com.blakekhan.surl.aws.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.blakekhan.surl.aws.common.link.DynamoShortLink;
import com.blakekhan.surl.aws.common.link.ShortLink;
import com.blakekhan.surl.aws.common.proxy.ProxyUtil;
import com.blakekhan.surl.aws.common.util.DynamoUtil;
import com.blakekhan.surl.aws.common.util.Header;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

public class App
    implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private static final String BASE_DOMAIN;
  private static final int BAD_REQUEST;
  private static final Pattern NICE_NAME_PATTERN;

  static {
    BASE_DOMAIN = System.getenv("BASE_DOMAIN");
    BAD_REQUEST = 400;
    NICE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
  }

  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(
      APIGatewayV2ProxyRequestEvent request, Context context) {
    // Verify nicename header exists
    String niceName = request.getHeaders().getOrDefault(Header.NICE_NAME.getKey(), null);
    if (niceName == null) {
      return ProxyUtil.createResponse(
          BAD_REQUEST, String.format("Requires %s header.", Header.NICE_NAME.getKey()));
    }

    niceName = niceName.trim();

    // Verify nice name matches pattern
    if (niceName.isEmpty() || !NICE_NAME_PATTERN.matcher(niceName).matches()) {
      return ProxyUtil.createResponse(
          BAD_REQUEST,
          String.format("%s Header value must be alphanumeric.", Header.NICE_NAME.getKey()));
    }

    // Verify destination header exists
    String destination = request.getHeaders().getOrDefault(Header.DESTINATION.getKey(), null);
    if (destination == null) {
      return ProxyUtil.createResponse(
          BAD_REQUEST, String.format("Requires %s header.", Header.DESTINATION.getKey()));
    }

    destination = destination.trim();

    if (destination.isEmpty()) {
      ProxyUtil.createResponse(
          BAD_REQUEST,
          String.format("%s Header value is not a proper URI.", Header.DESTINATION.getKey()));
    }

    // Verify destination is valid format
    try {
      URI uri = new URI(destination);
    } catch (URISyntaxException e) {
      return ProxyUtil.createResponse(
          BAD_REQUEST,
          String.format("%s Header value is not a proper URI.", Header.DESTINATION.getKey()));
    }

    // Creation Date (now)
    Date created = new Date();

    // Set & parse expiration
    Date expiration = null;
    String expirationRaw = request.getHeaders().getOrDefault(Header.EXPIRE_AT.getKey(), null);
    if (expirationRaw != null) {
      try {
        long expireLong = Long.parseLong(expirationRaw);
        expiration = new Date(expireLong);
      } catch (NumberFormatException ex) {
        return ProxyUtil.createResponse(
            BAD_REQUEST,
            String.format("Could not parse %s: %s", Header.EXPIRE_AT.getKey(), expirationRaw));
      }

      if (expiration.before(created)) {
        return ProxyUtil.createResponse(
            BAD_REQUEST,
            String.format(
                "Header %s: %s is an invalid timestamp.", Header.EXPIRE_AT, expirationRaw));
      }
    }

    // Create ShortLink
    ShortLink shortLink =
        DynamoShortLink.builder()
            .niceName(niceName)
            .destination(destination)
            .expiration(expiration)
            .created(created)
            .build();

    return processShortLink(shortLink);
  }

  private APIGatewayV2ProxyResponseEvent processShortLink(ShortLink shortLink) {
    Optional<ShortLink> optionalExisting = DynamoUtil.getShortLink(shortLink.getNiceName());

    // Completely new link, does not currently exist.
    if (optionalExisting.isEmpty()) {
      // Insert into db
      DynamoUtil.putShortLink(shortLink);
      return ProxyUtil.createResponse(201, BASE_DOMAIN + shortLink.getNiceName());
    }

    return ProxyUtil.createResponse(
        400, String.format("Link %s already exists.", shortLink.getNiceName()));
  }
}
