package shopipi.click.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Cart;

@Repository
public interface CartRepo extends MongoRepository<Cart, String> {

  Optional<Cart> findByUserId(String id);

}
