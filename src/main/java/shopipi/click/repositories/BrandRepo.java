package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Brand;

@Repository
public interface BrandRepo extends MongoRepository<Brand, String> {

  boolean existsByName(String name);

}
