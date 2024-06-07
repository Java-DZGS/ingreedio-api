package pl.edu.pw.mini.ingreedio.api.provider.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.edu.pw.mini.ingreedio.api.common.mapping.BuilderConverter;
import pl.edu.pw.mini.ingreedio.api.common.mapping.MapperConfig;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto.ProviderDtoBuilder;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;

@Component
public class ProviderMapperConfig implements MapperConfig {
    @Override
    public void setupMapper(ModelMapper mapper) {
        mapper.addConverter(new BuilderConverter<>(ProviderDtoBuilder::build,
            ProviderDtoBuilder.class), Provider.class, ProviderDto.class);
    }
}