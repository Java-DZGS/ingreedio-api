package pl.edu.pw.mini.ingreedio.api.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@Tag(name = "Providers")
public class ProviderController {
    // TODO: implement getProvidersByIds
}
