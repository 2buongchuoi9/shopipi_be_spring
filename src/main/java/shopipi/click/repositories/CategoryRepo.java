package shopipi.click.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Category;

@Repository
public interface CategoryRepo extends MongoRepository<Category, String> {
  List<Category> findByName(String name);

  boolean existsByNameAndIdNot(String name, String id);

  boolean existsByName(String name);

  Optional<Category> findBySlug(String slug);

  // Tìm tất cả danh mục có chứa ID cha trong danh sách parentIds
  List<Category> findAllByParentIdsContaining(String parentId);

  List<Category> findByParentIds(String categoryId);
}
