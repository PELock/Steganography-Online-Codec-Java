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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * Java client for login / encode / decode against the Steganography Online Codec Web API.
 * Image fields are sent as multipart file uploads.
 */
public class SteganographyOnlineCodec {

  public static final String DEFAULT_API_URL =
      "https://www.pelock.com/api/steganography-online-codec/v1";

  private static final ObjectMapper JSON = new ObjectMapper();

  private final String apiKey;
  private String apiUrl = DEFAULT_API_URL;

  public SteganographyOnlineCodec(String apiKey) {
    this.apiKey = apiKey;
  }

  public SteganographyOnlineCodec() {
    this(null);
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = Objects.requireNonNull(apiUrl, "apiUrl");
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public JsonNode login() throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "login");
    return postRequest(params, null);
  }

  /**
   * Encode a secret message into an image and write the resulting PNG to {@code outputImagePath}.
   * On success the {@code encodedImage} field is consumed and not returned.
   */
  public JsonNode encode(
      String inputImagePath, String messageToHide, String password, String outputImagePath)
      throws IOException {
    return encode(
        inputImagePath == null ? null : Path.of(inputImagePath),
        messageToHide,
        password,
        outputImagePath == null ? null : Path.of(outputImagePath));
  }

  public JsonNode encode(
      Path inputImagePath, String messageToHide, String password, Path outputImagePath)
      throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "encode");
    params.put("message", messageToHide == null ? "" : messageToHide);
    params.put("password", password == null ? "" : password);

    JsonNode result = postRequest(params, inputImagePath);
    if (result.path("error").asInt(Errors.WEBAPI_CONNECTION) != Errors.SUCCESS) {
      return result;
    }

    JsonNode encoded = result.get("encodedImage");
    if (encoded == null || encoded.isNull() || encoded.asText().isEmpty()) {
      return errorNode(Errors.OUTPUT_FILE);
    }
    try {
      byte[] binary = Base64.getDecoder().decode(encoded.asText());
      Files.write(outputImagePath, binary);
    } catch (IllegalArgumentException | IOException e) {
      return errorNode(Errors.OUTPUT_FILE);
    }

    if (result instanceof ObjectNode) {
      ((ObjectNode) result).remove("encodedImage");
    }
    return result;
  }

  public JsonNode decode(String inputImagePath, String password) throws IOException {
    return decode(inputImagePath == null ? null : Path.of(inputImagePath), password);
  }

  public JsonNode decode(Path inputImagePath, String password) throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "decode");
    params.put("password", password == null ? "" : password);
    return postRequest(params, inputImagePath);
  }

  public JsonNode postRequest(Map<String, String> paramsArray, Path imagePath) throws IOException {
    LinkedHashMap<String, String> params = new LinkedHashMap<>(paramsArray);

    if (apiKey != null && !apiKey.isEmpty()) {
      params.put("key", apiKey);
    }

    if (imagePath != null) {
      if (!Files.isRegularFile(imagePath)) {
        return errorNode(Errors.INVALID_INPUT);
      }
    }

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    for (Map.Entry<String, String> e : params.entrySet()) {
      builder.addTextBody(
          e.getKey(), e.getValue(), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
    }
    if (imagePath != null) {
      builder.addBinaryBody(
          "image",
          imagePath.toFile(),
          ContentType.APPLICATION_OCTET_STREAM,
          imagePath.getFileName().toString());
    }
    HttpEntity entity = builder.build();

    HttpPost post = new HttpPost(apiUrl);
    post.setEntity(entity);
    post.addHeader("User-Agent", "PELock Steganography Online Codec");

    try (CloseableHttpClient http = HttpClients.createDefault();
        CloseableHttpResponse response = http.execute(post)) {
      String body;
      try {
        body =
            response.getEntity() != null
                ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                : "";
      } catch (ParseException e) {
        return errorNode(Errors.WEBAPI_CONNECTION);
      }
      if (body == null || body.isEmpty()) {
        return errorNode(Errors.WEBAPI_CONNECTION);
      }
      JsonNode result = JSON.readTree(body);
      if (result == null || !result.isObject()) {
        return errorNode(Errors.WEBAPI_CONNECTION);
      }
      return result;
    } catch (IOException e) {
      return errorNode(Errors.WEBAPI_CONNECTION);
    }
  }

  public String convertSize(double sizeBytes) {
    if (sizeBytes <= 0) {
      return "0 bytes";
    }
    String[] sizeName = {"bytes", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
    int i = (int) Math.floor(Math.log(sizeBytes) / Math.log(1024));
    if (i < 0) i = 0;
    if (i >= sizeName.length) i = sizeName.length - 1;
    double p = Math.pow(1024, i);
    double s = Math.round(sizeBytes / p * 100.0) / 100.0;
    if (s == Math.rint(s)) {
      return ((long) s) + " " + sizeName[i];
    }
    return s + " " + sizeName[i];
  }

  private static ObjectNode errorNode(int error) {
    ObjectNode node = JSON.createObjectNode();
    node.put("error", error);
    return node;
  }
}
