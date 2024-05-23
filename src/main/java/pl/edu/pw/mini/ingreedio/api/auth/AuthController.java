package pl.edu.pw.mini.ingreedio.api.auth;

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
@Tag(name = "Authorization")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAndGetToken(
        @RequestBody AuthRequestDto request) {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (Exception e) {
            throw Problem.valueOf(Status.UNAUTHORIZED);
        }
    }

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
