package shopipi.click.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.KeyToken;
import shopipi.click.repositories.KeyTokenRepo;

@Service
@SuppressWarnings("null")
@RequiredArgsConstructor
public class KeyTokenService {
  final private KeyTokenRepo keyRepo;

  public boolean createKeyStore(String userId, String publicKey, String refreshToken) {
    return createKeyStore(userId, publicKey, refreshToken, null);
  }

  public boolean createKeyStore(String userId, String publicKey, String refreshToken, List<String> refreshTokenUsed) {
    try {
      Optional<KeyToken> oKey = keyRepo.findByUserId(userId);
      if (oKey.isPresent()) {
        KeyToken k = oKey.get();
        k.setPublicKey(publicKey);
        k.setRefreshToken(refreshToken);
        k.setRefreshTokensUsed(refreshTokenUsed);
        keyRepo.save(k);
      } else
        keyRepo.save(KeyToken.builder()
            .userId(userId)
            .publicKey(publicKey)
            .refreshToken(refreshToken)
            .refreshTokensUsed(refreshTokenUsed)
            .build());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
