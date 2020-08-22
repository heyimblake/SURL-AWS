package com.blakekhan.surl.aws.common.util;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Immutable Tuple
 *
 * @param <K> Type of key
 * @param <V> Type of value
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class ImmTuple<K, V> implements Map.Entry<K, V> {

  private final K key;
  private final V value;

  @Override
  public V setValue(V value) {
    throw new UnsupportedOperationException("Cannot modify an Immutable Tuple.");
  }
}
