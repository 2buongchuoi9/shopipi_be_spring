package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shopipi.click.configs.VnpayConfig;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.MomoService;
import shopipi.click.services.OrderService;

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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
  private final OrderService orderService;
  private final MomoService momoService;

  // use test
  // @GetMapping("/vnPay/create-payment")
  // public ResponseEntity<MainResponse<?>> createPayment(HttpServletRequest req)
  // throws UnsupportedEncodingException {

  // String vnp_Version = "2.1.0";
  // String vnp_Command = "pay";
  // String orderType = "other";
  // // long amount = Integer.parseInt(req.getParameter("amount")) * 100;
  // long amount = 100000 * 100;
  // String bankCode = "NCB";
  // // String bankCode = req.getParameter("bankCode");

  // String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
  // String vnp_IpAddr = VnpayConfig.getIpAddress(req);

  // String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

  // Map<String, String> vnp_Params = new HashMap<>();
  // vnp_Params.put("vnp_Version", vnp_Version);
  // vnp_Params.put("vnp_Command", vnp_Command);
  // vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
  // vnp_Params.put("vnp_Amount", String.valueOf(amount));
  // vnp_Params.put("vnp_CurrCode", "VND");

  // if (bankCode != null && !bankCode.isEmpty()) {
  // vnp_Params.put("vnp_BankCode", bankCode);
  // }
  // vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
  // vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
  // vnp_Params.put("vnp_OrderType", orderType);
  // vnp_Params.put("vnp_Locale", "vn");

  // vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
  // vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

  // Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
  // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
  // String vnp_CreateDate = formatter.format(cld.getTime());
  // vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

  // cld.add(Calendar.MINUTE, 15);
  // String vnp_ExpireDate = formatter.format(cld.getTime());
  // vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

  // List fieldNames = new ArrayList(vnp_Params.keySet());
  // Collections.sort(fieldNames);
  // StringBuilder hashData = new StringBuilder();
  // StringBuilder query = new StringBuilder();
  // Iterator itr = fieldNames.iterator();
  // while (itr.hasNext()) {
  // String fieldName = (String) itr.next();
  // String fieldValue = (String) vnp_Params.get(fieldName);
  // if ((fieldValue != null) && (fieldValue.length() > 0)) {
  // // Build hash data
  // hashData.append(fieldName);
  // hashData.append('=');
  // hashData.append(URLEncoder.encode(fieldValue,
  // StandardCharsets.US_ASCII.toString()));
  // // Build query
  // query.append(URLEncoder.encode(fieldName,
  // StandardCharsets.US_ASCII.toString()));
  // query.append('=');
  // query.append(URLEncoder.encode(fieldValue,
  // StandardCharsets.US_ASCII.toString()));
  // if (itr.hasNext()) {
  // query.append('&');
  // hashData.append('&');
  // }
  // }
  // }
  // String queryUrl = query.toString();
  // String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey,
  // hashData.toString());
  // queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
  // String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;

  // Map<String, String> result = new HashMap<>();
  // result.put("url", paymentUrl);

  // return ResponseEntity.ok().body(MainResponse.oke(result));
  // }

  @GetMapping("/vnPay/callback")
  public Object transaction(
      @RequestParam(value = "vnp_TransactionStatus") String cc,
      @RequestParam(value = "vnp_OrderInfo") String urlRedirect) throws JsonMappingException, JsonProcessingException {

    // check status payment
    String mes = VnpayConfig.getMessageIPN().keySet().stream()
        .map(v -> {
          if (v.equals(cc))
            return VnpayConfig.getMessageIPN().get(v);
          return "";
        })
        .filter(v -> !v.equals(""))
        .collect(Collectors.joining(" "));

    String[] parts = urlRedirect.split("\\?orderId=");

    // giao dich that bai xoa order va tra lai du lieu cua product & discount
    if (cc.equals("00") && cc.equals("07")) {
      var ids = parts[1].split(",");

      for (int i = 0; i < ids.length; i++) {
        var a = ids[i].trim();
        orderService.removeOrderAndReturnProductQuantityBecausePaymentFail(a);
      }
      // return ResponseEntity.ok().body(new
      // MainResponse<>(HttpStatus.PAYMENT_REQUIRED, mes));
      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(parts[0])
          // .queryParam("orderId", parts[1])
          .queryParam("code", cc)
          .queryParam("message", mes);

      return new RedirectView(builder.toUriString());
    }

    // return ResponseEntity.ok().body(MainResponse.oke(mes,
    // orderService.findOrderById(orderId)));

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(parts[0])
        .queryParam("orderId", parts[1])
        .queryParam("code", "200")
        .queryParam("message", mes);

    return new RedirectView(builder.toUriString());
  }

  @GetMapping("/momo/callback")
  public Object transactionMomo(@RequestParam(value = "resultCode") int resultCode,
      @RequestParam(value = "orderInfo") String orderInfo,
      @RequestParam(value = "message") String message)
      throws JsonMappingException, JsonProcessingException {

    String[] parts = orderInfo.split("\\?orderId=");

    // giao dich that bai xoa order va tra lai du lieu cua product & discount
    if (resultCode != 0) {
      var ids = parts[1].split(",");

      for (int i = 0; i < ids.length; i++) {
        var a = ids[i].trim();
        orderService.removeOrderAndReturnProductQuantityBecausePaymentFail(a);
      }

      // return ResponseEntity.ok()
      // .body(new MainResponse<>(HttpStatus.PAYMENT_REQUIRED, message + " code: " +
      // resultCode));

      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(parts[0])
          // .queryParam("orderId", parts[1])
          .queryParam("code", resultCode + "")
          .queryParam("message", message);

      return new RedirectView(builder.toUriString());
    }

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(parts[0])
        .queryParam("orderId", parts[1])
        .queryParam("message", message);

    return new RedirectView(builder.toUriString());
  }

  // @GetMapping("/momo/create-payment")
  // public ResponseEntity<MainResponse<?>> createPaymentMomo() throws Exception {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(MainResponse.oke(momoService.createPayment())));
  // }

}

// "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=40000000
// &vnp_BankCode=NCB
// &vnp_Command=pay
// &vnp_CreateDate=20240714113531
// &vnp_CurrCode=VND
// &vnp_ExpireDate=20240714115031
// &vnp_IpAddr=0%3A0%3A0%3A0%3A0%3A0%3A0%3A1
// &vnp_Locale=vn
// &vnp_OrderInfo=http%3A%2F%2Flocalhost%3A5174%3ForderId%3Dsdkpfjp
// &vnp_OrderType=other
// &vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8083%2Fapi%2Fv1%2Fpayment%2Fpayment-info
// &vnp_TmnCode=UD5RWEFZ
// &vnp_TxnRef=54232425
// &vnp_Version=2.1.0
// &vnp_SecureHash=b0d065f0de27538569e122e70311182de1b34558f0f3e636abb249a31e406ce99e63c197a9b8e97176926324afbfb323f3b24c610af4873f1cc6458026bf2cda"

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