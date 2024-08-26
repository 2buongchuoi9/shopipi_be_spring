package shopipi.click;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PostConstruct;

@EnableMongoAuditing
@SpringBootApplication
@EnableAsync
@EnableCaching
public class ShopipiBeSpringApplication {

	@PostConstruct
	public void init() {
		// Thiết lập múi giờ mặc định
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ShopipiBeSpringApplication.class, args);
	}

}
