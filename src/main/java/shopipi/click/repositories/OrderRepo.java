package shopipi.click.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Order;

@Repository
public interface OrderRepo extends MongoRepository<Order, String> {

  @Query("{ 'user.id' : ?0 }")
  List<Order> findByUserId(String userId);

  @Query("{ 'createdAt' : { $gte: ?0, $lt: ?1 } }")
  List<Order> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

  default List<Order> findByCreateDateBetween(LocalDate startDate, LocalDate endDate) {
    LocalDateTime startOfDay = startDate.atStartOfDay();
    LocalDateTime endOfDay = endDate.atStartOfDay().plusDays(1).minusSeconds(1);
    return findByCreateDateBetween(startOfDay, endOfDay);
  }

  @Query("{ 'user.id' : ?0, 'state' : ?1 }")
  List<Order> findByUserIdAndState(String userId, String state);

}
