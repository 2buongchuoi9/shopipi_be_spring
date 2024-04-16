package shopipi.click;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class ShopipiBeSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopipiBeSpringApplication.class, args);
	}

}
