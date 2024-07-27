package shopipi.click.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.OnlineStatusUser;

@Repository
public interface OnlineStatusRepo extends MongoRepository<OnlineStatusUser, String> {

  Optional<OnlineStatusUser> findByUserId(String id);

  @Query("{ 'userId' : { $in: ?0 } }")
  List<OnlineStatusUser> findAllByUserId(List<String> ids);

}
