package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Discount;

@Repository
public interface DiscountRepo extends MongoRepository<Discount, String> {

  boolean existsByCodeAndName(String code, String name);

}
