package shopipi.click.services;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.models.paramsRequest.ProductParamsReq;
import shopipi.click.models.paramsRequest.UserParamReq;
import shopipi.click.models.response.SearchRes;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.ProductService;
import shopipi.click.utils._enum.ProductState;
import shopipi.click.utils._enum.UserRoleEnum;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final ProductService productService;
  private final UserService userService;

  public SearchRes searchProductAndShop(Pageable pageable, String keySearch) {

    // Search product
    PageCustom<Product> productPage = productService.findProduct(pageable,
        ProductParamsReq.builder().keySearch(keySearch).state(ProductState.ACTIVE.name()).build());

    // Search news
    PageCustom<User> shopPage = userService.findAll(pageable,
        UserParamReq.builder().keySearch(keySearch).role(UserRoleEnum.SHOP.name()).build());

    return SearchRes.builder().products(productPage).shops(shopPage).build();
  }

}