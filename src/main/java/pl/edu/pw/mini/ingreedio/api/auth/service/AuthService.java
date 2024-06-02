package pl.edu.pw.mini.ingreedio.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthInfoRepository;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtAuthTokens;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final AuthInfoRepository authInfoRepository;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JwtClaimsService jwtClaimsService;
    private final RoleService roleService;

    @Transactional
    public AuthInfo register(String username, String password, User user)
        throws UserAlreadyExistsException {
        AuthInfo authInfo = AuthInfo.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .user(user)
            .roles(roleService.getDefaultUserRoles())
            .build();

        try {
            authInfoRepository.save(authInfo);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }

        return authInfo;
    }

    @Transactional
    public JwtAuthTokens refresh(RefreshToken token) {
        AuthInfo authInfo = token.getAuthInfo();

        String jwtToken = jwtService.generateToken(jwtClaimsService
            .getJwtUserClaimsByAuthInfo(authInfo));
        RefreshToken refreshToken = refreshTokenService.refreshToken(token);
        return JwtAuthTokens.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    @Transactional
    public JwtAuthTokens login(String username, String password) throws AuthenticationException,
        ThrowableProblem {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        if (!authentication.isAuthenticated()) {
            throw Problem.valueOf(Status.UNAUTHORIZED);
        }

        AuthInfo authInfo = (AuthInfo) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(jwtClaimsService
            .getJwtUserClaimsByUsername(username));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authInfo);
        return JwtAuthTokens.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }


    @Transactional
    public void grantRole(AuthInfo userAuthInfo, Role role) {
        userAuthInfo.getRoles().add(role);
        authInfoRepository.save(userAuthInfo);
    }


    /**
     * @deprecated this method should not be ever used. If it's used, the code needs refactor.
     */
    @Transactional(readOnly = true)
    @Deprecated
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return null;
    }

    /**
     * @deprecated this method should not be ever used. If it's used, the code needs refactor.
     */
    @Transactional(readOnly = true)
    @Deprecated
    public AuthInfo getAuthInfoByUsername(String username) throws UsernameNotFoundException {
        return authInfoRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found!"));
    }
}