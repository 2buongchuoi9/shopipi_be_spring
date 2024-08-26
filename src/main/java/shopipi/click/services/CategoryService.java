package shopipi.click.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Category;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.CategoryRepo;
import shopipi.click.repositories.ProductRepo;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepo cateRepo;
  private final MongoTemplate mongoTemplate;
  private final ProductRepo productRepo;

  // [0,1,2] -> 0: parent, 1: parent of parent, 2: parent of parent of parent
  @CacheEvict(value = "categories", allEntries = true)
  public Category addCategory(Category cateReq) {

    if (cateReq.getParentIds() == null || cateReq.getParentIds().isEmpty() || cateReq.getParentIds().size() == 0)
      return cateRepo
          .save(Category.builder().name(cateReq.getName()).slug(cateReq.getSlug()).thumb(cateReq.getThumb()).build());

    if (checkCategoryExists(cateReq.getName(), cateReq.getParentIds().get(0)))
      throw new DuplicateRecordError("name, parentId", cateReq.getName() + ", " + cateReq.getParentIds().get(0));

    Category cate = Category.builder().name(cateReq.getName()).slug(cateReq.getSlug()).thumb(cateReq.getThumb())
        .parentIds(cateReq.getParentIds()).build();
    return cateRepo.save(cate);

  }

  // chỉ update name, thumb, slug
  // không update parentIds
  @CacheEvict(value = "categories", allEntries = true)
  public Category updateCategory(String id, Category cateReq) {

    // check id
    Category foundCate = cateRepo.findById(id).orElseThrow(() -> new NotFoundError("id", id));

    if (cateReq.getParentIds() != null && cateReq.getParentIds().size() > 0
        && checkCategoryUpdateExists(cateReq.getName(), cateReq.getParentIds().get(0), id))
      throw new DuplicateRecordError("name, parentId", cateReq.getName() + ", " + cateReq.getParentIds().get(0));

    foundCate.setName(cateReq.getName());
    foundCate.setThumb(cateReq.getThumb());
    foundCate.setSlug(cateReq.getSlug());
    // foundCate.setParentIds(cateReq.getParentIds());

    // Get all categories that are children or descendants of the updated category
    // List<Category> allCategories =
    // cateRepo.findAllByParentIdsContaining(foundCate.getId());

    // // Update the parentIds of all affected child categories
    // for (Category childCate : allCategories) {
    // // Update the parentIds array of the child category

    // List<String> updateChild = cateReq.getParentIds();
    // updateChild.add(0, foundCate.getId());

    // childCate.setParentIds(updateChild);
    // cateRepo.save(childCate);
    // }

    return cateRepo.save(foundCate);
  }

  @Cacheable(value = "categories", key = "#root.methodName")
  public List<Category> findAll() {
    return cateRepo.findAll();
  }

  @Cacheable(value = "categories", key = "#root.methodName + '_' + #slug")
  public Category findBySlug(String slug) {
    return cateRepo.findBySlug(slug).orElseThrow(() -> new NotFoundError("slug", slug));
  }

  @CacheEvict(value = "categories", allEntries = true)
  public Boolean deleteCategory(String id) {
    // check if exits child category
    Category foundCate = cateRepo.findById(id).orElseThrow(() -> new NotFoundError("id", id));

    // nếu có danh mục con thì không thể xóa
    if (cateRepo.findAllByParentIdsContaining(id).size() > 0)
      throw new BabRequestError("category has child category, can't delete. You must delete category child first.");

    // check if exits product in category
    if (productRepo.existsByCategoryId(id))
      throw new BabRequestError("category has product, can't delete. You must change news has category first.");

    // delete category
    cateRepo.delete(foundCate);

    return true;
  }

  private boolean checkCategoryExists(String name, String parentId) {
    List<Category> categories = cateRepo.findByName(name);
    for (Category category : categories) {
      if (!category.getParentIds().isEmpty() && category.getParentIds().get(0).equals(parentId)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkCategoryUpdateExists(String name, String parentId, String id) {
    List<Category> categories = cateRepo.findByName(name);
    for (Category category : categories) {
      if (!category.getParentIds().isEmpty() && category.getParentIds().get(0).equals(parentId)
          && !category.getId().equals(id)) {
        return true;
      }
    }
    return false;
  }

}
