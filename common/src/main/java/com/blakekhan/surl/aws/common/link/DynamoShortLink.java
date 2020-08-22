package com.blakekhan.surl.aws.common.link;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a document in DynamoDB.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
@DynamoDBTable(tableName = "surl_links") //TODO: Figure out a way to not hardcode this value
public final class DynamoShortLink implements ShortLink {

  @DynamoDBHashKey(attributeName = KEY_NICE)
  private String niceName;

  @DynamoDBAttribute(attributeName = KEY_DESTINATION)
  private String destination;

  @DynamoDBAttribute(attributeName = KEY_EXPIRATION)
  private Date expiration;

  @DynamoDBAttribute(attributeName = KEY_CREATED)
  private Date created;

}
