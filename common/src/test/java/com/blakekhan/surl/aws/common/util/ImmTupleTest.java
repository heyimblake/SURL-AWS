package com.blakekhan.surl.aws.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmTupleTest {

  @Test
  public void testGetKey() {
    String key = "hello";
    ImmTuple<String, String> tuple = new ImmTuple<>(key, "world");
    Assertions.assertEquals(key, tuple.getKey());
  }

  @Test
  public void testGetValue() {
    String value = "world";
    ImmTuple<String, String> tuple = new ImmTuple<>("hello", value);
    Assertions.assertEquals(value, tuple.getValue());
  }

  @Test
  public void testSetValue() {
    ImmTuple<String, String> tuple = new ImmTuple<>("hello", "world");
    Assertions.assertThrows(UnsupportedOperationException.class, () -> tuple.setValue("world!"));
  }

  @Test
  public void testEquals() {
    ImmTuple<String, String> tuple1 = new ImmTuple<>("hello", "world");
    ImmTuple<String, String> tuple2 = new ImmTuple<>("hello", "world");
    Assertions.assertEquals(tuple2, tuple1);
    Assertions.assertEquals(tuple1, tuple2);
  }
}
