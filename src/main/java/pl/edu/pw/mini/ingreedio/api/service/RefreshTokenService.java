package pl.edu.pw.mini.ingreedio.api.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${security.refresh-token-lifetime}")
    private long refreshTokenLifetime;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthRepository authRepository;

    public RefreshToken refreshToken(RefreshToken token) {
        token.setToken(UUID.randomUUID().toString());
        token.setExpirationDate(Instant.now().plusMillis(refreshTokenLifetime));
        return refreshTokenRepository.save(token);
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
            .authInfo(authRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User '" + username + "' not found!")))
            .token(UUID.randomUUID().toString())
            .expirationDate(Instant.now().plusMillis(refreshTokenLifetime))
            .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpirationOfToken(RefreshToken token) {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired.");
        }

        return token;
    }
}
