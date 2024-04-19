package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.management.relation.RoleNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Role;
import pl.edu.pw.mini.ingreedio.api.repository.RoleRepository;

public class RoleServiceTest extends IntegrationTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;


    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void givenUser_whenGrantingRole_thenRoleIsGranted() throws RoleNotFoundException {
        // Given
        RegisterRequestDto request =
            new RegisterRequestDto("us", "Us", "us@as.pl", "pass");
        authService.register(request);
        AuthInfo authInfo = authService.getAuthInfoByUsername("us");
        Role newUserRole = assertRoleExist("MODERATOR");

        // When
        roleService.grantRole(authInfo, newUserRole);
        boolean valid = authService.getAuthInfoByUsername("us").getRoles().stream()
            .anyMatch(role -> role.getName().equals("MODERATOR"));

        // Then
        assertTrue(valid);
    }

    private Role assertRoleExist(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> roleService.createRoleWithName(roleName));
    }
}
