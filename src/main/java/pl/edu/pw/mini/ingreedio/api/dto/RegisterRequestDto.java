package pl.edu.pw.mini.ingreedio.api.dto;

public record RegisterRequestDto(String userName, String displayName, String email,
                                 String password) {
}
