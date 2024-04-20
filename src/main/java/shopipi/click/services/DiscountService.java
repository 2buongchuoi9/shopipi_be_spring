package shopipi.click.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.models.request.DiscountReq;
import shopipi.click.repositories.DiscountRepo;
import shopipi.click.utils._enum.TypeDiscount;

@Service
@RequiredArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;

  public Discount addDiscount(DiscountReq discountReq) {
    // return discountRepo.save(discount);
    if (discountRepo.existsByCodeAndName(discountReq.getCode(), discountReq.getName()))
      throw new DuplicateRecordError("Discount code is already exists");

    return discountRepo.save(Discount.builder()
        .name(discountReq.getName())
        .code(discountReq.getCode())
        .type(discountReq.getType())
        .value(discountReq.getValue())
        .totalCount(discountReq.getTotalCount())
        .minOrderValue(discountReq.getMinOrderValue())
        .countUserUseDiscount(discountReq.getCountUserUseDiscount())
        .status(discountReq.getStatus())
        .dateStart(discountReq.getDateStart())
        .dateEnd(discountReq.getDateEnd())
        .build());
  }

  // calculate discount amount in 1 ShopOrderItemsModel
  /**
   * 
   * @param Item     : 1 ShopOrderItemsModel
   * @param discount
   * @param userId
   * @return Double[] : [amount, totalOrder]
   */
  public Double[] getDiscountAmount(ShopOrderItemsModel Item, Discount discount, String userId) {

    // check status and totalCount discount current
    if (!discount.getStatus() || discount.getTotalCount() <= 0)
      throw new NotFoundError("discount expired 'status or countDiscount'", discount.getId());

    // check date discount current
    if (LocalDateTime.now().isBefore(discount.getDateStart())
        || LocalDateTime.now().isAfter(discount.getDateEnd()))
      throw new NotFoundError("Discount expired 'date'");

    // check count use used
    if (discount.getUserUsedIds() != null && discount.getCountUserUseDiscount() > 0) {
      int length = discount.getUserUsedIds().stream()
          .filter(v -> v.equals(userId)).collect(Collectors.toList()).size();
      if (length >= discount.getCountUserUseDiscount())
        throw new BabRequestError(
            "discount requires a maximum of " + discount.getCountUserUseDiscount() + " uses per user");
    }

    // calculate total order
    Double totalOrder = Item.getItems().stream()
        .mapToDouble(v -> v.getPrice() * v.getQuantity()).sum();

    // check minOrder apply discount
    if (discount.getMinOrderValue() != null && discount.getMinOrderValue() > totalOrder)
      throw new BabRequestError(
          "Order value must be at least " + discount.getMinOrderValue() + " to apply the discount");

    // check type discount (so tien co dinh hoac theo phan tram)
    Double amount = discount.getType().equals(TypeDiscount.FIXED_AMOUNT.name()) ? discount.getValue()
        : totalOrder * (discount.getValue() / 100);

    Double[] result = { amount, totalOrder - amount };
    return result;
  }

}
