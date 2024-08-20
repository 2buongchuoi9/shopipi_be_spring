package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Comment;

@Repository
public interface CommentRepo extends MongoRepository<Comment, String> {
  long countByProductId(String productId);
}
