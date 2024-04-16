package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.productSchema.ProductClothing;

@Repository
public interface ProductClothingRepo extends MongoRepository<ProductClothing, String> {

  boolean existsByColorAndSizeAndProductId(String color, String size, String id);

}
