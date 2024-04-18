package pl.edu.pw.mini.ingreedio.api.service;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.mapper.AuthInfoMapper;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Role;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.repository.RoleRepository;
import pl.edu.pw.mini.ingreedio.api.security.JwtUserClaims;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final AuthRepository authRepository;
    private final AuthInfoMapper authInfoMapper;

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
            if (!role.isPresent()) {
                throw new RoleNotFoundException("Role '" + roleName + "' not found!");
            }
            result.add(role.get());
        }

        return result;
    }

    public JwtUserClaims getJwtTokenUserClaimsByUsername(String username)
        throws UsernameNotFoundException {
        AuthInfo authInfo = authRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User '" + username + "' not found!"));

        return authInfoMapper.toTokenClaims(authInfo);
    }

    public JwtUserClaims getJwtTokenUserClaimsByAuthInfo(AuthInfo authInfo) {
        return authInfoMapper.toTokenClaims(authInfo);
    }

    @Override
    public AuthInfo loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findByUsername(username)
             .orElseThrow(
                 () -> new UsernameNotFoundException("Could not find user '" + username + "'.")
             );
    }
}
