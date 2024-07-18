package shopipi.click.services;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Category;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.CategoryRepo;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepo cateRepo;
  private final MongoTemplate mongoTemplate;

  public Category addCategory(Category cate) {

    if (cateRepo.existsByNameAndParentId(cate.getName(), cate.getParentId()))
      throw new DuplicateRecordError("name, parentId", cate.getName() + ", " + cate.getParentId());

    if (cate.getParentId() == null)
      return cateRepo.save(cate);

    Category parentCate = cateRepo.findById(cate.getParentId())
        .orElseThrow(() -> new NotFoundError("parentId", cate.getParentId()));

    cate.setParentName(parentCate.getName());
    return cateRepo.save(cate);

  }

  public Category updateCategory(String id, Category cate) {
    // check id
    Category foundCate = cateRepo.findById(id).orElseThrow(() -> new NotFoundError("id", id));

    if (!cateRepo.existsById(cate.getId()))
      throw new BabRequestError("Category does not exist");

    if (cateRepo.existsByNameAndIdNot(cate.getName(), id))
      throw new DuplicateRecordError("Category name already exists");

    foundCate.setName(cate.getName());
    foundCate.setParentId(cate.getParentId());
    foundCate.setThumb(cate.getThumb());
    foundCate.setSlug(cate.getSlug());

    if (foundCate.getParentId() == null)
      return cateRepo.save(foundCate);

    Category parentCate = cateRepo.findById(cate.getParentId())
        .orElseThrow(() -> new NotFoundError("parentId", cate.getParentId()));

    cate.setParentName(parentCate.getName());
    return cateRepo.save(foundCate);
  }

  public List<Category> findAll(String id, String parentId) {
    Query query = new Query();

    if (id != null && !id.isEmpty()) {
      if (cateRepo.existsById(id) == false)
        throw new NotFoundError("id", id);
      query.addCriteria(Criteria.where("id").is(id));
    }

    if (parentId != null && !parentId.isEmpty()) {
      if (!parentId.equals("null") && cateRepo.existsByParentId(parentId) == false)
        throw new NotFoundError("parentId", parentId);
      query.addCriteria(
          parentId.equals("null")
              ? new Criteria().orOperator(Criteria.where("parentId").exists(false), Criteria.where("parentId").is(null))
              : Criteria.where("parentId").is(parentId));
    }

    return mongoTemplate.find(query, Category.class);
  }

}
