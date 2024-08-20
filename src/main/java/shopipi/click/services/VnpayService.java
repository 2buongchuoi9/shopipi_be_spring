package shopipi.click.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import shopipi.click.configs.VnpayConfig;
import shopipi.click.entity.Order;

@Service
@RequiredArgsConstructor
public class VnpayService {
  private final OrderService orderService;

  public String createPaymentUrl(HttpServletRequest req, List<Order> orders, String urlClient)
      throws UnsupportedEncodingException, JsonProcessingException {

    // String ckeckoutString = new ObjectMapper().writeValueAsString(checkout);
    // String encodedCheckoutString = URLEncoder.encode(ckeckoutString,
    // StandardCharsets.UTF_8.toString());

    long amount = 0;

    for (Order order : orders) {
      amount += (order.getTotalCheckout() * 100l);
    }

    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String orderType = "other";
    // long amount = Integer.parseInt(req.getParameter("amount")) * 100;
    String bankCode = "NCB";
    // String bankCode = req.getParameter("bankCode");

    String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
    String vnp_IpAddr = VnpayConfig.getIpAddress(req);

    String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", vnp_Version);
    vnp_Params.put("vnp_Command", vnp_Command);
    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
    vnp_Params.put("vnp_Amount", String.valueOf(amount));
    vnp_Params.put("vnp_CurrCode", "VND");

    if (bankCode != null && !bankCode.isEmpty()) {
      vnp_Params.put("vnp_BankCode", bankCode);
    }

    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

    // chú ý sửa lại phần này
    // vnp_Params.put("vnp_OrderInfo", urlClient + "?orderId=" +
    // orders.get(0).getId());
    vnp_Params.put("vnp_OrderInfo", urlClient + "?orderId=" + "sdkpfjp");
    System.out.println(vnp_Params.get("vnp_OrderInfo"));

    vnp_Params.put("vnp_OrderType", orderType);
    vnp_Params.put("vnp_Locale", "vn");

    vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

    cld.add(Calendar.MINUTE, 15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

    List fieldNames = new ArrayList(vnp_Params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = (String) itr.next();
      String fieldValue = (String) vnp_Params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        // Build hash data
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        // Build query
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        if (itr.hasNext()) {
          query.append('&');
          hashData.append('&');
        }
      }
    }
    String queryUrl = query.toString();
    String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, hashData.toString());
    queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
    String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;

    return paymentUrl;
  }
}
