package pl.edu.pw.mini.ingreedio.api.auth.service;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import javax.management.relation.RoleNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Permission;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.auth.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final AuthRepository authRepository;

    @Value("${security.default-user-roles}")
    @Getter
    private Set<String> defaultUserRolesNames;

    @Getter
    private Set<Role> defaultUserRoles;

    @PostConstruct
    private void initDefaultUserRoles() throws RoleNotFoundException {
        defaultUserRoles = getRolesByRolesNames(defaultUserRolesNames);
    }

    public Set<Role> getRolesByRolesNames(Set<String> rolesNames) throws RoleNotFoundException {
        HashSet<Role> result = new HashSet<>();

        for (var roleName : rolesNames) {
            var role = roleRepository.findByName(roleName);
            if (role.isEmpty()) {
                throw new RoleNotFoundException("Role '" + roleName + "' not found!");
            }
            result.add(role.get());
        }

        return result;
    }

    public void grantRole(AuthInfo userAuthInfo, Role role) {
        Set<Role> roles = userAuthInfo.getRoles();
        roles.add(role);
        userAuthInfo.setRoles(roles);

        authRepository.save(userAuthInfo);
    }
    
    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName).orElseThrow(
            () -> new RoleNotFoundException("User '" + roleName + "' not found!"));
    }

    public Role createRoleWithName(String roleName) {
        Role role = Role.builder().name(roleName).build();
        roleRepository.save(role);

        return role;
    }

    public void addPermissionToRole(Role role, Permission permission) {
        role.getPermissions().add(permission);
        roleRepository.save(role);
    }
}
