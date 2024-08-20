package shopipi.click.models.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Data
@Builder
public class SearchRes implements Serializable {

  private PageCustom<Product> products;
  private PageCustom<User> shops;

}
