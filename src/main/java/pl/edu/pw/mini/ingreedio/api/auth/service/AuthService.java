package pl.edu.pw.mini.ingreedio.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.auth.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtClaimsService jwtClaimsService;
    private final RoleService roleService;

    public User register(RegisterRequestDto request) {
        User user = User.builder()
            .displayName(request.displayName())
            .email(request.email())
            .build();

        AuthInfo authInfo = AuthInfo.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .user(user)
            .roles(roleService.getDefaultUserRoles())
            .build();

        userRepository.save(user);
        authRepository.save(authInfo);

        return user;
    }

    public JwtResponseDto refresh(RefreshTokenRequestDto request) {
        return refreshTokenService.findByToken(request.refreshToken())
            .map(refreshTokenService::verifyExpirationOfToken)
            .map(token -> {
                AuthInfo authInfo = token.getAuthInfo();

                String jwtToken = jwtService.generateToken(jwtClaimsService
                    .getJwtUserClaimsByAuthInfo(authInfo));
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
            String jwtToken = jwtService.generateToken(jwtClaimsService
                .getJwtUserClaimsByUsername(request.username()));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());
            return JwtResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
        } else {
            throw new UsernameNotFoundException("User '" + request.username() + "' not found!");
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return null;
    }

    public AuthInfo getAuthInfoByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found!"));
    }
}