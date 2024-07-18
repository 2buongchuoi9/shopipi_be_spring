package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Chat;

@Repository
public interface ChatRepo extends MongoRepository<Chat, String> {

}
