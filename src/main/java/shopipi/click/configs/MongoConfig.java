// package shopipi.click.configs;

// import com.mongodb.ConnectionString;
// import com.mongodb.MongoClientSettings;
// import com.mongodb.client.MongoClients;

// import com.mongodb.client.MongoClient;
// // import com.mongodb.client.MongoDatabase;
// import org.bson.codecs.configuration.CodecRegistries;
// import org.bson.codecs.configuration.CodecRegistry;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.mongodb.MongoDatabaseFactory;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import
// org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
// import org.bson.codecs.pojo.PojoCodecProvider;

// @Configuration
// public class MongoConfig {
// @Value("${spring.data.mongodb.uri}")
// private String mongoUri;

// @Bean
// public MongoClient mongoClient() {

// CodecRegistry pojoCodecRegistry = CodecRegistries
// .fromProviders(PojoCodecProvider.builder().automatic(true).build());
// CodecRegistry codecRegistry =
// CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
// pojoCodecRegistry);

// MongoClientSettings settings = MongoClientSettings.builder()
// .applyConnectionString(new ConnectionString(mongoUri))
// .codecRegistry(codecRegistry)
// .build();

// return MongoClients.create(settings);
// }

// @Bean
// public MongoDatabaseFactory mongoDbFactory() {
// return new SimpleMongoClientDatabaseFactory(mongoClient(), "shopipi_fpt");
// }

// @Bean
// public MongoTemplate mongoTemplate() {
// return new MongoTemplate(mongoDbFactory());
// }
// }
