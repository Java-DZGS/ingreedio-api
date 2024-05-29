package pl.edu.pw.mini.ingreedio.api.auth.mapper;


import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Permission;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims;

@Component
public class AuthInfoMapper {
    public JwtUserClaims toTokenClaims(AuthInfo auth) {
        Set<String> roles = auth.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = auth.getRoles().stream()
            .map(Role::getPermissions)
            .flatMap(Set::stream)
            .map(Permission::getName)
            .collect(Collectors.toSet());

        return new JwtUserClaims(
            auth.getUsername(),
            roles,
            permissions
        );
    }
}
