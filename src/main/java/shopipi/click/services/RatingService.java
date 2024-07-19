package shopipi.click.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Rating;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.request.RatingReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.RatingRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;

@Service
@RequiredArgsConstructor
public class RatingService {
  private final RatingRepo ratingRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct iUpdateProduct;
  private final MongoTemplate mongoTemplate;

  public Rating addRating(User user, RatingReq ratingReq) {

    Product foundProduct = productRepo.findById(ratingReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", ratingReq.getProductId()));

    Rating rating = Rating.builder()
        .productId(ratingReq.getProductId())
        .shopId(foundProduct.getShop().getId())
        .user(user)
        .value(ratingReq.getValue())
        .comment(ratingReq.getComment())
        .images(ratingReq.getImages())
        .build();

    rating = ratingRepo.save(rating);

    iUpdateProduct.afterAddRating(foundProduct, rating.getValue());

    return rating;
  }

  public Rating updateRating(String id, User user, RatingReq ratingReq) {

    Rating foundRating = ratingRepo.findById(id)
        .orElseThrow(() -> new NotFoundError("ratingId", id));

    Product foundProduct = productRepo.findById(ratingReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", ratingReq.getProductId()));

    Integer oldValue = foundRating.getValue();

    foundRating.setValue(ratingReq.getValue());
    foundRating.setValue(ratingReq.getValue());
    foundRating.setImages(ratingReq.getImages());

    foundRating = ratingRepo.save(foundRating);

    // Update product rating

    iUpdateProduct.afterUpdateRating(foundProduct, foundRating.getValue(), oldValue);

    return foundRating;
  }

  public void deleteRating(String ratingId) {
    Rating foundRating = ratingRepo.findById(ratingId)
        .orElseThrow(() -> new NotFoundError("ratingId", ratingId));
    ratingRepo.delete(foundRating);

    // Update product rating
    Product foundProduct = productRepo.findById(foundRating.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", foundRating.getProductId()));
    iUpdateProduct.afterDeleteRating(foundProduct, foundRating.getValue());
  }

  public PageCustom<Rating> find(Pageable pageable, String productId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("productId").is(productId));

    long total = mongoTemplate.count(query, Rating.class);
    query.with(pageable);
    List<Rating> list = mongoTemplate.find(query, Rating.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }

}
