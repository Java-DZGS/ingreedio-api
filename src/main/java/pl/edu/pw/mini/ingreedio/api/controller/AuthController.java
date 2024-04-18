package pl.edu.pw.mini.ingreedio.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.mini.ingreedio.api.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAndGetToken(
        @RequestBody AuthRequestDto request) {
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(
        @RequestBody RefreshTokenRequestDto request) {
        try {
            return ResponseEntity.ok(service.refresh(request));
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
