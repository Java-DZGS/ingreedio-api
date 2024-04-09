package pl.edu.pw.mini.ingreedio.api.security;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.repository.RefreshTokenRepository;

@Component
@RequiredArgsConstructor
public class TokenJanitor {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void clearOldTokens() {
        refreshTokenRepository.deleteAll(
            refreshTokenRepository.findAllByExpirationDateBefore(Instant.now()));
    }
}
