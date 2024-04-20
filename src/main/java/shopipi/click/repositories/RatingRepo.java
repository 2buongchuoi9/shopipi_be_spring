package shopipi.click.repositories;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Rating;
import java.util.Optional;

@Repository
public interface RatingRepo extends MongoRepository<Rating, String> {

  @Query("{ 'productId' : ?0, 'user.id' : ?1 }")
  Optional<Rating> findByProductIdAndUserId(String productId, String userId);
}
