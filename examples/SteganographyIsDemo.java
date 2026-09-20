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
import com.pelock.steganography.SteganographyOnlineCodec;

public class SteganographyIsDemo {

  public static void main(String[] args) throws Exception {
    SteganographyOnlineCodec client = new SteganographyOnlineCodec("YOUR-WEB-API-KEY");
    JsonNode result = client.login();

    if (result != null && result.has("license")) {
      boolean full = result.path("license").path("activationStatus").asBoolean(false);
      System.out.println("You are running in " + (full ? "full" : "demo") + " version");
      if (full) {
        System.out.println("Registered for - " + result.path("license").path("userName").asText());
        System.out.println(
            "Remaining number of usage credits - "
                + result.path("license").path("usagesCount").asText());
      }
      System.out.println("Max. password length - " + result.path("limits").path("maxPasswordLen").asText());
    } else {
      System.out.println("Something unexpected happened while trying to login to the service.");
    }
  }
}
