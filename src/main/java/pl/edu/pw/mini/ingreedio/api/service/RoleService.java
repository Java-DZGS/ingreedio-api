package pl.edu.pw.mini.ingreedio.api.service;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import javax.management.relation.RoleNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Role;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.repository.RoleRepository;

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
        userAuthInfo.getRoles().add(role);

        authRepository.save(userAuthInfo);
    }
    
    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName).orElseThrow(
            () -> new RoleNotFoundException("User '" + roleName + "' not found!"));
    }
}
