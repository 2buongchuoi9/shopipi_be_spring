// package shopipi.click.configs;

// import java.util.List;

// import org.bson.Document;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.index.Index;
// import org.springframework.data.mongodb.core.index.IndexOperations;

// import com.mongodb.client.MongoClient;
// import com.mongodb.client.MongoCollection;
// import com.mongodb.client.MongoCursor;
// import com.mongodb.client.MongoDatabase;
// import com.mongodb.client.MongoIterable;
// import com.mongodb.client.model.changestream.ChangeStreamDocument;

// import jakarta.annotation.PostConstruct;
// import shopipi.click.entity.Sale;
// import shopipi.click.entity.productSchema.Product;
// import shopipi.click.repositories.ProductRepo;
// import shopipi.click.repositories.SaleRepo;
// import shopipi.click.services.productService.IUpdateProduct;

// @Configuration
// public class MongoTTLConfig {

// @Autowired
// private MongoTemplate mongoTemplate;
// @Autowired
// private MongoClient mongoClient;

// @Autowired
// private SaleRepo saleRepo;

// @Autowired
// private ProductRepo productRepo;

// @Autowired
// private IUpdateProduct updateProduct;

// @Value("${spring.data.mongodb.database}")
// private String databaseName;

// // tự động xóa document ExpiryDateSale sau khi hết hạn
// @PostConstruct
// public void initIndexes() {
// try {
// IndexOperations indexOps = mongoTemplate.indexOps("ExpiryDateSale");
// Index index = new Index().on("expiryDate", Sort.Direction.ASC).expire(0); //
// TTL index
// indexOps.ensureIndex(index);
// } catch (Exception e) {
// e.printStackTrace(); // In ra lỗi nếu có
// }
// }

// @PostConstruct
// public void watchSaleExpiryDate() {
// try {
// MongoDatabase database = mongoClient.getDatabase("shopipi_fpt");
// MongoCollection<Document> collection =
// database.getCollection("ExpiryDateSale");

// MongoIterable<ChangeStreamDocument<Document>> changeStreamIterable =
// collection.watch();

// try (MongoCursor<ChangeStreamDocument<Document>> cursor =
// changeStreamIterable.iterator()) {
// while (cursor.hasNext()) {
// ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
// System.out.println(changeStreamDocument.getFullDocument());

// Document fullDocument = changeStreamDocument.getFullDocument();
// if (fullDocument != null) {
// String documentId = fullDocument.getObjectId("_id").toHexString();

// Sale sale = saleRepo.findById(documentId).orElse(null);

// if (sale == null)
// continue;

// List<Product> products = productRepo.findAllById(sale.getProductIds());

// products.forEach(product -> updateProduct.priceSaleProduct(product, null));
// }
// }
// }
// } catch (Exception e) {
// e.printStackTrace(); // In ra lỗi nếu có
// }
// }

// }
