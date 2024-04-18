package pl.edu.pw.mini.ingreedio.api.mapper;


import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.security.JwtTokenUserClaims;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.model.Permission;
import pl.edu.pw.mini.ingreedio.api.model.Role;

@Component
public class AuthInfoMapper {
    public JwtTokenUserClaims toTokenClaims(AuthInfo auth) {
        Set<String> roles = auth.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = auth.getRoles().stream()
            .map(Role::getPermissions)
            .flatMap(Set::stream)
            .map(Permission::getName)
            .collect(Collectors.toSet());

        return new JwtTokenUserClaims(
            auth.getUsername(),
            roles,
            permissions
        );
    }
}
