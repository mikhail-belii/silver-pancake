package silverpancake.application.service;

import silverpancake.application.model.auth.TokenResponseModel;
import silverpancake.application.model.user.UserLoginModel;
import silverpancake.application.model.user.UserRegisterModel;

import java.util.UUID;

public interface AuthService {
    TokenResponseModel register(UserRegisterModel userRegisterModel);
    TokenResponseModel login(UserLoginModel userLoginModel);
    TokenResponseModel refreshTokens(String refreshToken);
    void logout(UUID userId, String accessToken);
}
