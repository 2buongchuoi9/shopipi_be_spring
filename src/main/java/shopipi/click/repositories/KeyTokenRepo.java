package shopipi.click.repositories;

import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.KeyToken;

import java.util.Optional;

@Repository
public interface KeyTokenRepo extends MongoRepository<KeyToken, String> {

  Optional<KeyToken> findByUserId(String userId);

  Optional<KeyToken> findByRefreshToken(String refreshToken);

  void deleteByUserId(String userId);

}
