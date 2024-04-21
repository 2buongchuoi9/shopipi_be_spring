package shopipi.click.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Cart;
import shopipi.click.entity.Discount;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.models.ShopOrderItemsModel.ProductItemsModel;
import shopipi.click.models.request.CartReq;
import shopipi.click.repositories.CartRepo;
import shopipi.click.repositories.DiscountRepo;
import shopipi.click.repositories.ProductRepo;

@Service
@RequiredArgsConstructor
public class CartService {
  private final CartRepo cartRepo;
  private final ProductRepo productRepo;
  private final DiscountRepo discountRepo;
  private final DiscountService discountService;

  /**
   * {
   * "id": "1",
   * "userId": "1",
   * "shopOrderItems":
   * [
   * {
   * "shopId": "1",
   * "discountId": "1",
   * "items": {
   * "productId": "1",
   * "attributeId": "1",
   * "quantity": 1
   * }
   * }
   * }
   */
  public Cart addToCart(User user, CartReq cartReq) {
    ProductItemsModel item = checkoutProductServer(cartReq);

    // don't check discountId
    Optional<Cart> cart = cartRepo.findByUserId(user.getId());
    if (cart.isPresent()) {
      return updateAddToCart(cart.get(), item);
    } else {
      return createCart(user, item);
    }

  }

  public Cart addDiscountToShop(User user, String shopId, String discountId) {
    Cart cart = cartRepo.findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundError("Cart not found"));

    Discount discount = discountRepo.findById(discountId)
        .orElseThrow(() -> new NotFoundError("Discount not found"));

    List<ShopOrderItemsModel> shopOrderItems = cart.getShopOrderItems();
    Optional<ShopOrderItemsModel> shopOrderItemOpt = shopOrderItems.stream()
        .filter(shopOrderItem -> shopOrderItem.getShopId().equals(shopId))
        .findFirst();

    if (shopOrderItemOpt.isEmpty())
      throw new NotFoundError("Shop not found");

    ShopOrderItemsModel shopOrderItem = shopOrderItemOpt.get();
    if (!discount.getShop().getId().equals(shopOrderItem.getShopId()))
      throw new BabRequestError("Discount not belong to shop");

    shopOrderItem.setDiscountId(discountId);

    // calculator totalDiscount for 1 shopOrderItem
    Double[] totalsShopOrderItem = discountService.getDiscountAmount(shopOrderItem, discount, user.getId());
    shopOrderItem.setTotalDiscount(totalsShopOrderItem[0]);
    shopOrderItem.setTotal(totalsShopOrderItem[1]);

    // calculator total price and discount in cart
    calculateTotalCart(cart, shopOrderItems);

    return cartRepo.save(cart);

  }

  public Cart getCart(User user) {
    return cartRepo.findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundError("Cart is empty"));
  }

  public void removeProductItemsToCart(User user, String attributeId) {
    Cart cart = cartRepo.findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundError("Cart not found"));

    List<ShopOrderItemsModel> shopOrderItems = cart.getShopOrderItems();
    shopOrderItems.forEach(shopOrderItem -> {
      shopOrderItem.getItems().removeIf(productItem -> productItem.getAttribute().getId().equals(attributeId));
    });

    // remove empty shopOrderItem
    shopOrderItems.removeIf(shopOrderItem -> shopOrderItem.getItems().isEmpty());

    // calculator total price and discount in cart
    calculateTotalCart(cart, shopOrderItems);

    cartRepo.save(cart);
  }

  public ProductItemsModel checkoutProductServer(CartReq cartReq) {
    // check product exist
    Product foundProduct = productRepo.findById(cartReq.getProductId())
        .orElseThrow(() -> new NotFoundError("Product not found"));

    // check attribute exist
    Attribute attribute = foundProduct.getAttributes().stream()
        .filter(attr -> attr.getId().equals(cartReq.getAttributeId()))
        .findFirst()
        .orElseThrow(() -> new NotFoundError("Attribute not found"));

    // check quantity
    if (cartReq.getQuantity() > attribute.getQuantity())
      throw new BabRequestError("quantity's product " + foundProduct.getId() + " in inventory is "
          + attribute.getQuantity() + " ,quantity must less than it");

    // call product service to get product detail
    return ProductItemsModel.builder()
        .product(foundProduct)
        .attribute(attribute)
        .quantity(cartReq.getQuantity())
        .price(foundProduct.getPrice())
        .build();
  }

  // all private method below is for update cart
  private Cart createCart(User user, ProductItemsModel item) {
    // calculator total price and discount
    double totalPrice = item.getPrice() * item.getQuantity();

    Cart cart = Cart.builder()
        .userId(user.getId())
        .shopOrderItems(Arrays.asList(ShopOrderItemsModel.builder()
            .shopId(item.getProduct().getShop().getId())
            .items(Arrays.asList(item))
            .total(item.getPrice() * item.getQuantity())
            .build()))
        .total(totalPrice)
        .build();

    return cartRepo.save(cart);
  }

  private Cart updateAddToCart(Cart cart, ProductItemsModel item) {
    List<ShopOrderItemsModel> shopOrderItems = cart.getShopOrderItems();
    Optional<ShopOrderItemsModel> shopOrderItemOpt = shopOrderItems.stream()
        .filter(shopOrderItem -> shopOrderItem.getShopId().equals(item.getProduct().getShop().getId()))
        .findFirst();

    if (shopOrderItemOpt.isPresent()) {
      ShopOrderItemsModel shopOrderItem = shopOrderItemOpt.get();
      int index = shopOrderItems.indexOf(shopOrderItem);
      shopOrderItems.set(index, updateShopOrderItem(shopOrderItemOpt.get(), item));
      cart.setShopOrderItems(shopOrderItems);
    } else
      addNewShopOrderItem(shopOrderItems, item);

    // remove empty shopOrderItem or quantity = 0
    shopOrderItems
        .forEach(shopOrderItem -> shopOrderItem.getItems().removeIf(productItem -> productItem.getQuantity() == 0));
    shopOrderItems.removeIf(shopOrderItem -> shopOrderItem.getItems().isEmpty());

    // calculator total price and discount
    calculateTotalCart(cart, shopOrderItems);

    // remove cart if shopOrderItems is empty
    if (shopOrderItems.isEmpty()) {
      cartRepo.delete(cart);
      return null;
    }
    cart.setShopOrderItems(shopOrderItems);
    cart.getShopOrderItems().stream().forEach(v -> System.out.println("sdajbhfkasjnas:" + v));

    return cartRepo.save(cart);
  }

  private ShopOrderItemsModel updateShopOrderItem(ShopOrderItemsModel shopOrderItem, ProductItemsModel item) {
    List<ProductItemsModel> productItems = shopOrderItem.getItems();
    Optional<ProductItemsModel> productItemOpt = productItems.stream()
        .filter(productItem -> productItem.getAttribute().getId().equals(item.getAttribute().getId()))
        .findFirst();

    if (productItemOpt.isPresent()) {
      ProductItemsModel productItem = productItemOpt.get();
      int quantity = item.getQuantity();
      productItems.get(productItems.indexOf(productItem)).setQuantity(quantity < 0 ? 0 : quantity);
    } else {
      int quantity = item.getQuantity();
      item.setQuantity(quantity < 0 ? 0 : quantity);
      productItems.add(item);
    }
    shopOrderItem.setTotal(productItems.stream()
        .mapToDouble(productItem -> productItem.getPrice() * productItem.getQuantity())
        .sum());
    return shopOrderItem;
  }

  private void addNewShopOrderItem(List<ShopOrderItemsModel> shopOrderItems, ProductItemsModel item) {
    shopOrderItems.add(ShopOrderItemsModel.builder()
        .discountId(item.getProduct().getId())
        .shopId(item.getProduct().getShop().getId())
        .items(Arrays.asList(item))
        .total(item.getPrice() * item.getQuantity())
        .build());
  }

  private void calculateTotalCart(Cart cart, List<ShopOrderItemsModel> shopOrderItems) {
    double total = shopOrderItems.stream()
        .mapToDouble(ShopOrderItemsModel::getTotal)
        .sum();
    double totalDiscount = shopOrderItems.stream()
        .mapToDouble(ShopOrderItemsModel::getTotalDiscount)
        .sum();

    cart.setTotalDiscount(totalDiscount);
    cart.setTotal(total);
  }

}
