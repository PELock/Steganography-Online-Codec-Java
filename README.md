# Steganography Online Codec — Java SDK

Java client for the [Steganography Online Codec](https://www.pelock.com/products/steganography-online-codec) Web API. Hide an AES-encrypted message inside an image via `https://www.pelock.com/api/steganography-online-codec/v1`.

## Maven

```xml
<dependency>
  <groupId>com.pelock</groupId>
  <artifactId>steganography-online-codec</artifactId>
  <version>1.0.2</version>
</dependency>
```

This package is not published to Maven Central. Install locally with `mvn install`.

## Usage

`encode` / `decode` upload the cover image as a multipart file field named `image`.

```java
import com.pelock.steganography.Errors;
import com.pelock.steganography.SteganographyOnlineCodec;
import com.fasterxml.jackson.databind.JsonNode;

SteganographyOnlineCodec client = new SteganographyOnlineCodec("YOUR-WEB-API-KEY");
JsonNode result = client.encode("input.jpg", "Secret message", "Pa$$word", "output.png");

if (result.path("error").asInt(Errors.WEBAPI_CONNECTION) == Errors.SUCCESS) {
    System.out.println("encoded");
}
```

See `examples/`. Apache-2.0. Copyright Bartosz Wójcik / PELock.
