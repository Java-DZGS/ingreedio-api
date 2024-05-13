package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.auth.service.RoleService;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.repository.RoleRepository;

@SpringBootTest
public class RoleServiceTest extends IntegrationTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;


    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void givenUser_whenGrantingRole_thenRoleIsGranted() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto(
            "test_user", "TestUser", "test_user@as.pl", "pass");
        authService.register(request);
        AuthInfo authInfo = authService.getAuthInfoByUsername("test_user");
        Role newUserRole = assertRoleExist("TEST");

        // When
        roleService.grantRole(authInfo, newUserRole);
        boolean valid = authService.getAuthInfoByUsername("test_user").getRoles().stream()
            .anyMatch(role -> role.getName().equals("TEST"));

        // Then
        assertTrue(valid);
    }

    private Role assertRoleExist(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> roleService.createRoleWithName(roleName));
    }
}
