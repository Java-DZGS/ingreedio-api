package pl.edu.pw.mini.ingreedio.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.repository.RoleRepository;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtAuthTokens;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.auth.service.JwtClaimsService;
import pl.edu.pw.mini.ingreedio.api.auth.service.JwtService;
import pl.edu.pw.mini.ingreedio.api.auth.service.RefreshTokenService;
import pl.edu.pw.mini.ingreedio.api.auth.service.RoleService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;
import pl.edu.pw.mini.ingreedio.api.user.service.UserService;

public class AuthServiceTest extends IntegrationTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtClaimsService jwtClaimsService;
    @Autowired
    private UserService userService;

    private User dummyUser;

    @BeforeEach
    public void setupData() {
        dummyUser = userService.createUser("Dummy", "dummy@example.com");
    }

    @Test
    public void givenValidSignupData_whenRegister_thenSuccess() {
        // Given
        User user = dummyUser;

        // When
        AuthInfo info = authService.register("us", "as", user);

        // Then
        assertThat(info).isNotNull();
        assertThat(info.getUsername()).isEqualTo("us");
        assertThat(passwordEncoder.matches("as", info.getPassword())).isTrue();
        assertThat(info.getUser()).isNotNull();
        assertThat(info.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void givenDuplicatedUsername_whenRegister_thenExceptionThrown() {
        // Given
        User user = dummyUser;

        // When
        Exception problem = catchException(() -> authService.register("user", "user", user));

        // Then
        assertThat(problem).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    public void givenValidSignupData_whenRegister_thenNewUserHasDefaultRoles() {
        // Given
        User user = dummyUser;
        AuthInfo authInfo = authService.register("us", "us", user);

        // When
        Set<String> defaultRoles = roleService.getDefaultUserRoles().stream()
            .map(Role::getName).collect(Collectors.toSet());
        Set<String> newUserRoles = authInfo.getRoles().stream()
            .map(Role::getName).collect(Collectors.toSet());

        // Then
        assertThat(newUserRoles).isEqualTo(defaultRoles);
    }

    @Test
    void givenValidLoginCredentials_whenLogin_thenTokensGenerated() {
        // Given
        final String username = "user";
        final String password = "user";

        // When
        JwtAuthTokens response = authService.login(username, password);
        JwtUserClaims claims = jwtClaimsService.getJwtUserClaimsByUsername(username);

        // Then
        assertThat(response.accessToken()).isNotNull();
        assertThat(jwtService.extractUsername(response.accessToken())).isEqualTo("user");
        assertThat(jwtService.isTokenValid(response.accessToken(), claims)).isTrue();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    void givenInvalidUsernameLoginCredentials_whenLogin_thenExceptionThrown() {
        // Given
        String username = "invalid";
        String password = "password";

        // When
        Exception exception = catchException(() -> authService.login(username, password));

        // Then
        assertThat(exception).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void givenWrongPasswordLoginCredentials_whenLogin_thenExceptionThrown() {
        // Given
        String username = "user";
        String password = "password";

        // When
        Exception exception = catchException(() -> authService.login(username, password));

        // Then
        assertThat(exception).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void givenValidRefreshToken_whenRefresh_thenNewTokenGenerated() {
        // Given
        JwtAuthTokens loginResponse = authService.login("user", "user");

        // When
        JwtAuthTokens response = authService.refresh(loginResponse.refreshToken());
        JwtUserClaims claims = jwtClaimsService.getJwtUserClaimsByUsername("user");

        // Then
        assertThat(response.accessToken()).isNotNull();
        assertThat(jwtService.extractUsername(response.accessToken())).isEqualTo("user");
        assertThat(jwtService.isTokenValid(response.accessToken(), claims)).isTrue();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    void givenInvalidRefreshToken_whenRefresh_thenExceptionThrown() {
        // Given
        String refreshToken = ":)";

        // When
        Exception exception = catchException(() -> refreshTokenService.getToken(refreshToken));

        // Then
        assertThat(exception).isInstanceOf(ThrowableProblem.class);
        assertThat(((ThrowableProblem) exception).getStatus()).isEqualTo(Status.UNAUTHORIZED);
    }

    @Test
    public void givenUser_whenGrantingRole_thenRoleIsGranted() {
        // Given
        User user = dummyUser;
        AuthInfo authInfo = authService.register("test_user", "pass", user);

        Role newUserRole = assertRoleExist("TEST");

        // When
        authService.grantRole(authInfo, newUserRole);

        // Then
        assertThat(authInfo.getRoles().stream()
            .anyMatch(role -> role.getName().equals("TEST"))).isTrue();
    }

    @Autowired
    RoleRepository roleRepository;

    private Role assertRoleExist(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> roleService.createRoleWithName(roleName));
    }
}
