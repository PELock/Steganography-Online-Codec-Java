/******************************************************************************
 * Steganography Online Codec WebApi interface
 *
 * Version        : v1.0.2
 * Language       : Java
 * Author         : Bartosz Wójcik
 * Web page       : https://www.pelock.com
 *
 *****************************************************************************/

package com.pelock.steganography;

/** Error codes returned by the Steganography Online Codec API and this SDK. */
public final class Errors {

  public static final int WEBAPI_CONNECTION = -1;
  public static final int SUCCESS = 0;
  public static final int UNKNOWN = 1;
  public static final int MESSAGE_TOO_LONG = 2;
  public static final int IMAGE_TOO_BIG = 3;
  public static final int INVALID_INPUT = 4;
  public static final int INVALID_IMAGE_FORMAT = 5;
  public static final int IMAGE_MALFORMED = 6;
  public static final int INVALID_PASSWORD = 7;
  public static final int LIMIT_MESSAGE = 9;
  public static final int LIMIT_PASSWORD = 10;
  public static final int OUTPUT_FILE = 99;
  public static final int INVALID_LICENSE = 100;

  private Errors() {}
}
