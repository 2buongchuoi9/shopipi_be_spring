package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.productSchema.ProductElectronic;

@Repository
public interface ProductElectronicRepo extends MongoRepository<ProductElectronic, String> {

  boolean existsByColorAndModelAndProductId(String color, String model, String id);

}
