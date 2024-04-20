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
    CANCELLED,
    DELIVERED,
  }

  public enum TypePayment {
    CASH,
    MOMO,
    CARD_BANK,
  }

  public enum TypeDiscount {
    FIXED_AMOUNT, PERCENTAGE_AMOUNT
  }

}
