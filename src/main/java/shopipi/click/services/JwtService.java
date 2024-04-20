package shopipi.click.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import shopipi.click.exceptions.NoAuthorizeError;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {
  private long JWT_EXPIRATION_TOKEN = 60 * 60 * 24 * 2;
  private long JWT_EXPIRATION_REFRESHTOKEN = 60 * 24 * 7;
  // private String accessToken, refreshToken;

  public static KeyPair generatorKeyPair() {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      KeyPair keyPair = kpg.generateKeyPair();
      return keyPair;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new Error("fail to generitor keyPair");
    }
  }

  public PublicKey getPublicKeyFromString(String base64PublicKey) {
    try {
      // Giải mã chuỗi Base64 để có mảng byte
      byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);

      // Tạo một X509EncodedKeySpec với mảng byte được giải mã
      X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

      // Sử dụng RSA algorithm để tạo đối tượng KeyFactory
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");

      // Tạo và trả về đối tượng PublicKey từ X509EncodedKeySpec
      return keyFactory.generatePublic(spec);
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  public String getStringFromPublicKey(PublicKey publicKey) {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  private String createToken(String payload, PrivateKey privateKey, long expiration) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expiration * 1000);
    // tạo chuỗi jwt từ email
    return Jwts.builder()
        .setSubject(payload)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.RS256, privateKey)
        .compact();
  }

  public String verifyToken(String token, PublicKey publicKey) {
    System.out.println(token);

    validateToken(token, publicKey);

    Claims claims = Jwts.parser()
        .setSigningKey(publicKey)
        .parseClaimsJws(token)
        .getBody();
    // trả lại thông tin email
    return claims.getSubject();
  }

  public String createAccessToken(String payload, PrivateKey privateKey) {
    return createToken(payload, privateKey, JWT_EXPIRATION_TOKEN);
  }

  public String createRefreshToken(String payload, PrivateKey privateKey) {
    return createToken(payload, privateKey, JWT_EXPIRATION_REFRESHTOKEN);
  }

  // validate thông tin của jwt
  public boolean validateToken(String token, PublicKey publickey) {
    try {
      Claims a = Jwts.parser()
          .setSigningKey(publickey)
          .parseClaimsJws(token)
          .getBody();

      log.info(a.getExpiration().toString());
      log.info(a.getIssuedAt().toString());

      return true;
    } catch (SignatureException ex) {
      log.error("Invalid token signature");
      throw new NoAuthorizeError("Invalid token signature");
    } catch (MalformedJwtException ex) {
      log.error("Invalid token Token");
      throw new NoAuthorizeError("Invalid token Token");
    } catch (ExpiredJwtException ex) {
      log.error("token Token expired", ex);
      throw new NoAuthorizeError("token Token expired");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported token Token");
      throw new NoAuthorizeError("Unsupported token Token");
    } catch (IllegalArgumentException ex) {
      log.error("token claims String is empty");
      throw new NoAuthorizeError("token claims String is empty");
    } catch (Exception ex) {
      log.error("Unknown token error", ex);
      throw new NoAuthorizeError("Unknown token error");
    }

  }

}
