package pl.edu.pw.mini.ingreedio.api.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.provider.model.Provider;
import pl.edu.pw.mini.ingreedio.api.provider.service.ProviderService;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@Tag(name = "Providers")
public class ProviderController {
    private final ProviderService providerService;

    @Operation(summary = "Get provider by id", description = "Get provider by id")
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProvidersByIds(@PathVariable long id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @Operation(summary = "Get providers by ids", description = "Get providers by ids")
    @GetMapping("/get-by")
    public ResponseEntity<Set<Provider>> getProvidersByIds(
        @RequestParam("ids") Set<Long> providerIds) {
        return ResponseEntity.ok(providerService.getProvidersByIds(providerIds));
    }
}
