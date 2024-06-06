package pl.edu.pw.mini.ingreedio.api.provider.mapper;

import java.util.function.Function;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;

public class ProviderDtoMapper implements Function<Provider, ProviderDto> {
    @Override
    public ProviderDto apply(Provider providerDto) {
        return ProviderDto.builder()
            .id(providerDto.getId())
            .name(providerDto.getName())
            .build();
    }
}
