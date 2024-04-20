package shopipi.click.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Rating;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.request.RatingReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.RatingRepo;
import shopipi.click.services.productService.IUpdateProduct;

@Service
@RequiredArgsConstructor
public class RatingService {
  private final RatingRepo ratingRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct iUpdateProduct;

  public Rating addOrUpdateRating(User user, RatingReq ratingReq) {

    Product foundProduct = productRepo.findById(ratingReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", ratingReq.getProductId()));

    Optional<Rating> foundRatingOpt = ratingRepo.findByProductIdAndUserId(foundProduct.getId(), user.getId());
    Integer oldValue = foundRatingOpt.isPresent() ? foundRatingOpt.get().getValue() : 0;
    Rating rating = foundRatingOpt.isPresent()
        ? foundRatingOpt.get()
        : Rating.builder()
            .productId(ratingReq.getProductId())
            .shopId(foundProduct.getShop().getId())
            .build();

    rating.setValue(ratingReq.getValue());
    rating = ratingRepo.save(rating);

    // Update product rating
    if (foundRatingOpt.isPresent())
      iUpdateProduct.afterUpdateRating(foundProduct, rating.getValue(), oldValue);

    else
      iUpdateProduct.afterAddRating(foundProduct, rating.getValue());

    return rating;
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

}
