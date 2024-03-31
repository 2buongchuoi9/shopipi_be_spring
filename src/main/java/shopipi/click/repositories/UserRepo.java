package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.User;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> findByNameAndIdNot(String name, String id);

  Boolean existsByNameAndIdNot(String name, String id);

  Optional<User> findByEmailAndIdNot(String email, String id);

  Boolean existsByEmailAndIdNot(String email, String id);
}
