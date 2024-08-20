package shopipi.click.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Notification;

@Repository
public interface NotifyRepo extends MongoRepository<Notification, String> {

  List<Notification> findByUserTo(String id);

  List<Notification> findByUserToAndDeliveredFalse(String id);
}
