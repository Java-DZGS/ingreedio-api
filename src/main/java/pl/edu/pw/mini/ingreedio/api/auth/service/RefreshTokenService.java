package pl.edu.pw.mini.ingreedio.api.auth.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.auth.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.refresh-token-lifetime}")
    private long refreshTokenLifetime;

    @Transactional
    public RefreshToken refreshToken(RefreshToken token) {
        token.setToken(UUID.randomUUID().toString());
        token.setExpirationDate(Instant.now().plusMillis(refreshTokenLifetime));
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(AuthInfo authInfo) {
        RefreshToken refreshToken = RefreshToken.builder()
            .authInfo(authInfo)
            .token(UUID.randomUUID().toString())
            .expirationDate(Instant.now().plusMillis(refreshTokenLifetime))
            .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken getToken(String token) throws ThrowableProblem {
        return refreshTokenRepository.findByToken(token)
            .map(this::verifyExpirationOfToken)
            .orElseThrow(() -> Problem.valueOf(Status.UNAUTHORIZED));
    }

    @Transactional
    public RefreshToken verifyExpirationOfToken(RefreshToken token) throws ThrowableProblem {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw Problem.valueOf(Status.UNAUTHORIZED);
        }

        return token;
    }
}
