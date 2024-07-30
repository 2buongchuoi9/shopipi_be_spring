package shopipi.click.services;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Brand;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.repositories.BrandRepo;

@Service
@RequiredArgsConstructor
public class BrandService {
  private final BrandRepo brandRepo;
  private final MongoTemplate mongoTemplate;

  public Brand addBrand(Brand brand) {
    if (brandRepo.existsByName(brand.getName())) {
      throw new DuplicateRecordError("Brand already exists");
    }
    return brandRepo.save(brand);
  }

  public List<Brand> find(String keySearch) {
    Query query = new Query();

    if (keySearch != null && !keySearch.isEmpty()) {
      query.addCriteria(Criteria.where("name").regex(".*" + keySearch + ".*", "i"));
    }

    return mongoTemplate.find(query, Brand.class);
  }

}
