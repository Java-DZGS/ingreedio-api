package pl.edu.pw.mini.ingreedio.api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.access-token-lifetime}")
    private long accessTokenLifetime;

    @Value("${security.secret}")
    public String secret;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Set<String> extractRoles(String token) {
        Collection<?> roles =
            extractClaim(token, claims -> claims.get("roles", Collection.class));

        return roles.stream()
            .map(Object::toString)
            .collect(Collectors.toSet());
    }

    public Set<String> extractPermissions(String token) {
        Collection<?> permissions =
            extractClaim(token, claims -> claims.get("permissions", Collection.class));

        return permissions.stream()
            .map(Object::toString)
            .collect(Collectors.toSet());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, JwtUserClaims expectedJwtUserClaims) {
        final String username = extractUsername(token);
        final Set<String> roles = extractRoles(token);
        final Set<String> permissions = extractPermissions(token);

        return (username.equals(expectedJwtUserClaims.username())
                && roles.equals(expectedJwtUserClaims.roles())
                && permissions.equals(expectedJwtUserClaims.permissions())
                && !isTokenExpired(token));
    }

    public String generateToken(JwtUserClaims jwtUserClaimsDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", jwtUserClaimsDto.roles());
        claims.put("permissions", jwtUserClaimsDto.permissions());
        return createToken(claims, jwtUserClaimsDto.username());
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + accessTokenLifetime))
            .signWith(getSignKey(), Jwts.SIG.HS256)
            .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
