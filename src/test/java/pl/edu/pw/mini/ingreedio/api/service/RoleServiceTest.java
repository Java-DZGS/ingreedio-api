package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.management.relation.RoleNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Role;

public class RoleServiceTest extends IntegrationTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Test
    public void givenUser_whenGrantingRole_thenRoleIsGranted() throws RoleNotFoundException {
        // Given
        RegisterRequestDto request =
            new RegisterRequestDto("us", "Us", "us@as.pl", "pass");
        authService.register(request);
        AuthInfo authInfo = authService.getAuthInfoByUsername("us");
        Role newRole = roleService.getRoleByName("MODERATOR");

        // When
        roleService.grantRole(authInfo, newRole);
        boolean valid = authService.getAuthInfoByUsername("us").getRoles().stream()
            .anyMatch(role -> role.getName().equals("MODERATOR"));

        // Then
        assertTrue(valid);
    }
}
