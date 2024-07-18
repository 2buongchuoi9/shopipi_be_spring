package shopipi.click.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.productSchema.Variant;

@Repository
public interface VariantRepo extends MongoRepository<Variant, String> {

  List<Variant> findByProductId(String id);

}
