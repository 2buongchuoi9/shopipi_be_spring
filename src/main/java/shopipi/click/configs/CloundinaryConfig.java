package shopipi.click.configs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloundinaryConfig {
  @Value("${den.cloudinary.secret}")
  String apiSecret;
  @Value("${den.cloudinary.apiKey}")
  String apiKey;

  @Bean
  public Cloudinary getCloudinary() {
    Map<String, Object> config = new HashMap<>();
    config.put("cloud_name", "anhdaden");
    config.put("api_key", apiKey);
    config.put("api_secret", apiSecret);
    config.put("secure", true);
    return new Cloudinary(config);
  }
}
