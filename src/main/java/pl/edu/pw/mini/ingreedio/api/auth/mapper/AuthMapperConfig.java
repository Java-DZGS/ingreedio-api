package pl.edu.pw.mini.ingreedio.api.auth.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Permission;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims.JwtUserClaimsBuilder;
import pl.edu.pw.mini.ingreedio.api.common.MapperConfig;

@Component
public class AuthMapperConfig implements MapperConfig {

    @Override
    public void setupMapper(ModelMapper mapper) {
        // Why not just create a class for those converters and do
        // mapper.addConverter(new NullCheckConverter<>(Role::getName)) or sth?
        // Because https://github.com/modelmapper/modelmapper/issues/622
        mapper.addConverter(new AbstractConverter<Role, String>() {
            @Override
            protected String convert(Role source) {
                return source == null ? null : source.getName();
            }
        });
        mapper.addConverter(new AbstractConverter<Permission, String>() {
            @Override
            protected String convert(Permission source) {
                return source == null ? null : source.getName();
            }
        });
        mapper.addConverter(new AbstractConverter<RefreshToken, String>() {
            @Override
            protected String convert(RefreshToken source) {
                return source == null ? null : source.getToken();
            }
        });

        Converter<Set<Role>, Set<String>> converter = new AbstractConverter<>() {
            @Override
            protected Set<String> convert(Set<Role> source) {
                return source == null ? null : source.stream()
                    .map(Role::getPermissions)
                    .flatMap(Set::stream)
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            }
        };

        mapper.typeMap(AuthInfo.class, JwtUserClaimsBuilder.class)
            .addMappings(exprMapper -> exprMapper.using(converter)
                .map(AuthInfo::getRoles, JwtUserClaimsBuilder::permissions));
    }
}
