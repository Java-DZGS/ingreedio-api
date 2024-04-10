package pl.edu.pw.mini.ingreedio.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.model.User;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public JwtResponseDto register(RegisterRequestDto request) {
        User user = User.builder()
            .displayName(request.displayName())
            .email(request.email())
            .build();

        AuthInfo authInfo = AuthInfo.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .user(user)
            .build();

        userRepository.save(user);
        authRepository.save(authInfo);

        String jwtToken = jwtService.generateToken(request.username());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());
        return JwtResponseDto.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken.getToken())
            .build();
    }

    public JwtResponseDto refresh(RefreshTokenRequestDto request) {
        return refreshTokenService.findByToken(request.refreshToken())
            .map(refreshTokenService::verifyExpirationOfToken)
            .map(token -> {
                AuthInfo authInfo = token.getAuthInfo();

                String jwtToken = jwtService.generateToken(authInfo.getUsername());
                RefreshToken refreshToken = refreshTokenService.refreshToken(token);
                return JwtResponseDto.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
            })
            .orElseThrow(() -> new RuntimeException("Invalid refresh token."));
    }

    public JwtResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
            )
        );

        if (authentication.isAuthenticated()) {
            String jwtToken = jwtService.generateToken(request.username());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());
            return JwtResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
        } else {
            throw new UsernameNotFoundException("User '" + request.username() + "' not found!");
        }
    }
}
