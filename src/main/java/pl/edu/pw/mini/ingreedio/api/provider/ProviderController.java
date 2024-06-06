package pl.edu.pw.mini.ingreedio.api.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.provider.dto.ProviderDto;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;
import pl.edu.pw.mini.ingreedio.api.provider.service.ProviderService;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@Tag(name = "Providers")
public class ProviderController {
    private final ProviderService providerService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get provider by ID", description = "Get provider by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> getProviderById(@PathVariable long id) {
        return ResponseEntity.ok(modelMapper.map(providerService.getProviderById(id),
            ProviderDto.class));
    }

    @Operation(summary = "Get providers by IDs", description = "Get providers by IDs")
    @GetMapping("/get-by")
    public ResponseEntity<Set<ProviderDto>> getProvidersByIds(@RequestParam("ids")
                                                                  Set<Long> providerIds) {
        Set<Provider> providers = providerService.getProvidersByIds(providerIds);
        Set<ProviderDto> providerDtos = providers.stream()
                .map(provider -> modelMapper.map(provider,
                    ProviderDto.ProviderDtoBuilder.class).build())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(providerDtos);
    }

    @Operation(summary = "Get all providers", description = "Get all existing providers")
    @GetMapping
    public ResponseEntity<List<ProviderDto>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        List<ProviderDto> providerDtos = providers.stream()
                .map(provider -> modelMapper.map(provider,
                    ProviderDto.ProviderDtoBuilder.class).build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(providerDtos);
    }
}
