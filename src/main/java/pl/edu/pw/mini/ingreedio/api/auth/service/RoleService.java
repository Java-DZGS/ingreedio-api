package pl.edu.pw.mini.ingreedio.api.auth.service;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.exception.RoleNotFoundException;
import pl.edu.pw.mini.ingreedio.api.auth.model.Permission;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Value("${security.default-user-roles}")
    @Getter
    private Set<String> defaultUserRolesNames;

    @Getter
    private Set<Role> defaultUserRoles;

    @PostConstruct
    @Transactional(readOnly = true)
    protected void initDefaultUserRoles() {
        defaultUserRoles = getRolesByRolesNames(defaultUserRolesNames);
    }

    @Transactional(readOnly = true)
    public Set<Role> getRolesByRolesNames(Set<String> rolesNames) throws RoleNotFoundException {
        Set<Role> roles = roleRepository.findAllByNameIn(rolesNames);

        if (roles.size() < rolesNames.size()) {
            rolesNames.removeAll(roles.stream().map(Role::getName).collect(Collectors.toSet()));

            //noinspection OptionalGetWithoutIsPresent
            throw new RoleNotFoundException(rolesNames.stream().findAny().get());
        }

        return roles;
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName).orElseThrow(
            () -> new RoleNotFoundException(roleName));
    }

    @Transactional
    public Role createRoleWithName(String roleName) {
        Role role = Role.builder().name(roleName).build();
        roleRepository.save(role);

        return role;
    }

    @Transactional
    public void addPermissionToRole(Role role, Permission permission) {
        role.getPermissions().add(permission);
        roleRepository.save(role);
    }
}
