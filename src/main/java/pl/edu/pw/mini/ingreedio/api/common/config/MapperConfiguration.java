package pl.edu.pw.mini.ingreedio.api.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pw.mini.ingreedio.api.common.MapperConfig;

@Configuration
@RequiredArgsConstructor
public class MapperConfiguration {
    private final List<MapperConfig> mapperConfigs;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.registerModule(new RecordModule());
        mapper.getConfiguration()
            .setDestinationNamingConvention(NamingConventions.builder())
            .setDestinationNameTransformer(NameTransformers.builder());

        mapperConfigs.forEach(config -> config.setupMapper(mapper));

        return mapper;
    }
}
