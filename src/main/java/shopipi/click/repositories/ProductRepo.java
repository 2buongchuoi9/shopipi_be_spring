package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.productSchema.Product;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends MongoRepository<Product, String> {
  Optional<Product> findBySlug(String slug);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, String id);

  boolean existsByCategoryId(String id);
}
