package pl.edu.pw.mini.ingreedio.api.dto;

public record RegisterRequestDto(String username, String displayName, String email,
                                 String password) {
}
