package silverpancake.application.serviceimpl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.service.LoggedOutTokenService;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.application.util.JwtUtil;

import java.time.Duration;

@Service
@AllArgsConstructor
public class LoggedOutTokenServiceImpl implements LoggedOutTokenService {
    private final JwtUtil jwtUtil;
    private final ExceptionUtility exceptionUtility;
    private final Cache<String, String> loggedOutTokens = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofDays(30))
            .maximumSize(100_000)
            .build();


    @Override
    public void addLoggedOutToken(String token) {
        var tokenId = getTokenId(token);
        loggedOutTokens.put(tokenId, token);
    }

    @Override
    public Boolean isTokenLoggedOut(String token) {
        var tokenId = getTokenId(token);
        return loggedOutTokens.getIfPresent(tokenId) != null;
    }

    private String getTokenId(String token) {
        var claims = jwtUtil.parseAccessClaims(token);

        var tokenId = claims.get("token_id",  String.class);
        if  (tokenId != null) {
            return tokenId;
        }

        throw exceptionUtility.invalidAccessTokenException();
    }
}
