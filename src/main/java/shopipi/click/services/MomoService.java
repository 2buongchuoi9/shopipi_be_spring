package shopipi.click.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import shopipi.click.entity.Order;

@Service
public class MomoService {
  private static final String PARTNER_CODE = "MOMO";
  private static final String ACCESS_KEY = "F8BBA842ECF85";
  private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
  @Value("${momo.redirect-url}")
  private String REDIRECT_URL;
  @Value("${momo.redirect-url}")
  private String IPN_URL;
  private static final String REQUEST_TYPE = "captureWallet";

  public Map<String, Object> createPayment(List<Order> orders, String urlClient) throws Exception {
    String requestId = PARTNER_CODE + System.currentTimeMillis();
    String orderId = requestId;
    String orderInfo = urlClient + "?orderId=" + orders.stream().map(v -> v.getId()).collect(Collectors.joining(","));
    String amount = String.valueOf(Math.round(orders.stream().mapToDouble(Order::getTotalCheckout).sum()));

    String extraData = "";
    String encodedIpnUrl = IPN_URL;
    String rawSignature = "accessKey=" + ACCESS_KEY +
        "&amount=" + amount +
        "&extraData=" + extraData +
        "&ipnUrl=" + encodedIpnUrl +
        "&orderId=" + orderId +
        "&orderInfo=" + orderInfo +
        "&partnerCode=" + PARTNER_CODE +
        "&redirectUrl=" + REDIRECT_URL +
        "&requestId=" + requestId +
        "&requestType=" + REQUEST_TYPE;

    String signature = signHmacSHA256(rawSignature, SECRET_KEY);

    Map<String, String> paymentRequest = new HashMap<>();
    paymentRequest.put("partnerCode", PARTNER_CODE);
    paymentRequest.put("accessKey", ACCESS_KEY);
    paymentRequest.put("requestId", requestId);
    paymentRequest.put("amount", amount);
    paymentRequest.put("orderId", orderId);
    paymentRequest.put("orderInfo", orderInfo);
    paymentRequest.put("redirectUrl", REDIRECT_URL);
    paymentRequest.put("ipnUrl", encodedIpnUrl);
    paymentRequest.put("extraData", extraData);
    paymentRequest.put("requestType", REQUEST_TYPE);
    paymentRequest.put("signature", signature);
    paymentRequest.put("lang", "vi");

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(paymentRequest);

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/create");
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Content-Type", "application/json");

    String response = EntityUtils.toString(client.execute(httpPost).getEntity());
    client.close();

    // Parse the response if needed and return the payment URL
    Map<String, Object> responseMap = mapper.readValue(response, HashMap.class);
    return responseMap;
  }

  private String signHmacSHA256(String data, String key) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
    mac.init(secretKeySpec);
    byte[] hmacData = mac.doFinal(data.getBytes());
    StringBuilder sb = new StringBuilder();
    for (byte b : hmacData) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

}

// /api/payment/momo/callback
// ?partnerCode=MOMO
// &orderId=MOMO1721014629740
// &requestId=MOMO1721014629740
// &amount=50000
// &orderInfo=pay+with+MoMo
// &orderType=momo_wallet
// &transId=4084757146
// &resultCode=0
// &message=Th%C3%A0nh+c%C3%B4ng.
// &payType=qr
// &responseTime=1721014660600
// &extraData=
// &signature=a5f1ec2a40181e82ae63153bad277bc6d46451a6724fecc88a92a6e869ea2621

// https://test-payment.momo.vn/v2/gateway/https%3A%2F%2F5e2e-123-21-184-249.ngrok-free.app%2Fapi%2Fpayment%2Fmomo%2Fcallback?partnerCode=MOMO&orderId=MOMO1721019135924&requestId=MOMO1721019135924&amount=618000&orderInfo=6694aafb4e133e31569ea5f2&orderType=momo_wallet&transId=4084773179&resultCode=0&message=Th%C3%A0nh+c%C3%B4ng.&payType=qr&responseTime=1721019200114&extraData=&signature=e0ea4ffa9618e0778a1415a8922e55baca15cc2c1d8a95c50e11bfe8fdf3b3a8