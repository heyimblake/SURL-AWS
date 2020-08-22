package com.blakekhan.surl.aws.common.link;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.util.DateUtils;
import java.util.Date;

/**
 * Represents a ShortLink entry in a database.
 */
public interface ShortLink {

  String KEY_NICE = "niceName";
  String KEY_DESTINATION = "destination";
  String KEY_EXPIRATION = "expireAt";
  String KEY_CREATED = "created";

  /**
   * Retrieves the short/nice name of this ShortLink.
   *
   * @return nice name
   */
  String getNiceName();

  /**
   * Sets the short/nice name of this ShortLink to the provided name.
   *
   * @param newName the new nice name
   */
  void setNiceName(String newName);

  /**
   * Retrieves the destination URL of this ShortLink.
   *
   * @return destination url
   */
  String getDestination();

  /**
   * Sets the destination of this ShortLink to the provided destination.
   *
   * @param newDestination the new destination
   */
  void setDestination(String newDestination);

  /**
   * Retrieves the expiration date of this ShortLink.
   *
   * @return expiration date, <code>null</code> if does not expire
   */
  Date getExpiration();

  /**
   * Sets the expiration date of this ShortLink to the provided date.
   *
   * @param newExpiration the new expiration date, <code>null</code> if it should not expire
   */
  void setExpiration(Date newExpiration);

  /**
   * Retrieves the creation date of this ShortLink.
   *
   * @return creation date
   */
  Date getCreated();

  /**
   * Sets the creation date of this ShortLink to the provided date.
   *
   * @param newCreationDate the new creation date
   */
  void setCreated(Date newCreationDate);

  /**
   * Determines if this ShortLink is expired or not.
   *
   * @return <code>true</code> if expired, <code>false</code> otherwise
   */
  default boolean isExpired() {
    Date expire = getExpiration();
    return expire != null && expire.before(new Date());
  }

  /**
   * Converts this ShortLink to an Item.
   *
   * @return item representation of this shortlink
   */
  default Item toItem() {
    Item item =
        new Item()
            .withString(KEY_NICE, getNiceName())
            .withString(KEY_DESTINATION, getDestination())
            .withString(KEY_CREATED, DateUtils.formatISO8601Date(getCreated()));

    if (getExpiration() == null) {
      item.withNull(KEY_EXPIRATION);
    } else {
      item.withString(KEY_EXPIRATION, DateUtils.formatISO8601Date(getExpiration()));
    }

    return item;
  }
}
