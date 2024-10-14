package com.kuibot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CharsetConfigTest {
  @Before
  public void setUp() {
  }

  @Test
  public void shouldLoadConfig() {
    CharsetConfig config = CharsetConfig.load();
    Assert.assertTrue(config.isEnabled());
    Assert.assertEquals("", "UTF-8", config.getCharset());
    Assert.assertNotEquals("", "ISO-8859-1", config.getCharset());
    Assert.assertNotNull(config.getContentTypeList());
    Assert.assertEquals(3, config.getContentTypeList().size());
  }
}
