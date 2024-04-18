package pl.edu.pw.mini.ingreedio.api.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private Set<String> defaultUserRolesNames;

    public Set<Role> getDefaultUserRoles() {
        return defaultUserRolesNames.stream()
            .map(roleRepository::findByName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
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
