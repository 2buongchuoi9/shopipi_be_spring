package shopipi.click.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Category;

@Repository
public interface CategoryRepo extends MongoRepository<Category, String> {
  boolean existsByNameAndParentId(String name, String parentId);

  boolean existsByNameAndIdNot(String name, String id);

  boolean existsByParentId(String parentId);

  boolean existsByName(String name);

  @Query("{ $or: [ { _id: ?0 }, { parentId: ?0 } ] }")
  List<Category> findByIdOrParentId(String categoryId);
}
