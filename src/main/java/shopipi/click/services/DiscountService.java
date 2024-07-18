package shopipi.click.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.models.paramsRequest.DiscountParamsReq;
import shopipi.click.models.request.DiscountReq;
import shopipi.click.repositories.DiscountRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.utils._enum.StateDiscount;
import shopipi.click.utils._enum.TypeDiscount;

@Service
@RequiredArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  private final MongoTemplate mongoTemplate;

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
        .currentCount(discountReq.getTotalCount())
        .minOrderValue(discountReq.getMinOrderValue())
        .countUserUseDiscount(discountReq.getCountUserUseDiscount())
        .status(discountReq.getStatus())
        .dateStart(discountReq.getDateStart())
        .dateEnd(discountReq.getDateEnd())
        .build())
        .setStateBasedOnDates();

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

    Double[] result = { amount, totalOrder };
    return result;
  }

  public Discount findById(String id) {
    return discountRepo.findById(id).orElseThrow(() -> new NotFoundError("Discount not found")).setStateBasedOnDates();
  }

  public PageCustom<Discount> finDiscounts(Pageable pageable, DiscountParamsReq params) {
    Query query = new Query();

    if (params.getShopId() != null) {
      query.addCriteria(Criteria.where("shop.id").is(params.getShopId()));
    }

    if (params.getKeySearch() != null) {
      String regexPattern = ".*" + params.getKeySearch() + ".*";
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("name").regex(regexPattern, "i"),
          Criteria.where("code").regex(regexPattern, "i")));
    }

    if (params.getState() != null) {
      String state = params.getState();

      // lấy mã giảm giá đang hoạt động
      if (state.equals(StateDiscount.ACTIVE.name()))
        query.addCriteria(Criteria.where("status").is(true)
            .and("dateStart").lte(LocalDateTime.now())
            .and("dateEnd").gt(LocalDateTime.now()));

      // lấy mã giảm giá đã kết thúc
      if (state.equals(StateDiscount.EXPIRED.name()))
        query.addCriteria(Criteria.where("dateEnd").lt(LocalDateTime.now()));

      // lấy mã giảm giá chưa kích hoạt
      if (state.equals(StateDiscount.NOT_YET_ACTIVE.name()))
        query.addCriteria(Criteria.where("dateStart").gt(LocalDateTime.now()));
    }

    Long total = mongoTemplate.count(query, Discount.class);
    List<Discount> list = mongoTemplate.find(query, Discount.class).stream().map(Discount::setStateBasedOnDates)
        .toList();

    // list.forEach(v -> {
    // v.setCurrentCount(v.getTotalCount() - v.getUserUsedIds().size());
    // discountRepo.save(v);
    // });

    return new PageCustom<Discount>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public Discount updateDiscount(String id, @Valid DiscountReq discountReq) {
    Discount discount = discountRepo.findById(id).orElseThrow(() -> new NotFoundError("Discount not found"));

    if (discountRepo.existsByCodeAndName(discountReq.getCode(), discountReq.getName())
        && !discount.getCode().equals(discountReq.getCode()))
      throw new DuplicateRecordError("Discount code is already exists");

    discount.setName(discountReq.getName());

    discount.setTotalCount(discountReq.getTotalCount());

    discount.setDateEnd(discountReq.getDateEnd());

    return discountRepo.save(discount).setStateBasedOnDates();
  }

}
