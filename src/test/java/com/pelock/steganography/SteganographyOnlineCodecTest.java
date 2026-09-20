package com.pelock.steganography;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SteganographyOnlineCodecTest {

  @Test
  void convertSizeFormatsUnits() {
    SteganographyOnlineCodec client = new SteganographyOnlineCodec();
    assertEquals("0 bytes", client.convertSize(0));
    assertEquals("512 bytes", client.convertSize(512));
    assertEquals("1 kB", client.convertSize(1024));
    assertEquals("1.5 kB", client.convertSize(1536));
  }
}
