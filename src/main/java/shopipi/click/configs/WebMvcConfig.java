package shopipi.click.configs;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SuppressWarnings("null")
public class WebMvcConfig {

  @Value("${den.date.format}")
  public static final String dateTimeFormat = "dd-MM-yyyy HH:mm:ss";

  public static final String regexpEmail = "^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,}$";

  @Bean
  public WebMvcConfigurer corsConfig() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // registry.addMapping("/**")
        // .allowedHeaders("*")
        // .allowedMethods("GET", "POST", "OPTIONS")
        // .allowedOrigins("http://localhost:5173")
        // .allowCredentials(true);

        registry.addMapping("/**")
            .allowedOrigins("/*")
            .allowedMethods("GET", "POST", "OPTIONS", "DELETE", "PUT")
            .allowedHeaders("*")
            .allowCredentials(true);

      }
    };
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(
        new MediaType("application", "json", Charset.forName("UTF-8"))));
    return converter;
  }

}
