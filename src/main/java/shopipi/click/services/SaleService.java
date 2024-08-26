package shopipi.click.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.entity.Sale;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.SaleParamsReq;
import shopipi.click.models.request.SaleReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.SaleRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;
import shopipi.click.utils._enum.StateDiscount;

@Service
@RequiredArgsConstructor
public class SaleService {
  private final SaleRepo saleRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct updateProduct;
  private final MongoTemplate mongoTemplate;

  @CacheEvict(value = "product", allEntries = true)
  public Sale addSale(SaleReq saleReq) {

    List<Product> products = productRepo.findAllById(saleReq.getProductIds());

    // kiểm tra xem sản phẩm đã tồn tại mã giảm giá chưa nếu có thì update xóa sản
    // phẩm đó ra khỏi mã giảm giá cũ
    products.forEach(product -> {
      if (product.getSale() != null) {
        // tìm mã giảm giá cũ và xóa sản phẩm ra khỏi mã giảm giá cũ
        Sale sale = saleRepo.findById(product.getSale().getId()).orElse(null);
        if (sale != null) {

          saleRepo.save(sale.removeProductId(product.getId()));
        }
        // xóa mã giảm giá cũ của sản phẩm
        updateProduct.priceSaleProduct(product, null);
      }
    });

    Sale saleSave = saleRepo.save(
        Sale.builder()
            .name(saleReq.getName())
            .shopId(saleReq.getShopId())
            .productIds(saleReq.getProductIds())
            .type(saleReq.getType())
            .value(saleReq.getValue())
            .dateStart(saleReq.getDateStart())
            .dateEnd(saleReq.getDateEnd())
            .build());

    // update price in product and save to database
    products.forEach(product -> updateProduct.priceSaleProduct(product, saleSave));
    return saleSave;
  }

  @CacheEvict(value = "product", allEntries = true)
  public Sale updateSale(String id, SaleReq saleReq) {
    Sale foundSale = saleRepo.findById(id).orElseThrow(() -> new NotFoundError(id, "Sale"));

    // Lấy danh sách sản phẩm trước và sau khi cập nhật
    // nếu danh sách trước ít hơn danh sách sau thì thêm mới
    // nếu danh sách trước nhiều hơn danh sách sau thì xóa
    List<Product> productsBefore = productRepo.findAllById(foundSale.getProductIds());
    List<Product> productsAfter = productRepo.findAllById(saleReq.getProductIds());

    // chỉ cập nhật tên hoặc thêm sản phẩm vào khuyến mãi
    foundSale.setName(saleReq.getName());
    foundSale.setProductIds(saleReq.getProductIds());
    Sale saleSave = saleRepo.save(foundSale);

    // Sản phẩm cần thêm mới
    List<Product> productsToAdd = productsAfter.stream()
        .filter(product -> !productsBefore.contains(product))
        .collect(Collectors.toList());

    // Sản phẩm cần xóa
    List<Product> productsToRemove = productsBefore.stream()
        .filter(product -> !productsAfter.contains(product))
        .collect(Collectors.toList());

    // kiểm tra xem sản phẩm đã tồn tại mã giảm giá chưa nếu có thì update xóa sản
    // phẩm đó ra khỏi mã giảm giá cũ
    productsToAdd.forEach(product -> {
      if (product.getSale() != null) {
        // tìm mã giảm giá cũ và xóa sản phẩm ra khỏi mã giảm giá cũ
        Sale sale = saleRepo.findById(product.getSale().getId()).orElse(null);
        if (sale != null) {
          saleRepo.save(sale.removeProductId(product.getId()));
        }
        // xóa mã giảm giá cũ của sản phẩm
        updateProduct.priceSaleProduct(product, null);
      }
    });

    // Thực hiện hành động thêm mới
    productsToAdd.forEach(product -> updateProduct.priceSaleProduct(product, saleSave));

    // Thực hiện hành động xóa
    productsToRemove.forEach(product -> updateProduct.priceSaleProduct(product, null));

    return saleSave;
  }

  @CacheEvict(value = "product", allEntries = true)
  public boolean deleteSale(String id) {
    Sale foundSale = saleRepo.findById(id).orElseThrow(() -> new NotFoundError(id, "Sale"));

    List<Product> products = productRepo.findAllById(foundSale.getProductIds());

    saleRepo.deleteById(id);

    // update price in product and save to database
    products.forEach(product -> updateProduct.priceSaleProduct(product, null));
    return true;
  }

  public PageCustom<Sale> find(Pageable pageable, SaleParamsReq params) {
    Query query = new Query();

    if (params.getShopId() != null & !params.getShopId().isEmpty())
      query.addCriteria(Criteria.where("shopId").is(params.getShopId()));

    if (params.getState() != null) {
      String state = params.getState();

      // lấy mã giảm giá đang hoạt động
      if (state.equals(StateDiscount.ACTIVE.name()))
        query.addCriteria(
            Criteria.where("dateStart").lte(LocalDateTime.now())
                .and("dateEnd").gt(LocalDateTime.now()));

      // lấy mã giảm giá đã kết thúc
      if (state.equals(StateDiscount.EXPIRED.name()))
        query.addCriteria(Criteria.where("dateEnd").lt(LocalDateTime.now()));

      // lấy mã giảm giá chưa kích hoạt
      if (state.equals(StateDiscount.NOT_YET_ACTIVE.name()))
        query.addCriteria(Criteria.where("dateStart").gt(LocalDateTime.now()));
    }

    long total = mongoTemplate.count(query, Sale.class);

    query.with(pageable);

    List<Sale> list = mongoTemplate.find(query, Sale.class).stream().map(Sale::setStateBasedOnDates)
        .toList();

    return new PageCustom<Sale>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }

  public Sale findById(String id) {
    return saleRepo.findById(id).orElseThrow(() -> new NotFoundError("sale not found")).setStateBasedOnDates();
  }

}
