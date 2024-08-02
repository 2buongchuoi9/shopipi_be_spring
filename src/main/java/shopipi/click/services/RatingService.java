package shopipi.click.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import shopipi.click.applicationEvent.LikeEvent;
import shopipi.click.applicationEvent.RatingEvent;
import shopipi.click.entity.Comment;
import shopipi.click.entity.Image;
import shopipi.click.entity.Notification;
import shopipi.click.entity.Rating;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.RatingParamsReq;
import shopipi.click.models.request.RatingReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.RatingRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;
import shopipi.click.utils._enum.NotificationType;

@Service
@RequiredArgsConstructor
public class RatingService {
  private final RatingRepo ratingRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct iUpdateProduct;
  private final MongoTemplate mongoTemplate;
  private final ImageService fileService;
  private final ApplicationEventPublisher eventPublisher;

  public Rating addRating(User user, RatingReq ratingReq) {

    Product foundProduct = productRepo.findById(ratingReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", ratingReq.getProductId()));

    int rightValue;
    if (ratingReq.getParentId() != null) {
      Rating parent = ratingRepo.findById(ratingReq.getParentId())
          .orElseThrow(() -> new NotFoundError("parentId", ratingReq.getParentId()));
      rightValue = parent.getRight();
      // update right của các comment có right >= rightValue lên 2 đơn vị
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("right").gte(rightValue)),
          new Update().inc("right", 2),
          Rating.class);
      // update left của các comment có left > rightValue lên 2 đơn vị
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("left").gt(rightValue)),
          new Update().inc("left", 2),
          Rating.class);
    } else {
      // tìm comment cuối cùng của bài viết
      Rating lastComment = mongoTemplate.findOne(Query.query(Criteria.where("productId").is(ratingReq.getProductId()))
          .with(Sort.by(Sort.Direction.DESC, "right")).limit(1), Rating.class);
      rightValue = lastComment == null ? 1 : lastComment.getRight() + 2;
    }

    Rating rating = Rating.builder()
        .isComment(ratingReq.getIsComment())
        .parentId(ratingReq.getParentId())
        .productId(ratingReq.getProductId())
        .shopId(foundProduct.getShop().getId())
        .variantId(ratingReq.getVariantId())
        .user(user)
        .value(ratingReq.getValue())
        .comment(ratingReq.getComment())
        .images(ratingReq.getImages())
        .left(rightValue)
        .right(rightValue + 1)
        .build();

    rating = ratingRepo.save(rating);

    if (!ratingReq.getIsComment())
      iUpdateProduct.afterAddRating(foundProduct, rating.getValue());

    eventPublisher.publishEvent(new RatingEvent(this, rating));

    return rating;
  }

  public Rating updateRating(String id, User user, RatingReq ratingReq) {

    Rating foundRating = ratingRepo.findById(id)
        .orElseThrow(() -> new NotFoundError("ratingId", id));

    Product foundProduct = productRepo.findById(ratingReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", ratingReq.getProductId()));

    Integer oldValue = foundRating.getValue();

    foundRating.setValue(ratingReq.getValue());
    foundRating.setImages(ratingReq.getImages());
    foundRating.setComment(ratingReq.getComment());
    foundRating.setUser(user);

    foundRating = ratingRepo.save(foundRating);

    // Update product rating

    if (!foundRating.getIsComment())
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

    if (!foundRating.getIsComment())
      iUpdateProduct.afterDeleteRating(foundProduct, foundRating.getValue());
  }

  public PageCustom<Rating> findRatings(Pageable pageable, RatingParamsReq params) {
    Query query = new Query();

    // productId
    if (params.getProductId() != null && !params.getProductId().isEmpty())
      query.addCriteria(Criteria.where("productId").is(params.getProductId()));

    // parentId
    if (params.getParentId() != null && !params.getParentId().isEmpty())
      query.addCriteria(Criteria.where("parentId").is(params.getParentId()));

    // userId
    if (params.getUserId() != null && !params.getUserId().isEmpty())
      query.addCriteria(Criteria.where("user.id").is(params.getUserId()));

    // shopId
    if (params.getShopId() != null && !params.getShopId().isEmpty())
      query.addCriteria(Criteria.where("shopId").is(params.getShopId()));

    // is comment
    if (params.getIsComment() != null && !params.getIsComment())
      query.addCriteria(Criteria.where("isComment").is(params.getIsComment()));

    // rating value
    if (params.getValue() != null)
      query.addCriteria(Criteria.where("value").is(params.getValue()));

    // if has image
    if (params.getHasIMage() != null && params.getHasIMage())
      query.addCriteria(Criteria.where("images").ne(null).ne(Optional.empty()).ne(""));

    long total = mongoTemplate.count(query, Rating.class);
    query.with(pageable);
    List<Rating> list = mongoTemplate.find(query, Rating.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }

  public Rating likeRating(User user, String ratingId) {
    Rating rating = ratingRepo.findById(ratingId)
        .orElseThrow(() -> new NotFoundError("ratingId", ratingId));
    if (rating.getLikes() == null)
      rating.setLikes(new ArrayList<>());
    if (rating.getLikes().contains(user.getId())) { // đã like
      rating.getLikes().remove(user.getId());
    } else {
      rating.getLikes().add(user.getId());

      eventPublisher.publishEvent(new LikeEvent(this, Notification.builder()
          .userFrom(user)
          .userTo(rating.getUser().getId())
          .description(rating.getProductId())
          .content(user.getName() + " đã thích " + (rating.getIsComment() ? "bình luận" : "đánh giá") + " của bạn")
          .notificationType(NotificationType.NEW_LIKE)
          .build()));
    }

    return ratingRepo.save(rating);
  }

  public Rating addRatingWithFile(User user, String productId, String variantId, int value, String comment,
      List<MultipartFile> images) {

    if (images == null || images.isEmpty())
      return addRating(user,
          RatingReq.builder()
              .productId(productId)
              .variantId(variantId)
              .value(value)
              .comment(comment)
              .isComment(false)
              .build());
    else {
      List<String> imageUrls = new ArrayList<>();
      images.forEach(image -> {
        // save image
        Image file = fileService.addImageAndFile(image);
        imageUrls.add(file.getUrl());
      });

      return addRating(user,
          RatingReq.builder()
              .productId(productId)
              .variantId(variantId)
              .value(value)
              .comment(comment)
              .images(imageUrls)
              .isComment(false)
              .build());
    }
  }

  public Integer countRatingByShopId(String shopId) {
    // Lookup operation to join Product collection with Rating collection
    LookupOperation lookupOperation = LookupOperation.newLookup()
        .from("Products") // Collection to join with
        .localField("productId") // Field from Rating collection
        .foreignField("id") // Field from Product collection
        .as("productDetails"); // Alias for the joined data

    // Match operation to filter results based on shopId in the joined Product data
    MatchOperation matchOperation = Aggregation.match(Criteria.where("productDetails.shopId").is(shopId));

    // Match operation to filter results based on isComment = false
    MatchOperation matchIsComment = Aggregation.match(Criteria.where("isComment").is(false));

    // Create aggregation pipeline
    Aggregation aggregation = Aggregation.newAggregation(
        lookupOperation,
        matchOperation,
        matchIsComment);

    // Execute aggregation
    AggregationResults<Object> results = mongoTemplate.aggregate(aggregation, "Ratings", Object.class);

    // Return count of matching documents
    return results.getMappedResults().size();
  }

}
