package silverpancake.application.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import silverpancake.application.exception.ExceptionWrapper;
import silverpancake.domain.entity.user.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("#{T(io.jsonwebtoken.security.Keys).hmacShaKeyFor(T(io.jsonwebtoken.io.Decoders).BASE64.decode('${jwt.secret.access}'))}")

    private SecretKey jwtAccessSecret;
    @Value("#{T(io.jsonwebtoken.security.Keys).hmacShaKeyFor(T(io.jsonwebtoken.io.Decoders).BASE64.decode('${jwt.secret.refresh}'))}")
    private SecretKey jwtRefreshSecret;

    @Value("${jwt.lifetime.access-minutes}")
    private long accessLifetimeMinutes;

    @Value("${jwt.lifetime.refresh-days}")
    private long refreshLifetimeDays;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(getAccessTokenExpirationDate()))
                .signWith(jwtAccessSecret)
                .claim("user_id", user.getId().toString())
                .claim("token_id", UUID.randomUUID().toString())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(getRefreshTokenExpirationDate()))
                .signWith(jwtRefreshSecret)
                .claim("user_id", user.getId().toString())
                .compact();
    }

    public Claims parseAccessClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtAccessSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRefreshClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtRefreshSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Instant getAccessTokenExpirationDate() {
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        return now
                .plusMinutes(accessLifetimeMinutes)
                .atZone(ZoneId.of("UTC"))
                .toInstant();
    }

    public Instant getRefreshTokenExpirationDate() {
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        return now
                .plusDays(refreshLifetimeDays)
                .atZone(ZoneId.of("UTC"))
                .toInstant();
    }

    public UUID getUserIdFromRefreshToken(String token) {
        var claims = parseRefreshClaims(token);

        var userId = claims.get("user_id",  String.class);
        if  (userId != null) {
            return UUID.fromString(userId);
        }
        var authEx = new ExceptionWrapper(new AuthException());
        authEx.setErrorMessage("Invalid token");
        throw authEx;
    }
}
