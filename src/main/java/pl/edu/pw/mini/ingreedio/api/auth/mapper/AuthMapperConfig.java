package pl.edu.pw.mini.ingreedio.api.auth.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtAuthTokensDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtAuthTokensDto.JwtAuthTokensDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.model.Permission;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtAuthTokens;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims.JwtUserClaimsBuilder;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.common.mapping.NullableConverter;

@Component
public class AuthMapperConfig implements MapperConfig {

    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new NullableConverter<>(Role::getName), Role.class, String.class);
        mapper.addConverter(new NullableConverter<>(Permission::getName), Permission.class,
            String.class);
        mapper.addConverter(new NullableConverter<>(RefreshToken::getToken), RefreshToken.class,
            String.class);

        mapper.addConverter(new BuilderConverter<>(JwtAuthTokensDtoBuilder::build,
            JwtAuthTokensDtoBuilder.class), JwtAuthTokens.class, JwtAuthTokensDto.class);

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

        mapper.addConverter(new BuilderConverter<>(JwtUserClaimsBuilder::build,
            JwtUserClaimsBuilder.class), AuthInfo.class, JwtUserClaims.class);
    }
}
