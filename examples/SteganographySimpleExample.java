/******************************************************************************
 * Steganography Online Codec WebApi interface usage example.
 *
 * Version        : v1.0.2
 * Language       : Java
 * Author         : Bartosz Wójcik
 * Web page       : https://www.pelock.com
 *
 *****************************************************************************/

import com.fasterxml.jackson.databind.JsonNode;
import com.pelock.steganography.Errors;
import com.pelock.steganography.SteganographyOnlineCodec;

public class SteganographySimpleExample {

  public static void main(String[] args) throws Exception {
    SteganographyOnlineCodec client = new SteganographyOnlineCodec("YOUR-WEB-API-KEY");
    JsonNode result =
        client.encode(
            "input_file.jpg",
            "Secret message",
            "Pa$$word",
            "output_file_with_hidden_secret_message.png");

    if (result != null && result.has("error")) {
      if (result.get("error").asInt() == Errors.SUCCESS) {
        System.out.println("Secret message encoded and saved to the output PNG file.");
      } else {
        System.out.println("Error code " + result.get("error").asInt());
      }
    } else {
      System.out.println("Something unexpected happened while trying to encode the message.");
    }
  }
}
