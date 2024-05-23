package pl.edu.pw.mini.ingreedio.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import pl.edu.pw.mini.ingreedio.api.auth.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Authenticate user and get token",
        description = "Authenticates a user with the provided credentials and returns a JWT token "
            + "if successful."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = JwtResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAndGetToken(
        @RequestBody AuthRequestDto request) {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (Exception e) {
            throw Problem.valueOf(Status.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Refresh JWT token",
        description = "Refreshes the JWT token using a valid refresh token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully refreshed the token",
            content = @Content(schema = @Schema(implementation = JwtResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDto> refreshToken(
        @RequestBody RefreshTokenRequestDto request) {
        try {
            return ResponseEntity.ok(service.refresh(request));
        } catch (RuntimeException ex) {
            throw Problem.valueOf(Status.UNAUTHORIZED);
        }
    }
}
