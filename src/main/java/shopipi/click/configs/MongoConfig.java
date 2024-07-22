// package shopipi.click.configs;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
// import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

// import jakarta.annotation.PostConstruct;
// import shopipi.click.configs.localDateTimeConfig.LocalDateTimeToUTCConverter;
// import shopipi.click.configs.localDateTimeConfig.UTCToLocalDateTimeConverter;

// import java.util.Arrays;
// import java.util.TimeZone;

// @Configuration
// public class MongoConfig extends AbstractMongoClientConfiguration {

// @Value("${spring.data.mongodb.database}")
// private String database;

// @Override
// protected String getDatabaseName() {
// return database;
// }

// @Bean
// public MongoCustomConversions customConversions() {
// return new MongoCustomConversions(Arrays.asList(
// new UTCToLocalDateTimeConverter(),
// new LocalDateTimeToUTCConverter()));
// }

// // @PostConstruct
// // public void init() {
// // TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
// // }

// }
