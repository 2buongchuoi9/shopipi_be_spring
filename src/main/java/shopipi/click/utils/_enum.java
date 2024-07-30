package shopipi.click.utils;

public final class _enum {

  public static enum UserRoleEnum {
    ADMIN,
    SHOP,
    USER,
    MOD;
  }

  public static enum AuthTypeEnum {
    LOCAL,
    FACEBOOK,
    GOOGLE,
  }

  public static enum ProductTypeEnum {
    CLOTHING,
    ELECTRONIC,
    TECH,
    BOOK,
    FOOD,
    OTHER,
  }

  public enum StateCartEnum {
    ACTIVE,
    COMPLETED,
    FAILED,
    PENDING,

  }

  public enum StateOrderEnum {
    PENDING,
    CONFIRMED,
    SHIPPING,
    DELIVERED,
    CANCELLED,
  }

  public enum TypePayment {
    CASH,
    MOMO,
    VN_PAY,
  }

  public enum TypeDiscount {
    FIXED_AMOUNT, PERCENTAGE_AMOUNT
  }

  public enum ChatType {
    TEXT,
    IMAGE,
    FILE,
  }

  public enum FileTypeEnum {
    IMAGE,
    VIDEO,
    ALL
  }

  public enum StateDiscount {
    ACTIVE,
    NOT_YET_ACTIVE,
    EXPIRED
  }

  public enum ProductState {
    PENDING,
    ACTIVE,
    HIDDEN,
    DELETED
  }

  public enum OrderShipping {
    FAST(25000.0),
    NORMAL(20000.0),
    GHTK(15000.0),
    HT(45000.0),
    NONE(0.0);

    private final Double cost;

    OrderShipping(Double cost) {
      this.cost = cost;
    }

    public Double getCost() {
      return cost;
    }
  }

  public enum NotificationType {
    LIKE, COMMENT, SHARE, NEW_PRODUCT, NEW_ORDER, NEW_SHOP, NEW_USER, NEW_COMMENT, NEW_REPLY, NEW_FOLLOW, NEW_MESSAGE
  }

}
