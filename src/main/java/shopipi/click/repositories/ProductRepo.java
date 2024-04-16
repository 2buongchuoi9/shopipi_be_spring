package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.productSchema.Product;

@Repository
public interface ProductRepo extends MongoRepository<Product, String> {

}
