package silverpancake.application.service;

public interface LoggedOutTokenService {
    void addLoggedOutToken(String token);
    Boolean isTokenLoggedOut(String token);
}
