package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Category;

@Repository
public interface CategoryRepo extends MongoRepository<Category, String> {
  boolean existsByNameAndParentId(String name, String parentId);

  boolean existsByNameAndIdNot(String name, String id);

  boolean existsByParentId(String parentId);

  boolean existsByName(String name);
}
