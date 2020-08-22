package com.blakekhan.surl.aws.common.util;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.blakekhan.surl.aws.common.link.DynamoShortLink;
import com.blakekhan.surl.aws.common.link.ShortLink;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.NonNull;

public final class DynamoUtil {

  private static final Regions REGION;
  private static final String TABLE_LINKS_NAME;
  private static final DynamoDB DYNAMO;
  private static final Table LINKS_TABLE;
  private static final AmazonDynamoDBAsync AMAZON_DYNAMO_DB_ASYNC;

  static {
    REGION = Regions.fromName(System.getenv("AWS_REGION"));
    TABLE_LINKS_NAME = System.getenv("TABLE_NAME_LINKS");
    AmazonDynamoDBAsyncClientBuilder clientBuilder = AmazonDynamoDBAsyncClientBuilder.standard();

    // Set local endpoint
    if (System.getenv().containsKey("AWS_SAM_LOCAL") && Boolean.parseBoolean(System.getenv("AWS_SAM_LOCAL"))) {
      clientBuilder.withEndpointConfiguration(
          new EndpointConfiguration("http://surl-local-dynamodb:8000", REGION.getName()));
    } else {
      // Not local, set region
      clientBuilder.withRegion(REGION);
    }

    // Provide credentials
    clientBuilder.setCredentials(new EnvironmentVariableCredentialsProvider());

    AMAZON_DYNAMO_DB_ASYNC = clientBuilder.build();
    DYNAMO = new DynamoDB(AMAZON_DYNAMO_DB_ASYNC);
    LINKS_TABLE = DYNAMO.getTable(TABLE_LINKS_NAME);
  }

  public static Optional<ShortLink> getShortLink(@NonNull String niceName) {
    Item item = LINKS_TABLE.getItem(DynamoShortLink.KEY_NICE, niceName);

    if (item == null) {
      return Optional.empty();
    }

    ShortLink shortLink =
        new DynamoDBMapper(AMAZON_DYNAMO_DB_ASYNC)
            .marshallIntoObject(DynamoShortLink.class, ItemUtils.toAttributeValues(item));
    return Optional.ofNullable(shortLink);
  }

  public static Future<PutItemResult> putShortLink(@NonNull ShortLink shortLink) {
    return AMAZON_DYNAMO_DB_ASYNC.putItemAsync(new PutItemRequest(TABLE_LINKS_NAME, ItemUtils.toAttributeValues(shortLink.toItem())));
  }

  public static Future<DeleteItemResult> removeShortLink(@NonNull ShortLink shortLink) {
    return AMAZON_DYNAMO_DB_ASYNC.deleteItemAsync(new DeleteItemRequest(TABLE_LINKS_NAME, ItemUtils.toAttributeValues(shortLink.toItem())));
  }
}
