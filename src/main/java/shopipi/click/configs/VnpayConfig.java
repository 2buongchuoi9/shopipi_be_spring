package shopipi.click.configs;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VnpayConfig {
  public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
  public static String vnp_ReturnUrl = "http://localhost:8083/api/payment/vnPay/callback";
  public static String vnp_TmnCode = "UD5RWEFZ";
  public static String secretKey = "DIQABKAQLOMURLDDQYXFFJVFPTQQXCIA";
  public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

  String vnp_Version = "2.1.0";
  String vnp_Command = "pay";
  String orderType = "other";

  public static String md5(String message) {
    String digest = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hash = md.digest(message.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
        sb.append(String.format("%02x", b & 0xff));
      }
      digest = sb.toString();
    } catch (UnsupportedEncodingException ex) {
      digest = "";
    } catch (NoSuchAlgorithmException ex) {
      digest = "";
    }
    return digest;
  }

  public static String Sha256(String message) {
    String digest = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(message.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
        sb.append(String.format("%02x", b & 0xff));
      }
      digest = sb.toString();
    } catch (UnsupportedEncodingException ex) {
      digest = "";
    } catch (NoSuchAlgorithmException ex) {
      digest = "";
    }
    return digest;
  }

  // Util for VNPAY
  public static String hashAllFields(Map fields) {
    List fieldNames = new ArrayList(fields.keySet());
    Collections.sort(fieldNames);
    StringBuilder sb = new StringBuilder();
    Iterator itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = (String) itr.next();
      String fieldValue = (String) fields.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        sb.append(fieldName);
        sb.append("=");
        sb.append(fieldValue);
      }
      if (itr.hasNext()) {
        sb.append("&");
      }
    }
    return hmacSHA512(secretKey, sb.toString());
  }

  public static String hmacSHA512(final String key, final String data) {
    try {

      if (key == null || data == null) {
        throw new NullPointerException();
      }
      final Mac hmac512 = Mac.getInstance("HmacSHA512");
      byte[] hmacKeyBytes = key.getBytes();
      final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
      hmac512.init(secretKey);
      byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
      byte[] result = hmac512.doFinal(dataBytes);
      StringBuilder sb = new StringBuilder(2 * result.length);
      for (byte b : result) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();

    } catch (Exception ex) {
      return "";
    }
  }

  public static String getIpAddress(HttpServletRequest request) {
    String ipAdress;
    try {
      ipAdress = request.getHeader("X-FORWARDED-FOR");
      if (ipAdress == null) {
        ipAdress = request.getRemoteAddr();
      }
    } catch (Exception e) {
      ipAdress = "Invalid IP:" + e.getMessage();
    }
    return ipAdress;
  }

  public static String getRandomNumber(int len) {
    Random rnd = new Random();
    String chars = "0123456789";
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(chars.charAt(rnd.nextInt(chars.length())));
    }
    return sb.toString();
  }

  public static Map<String, String> getMessageIPN() {
    Map<String, String> result = new HashMap<>();

    result.put("00", "Giao dịch thành công");
    result.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
    result.put("09",
        "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
    result.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
    result.put("11",
        "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
    result.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
    result.put("13",
        "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
    result.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
    result.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
    result.put("65",
        "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
    result.put("75", "Ngân hàng thanh toán đang bảo trì.");
    result.put("79",
        "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch");
    result.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

    return result;
  }

  // public static String _02 = "Merchant không hợp lệ (kiểm tra lại
  // vnp_TmnCode)";
  // public static String _03 = "Dữ liệu gửi sang không đúng định dạng";
  // public static String _91 = "Không tìm thấy giao dịch yêu cầu";
  // public static String _94 = "Yêu cầu bị trùng lặp trong thời gian giới hạn của
  // API (Giới hạn trong 5 phút)";
  // public static String _97 = "Chữ ký không hợp lệ";
  // public static String _99 = "Các lỗi khác (lỗi còn lại, không có trong danh
  // sách mã lỗi đã liệt kê)";

  // public static String _02 = "Tổng số tiền hoản trả lớn hơn số tiền gốc";
  // public static String _03 = "Dữ liệu gửi sang không đúng định dạng";
  // public static String _04 = "Không cho phép hoàn trả toàn phần sau khi hoàn
  // trả một phần";
  // public static String _13 = "Chỉ cho phép hoàn trả một phần";
  // public static String _91 = "Không tìm thấy giao dịch yêu cầu hoàn trả";
  // public static String _93 = "Số tiền hoàn trả không hợp lệ. Số tiền hoàn trả
  // phải nhỏ hơn hoặc bằng số tiền thanh toán.";
  // public static String _94 = "Yêu cầu bị trùng lặp trong thời gian giới hạn của
  // API (Giới hạn trong 5 phút)";
  // public static String _95 = "Giao dịch này không thành công bên VNPAY. VNPAY
  // từ chối xử lý yêu cầu.";
  // public static String _97 = "Chữ ký không hợp lệ";
  // public static String _98 = "Timeout Exception";
  // public static String _99 = "Các lỗi khác (lỗi còn lại, không có trong danh
  // sách mã lỗi đã liệt kê)";

}
