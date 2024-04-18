package pl.edu.pw.mini.ingreedio.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.security.JwtTokenUserClaims;

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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(JwtTokenUserClaims jwtTokenUserClaimsDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", jwtTokenUserClaimsDto.roles());
        claims.put("permissions", jwtTokenUserClaimsDto.permissions());
        return createToken(claims, jwtTokenUserClaimsDto.username());
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
