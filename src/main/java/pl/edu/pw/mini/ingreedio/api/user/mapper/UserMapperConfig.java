package pl.edu.pw.mini.ingreedio.api.user.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.user.dto.UserDto;
import pl.edu.pw.mini.ingreedio.api.user.dto.UserDto.UserDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@Component
public class UserMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new BuilderConverter<>(UserDtoBuilder::build, UserDtoBuilder.class),
            User.class, UserDto.class);
    }
}
