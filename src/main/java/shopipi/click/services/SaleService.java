package shopipi.click.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Sale;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.SaleRepo;
import shopipi.click.services.productService.IUpdateProduct;

@Service
@RequiredArgsConstructor
public class SaleService {
  private final SaleRepo saleRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct updateProduct;

  public Sale addSale(Sale sale) {

    List<Product> products = productRepo.findAllById(sale.getProductIds());

    Sale saleSave = saleRepo.save(sale);

    // update price in product and save to database
    products.forEach(product -> updateProduct.priceSaleProduct(product, saleSave));
    return saleSave;
  }

  public Sale updateSale(String id, Sale sale) {
    Sale foundSale = saleRepo.findById(id).orElseThrow(() -> new NotFoundError(id, "Sale"));

    List<Product> products = productRepo.findAllById(sale.getProductIds());

    foundSale.setName(sale.getName());
    foundSale.setDateStart(sale.getDateStart());
    foundSale.setDateEnd(sale.getDateEnd());
    foundSale.setType(sale.getType());
    foundSale.setValue(sale.getValue());

    Sale saleSave = saleRepo.save(sale);

    // update price in product and save to database
    products.forEach(product -> updateProduct.priceSaleProduct(product, saleSave));
    return saleSave;
  }

  public void deleteSale(String id) {
    Sale foundSale = saleRepo.findById(id).orElseThrow(() -> new NotFoundError(id, "Sale"));

    List<Product> products = productRepo.findAllById(foundSale.getProductIds());

    saleRepo.deleteById(id);

    // update price in product and save to database
    products.forEach(product -> updateProduct.priceSaleProduct(product, null));
  }

}
