package pl.edu.pw.mini.ingreedio.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.auth.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtAuthTokensDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.model.RefreshToken;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtAuthTokens;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.auth.service.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    private final ModelMapper mapper;

    @Operation(summary = "Authenticate user and get token",
        description = "Authenticates a user with the provided credentials and returns a JWT token "
                      + "if successful.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = JwtAuthTokensDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthTokensDto> authenticateAndGetToken(
        @RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(mapper.map(
            authService.login(request.username(), request.password()),
            JwtAuthTokensDto.JwtAuthTokensDtoBuilder.class
        ).build());
    }

    @Operation(summary = "Refresh JWT token",
        description = "Refreshes the JWT token using a valid refresh token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully refreshed the token",
            content = @Content(schema = @Schema(implementation = JwtAuthTokensDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthTokensDto> refreshToken(
        @RequestBody RefreshTokenRequestDto request) {
        RefreshToken token = refreshTokenService.getToken(request.refreshToken());
        JwtAuthTokens newTokens = authService.refresh(token);
        return ResponseEntity.ok(mapper.map(
            newTokens,
            JwtAuthTokensDto.JwtAuthTokensDtoBuilder.class
        ).build());
    }
}
