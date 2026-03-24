package silverpancake.application.serviceimpl;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.UserMapper;
import silverpancake.application.model.auth.TokenResponseModel;
import silverpancake.application.model.user.UserLoginModel;
import silverpancake.application.model.user.UserRegisterModel;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.service.AuthService;
import silverpancake.application.service.LoggedOutTokenService;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.application.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LoggedOutTokenService loggedOutTokenService;
    private final UserMapper userMapper;
    private final ExceptionUtility exceptionUtility;

    @Override
    public TokenResponseModel register(UserRegisterModel userRegisterModel) {
        if (userRepository.existsByEmail(userRegisterModel.getEmail())) {
            throw exceptionUtility.userWithEmailExistsException();
        }

        var user = userMapper.toEntity(userRegisterModel);
        user.setId(UUID.randomUUID())
                .setPasswordHash(passwordEncoder.encode(userRegisterModel.getPassword()))
                .setCreatedAt(LocalDateTime.now());
        var accessToken = jwtUtil.generateAccessToken(user);
        var refreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiryDate(jwtUtil.getRefreshTokenExpirationDate());

        userRepository.save(user);

        return new TokenResponseModel(accessToken, refreshToken);
    }

    @Override
    public TokenResponseModel login(UserLoginModel userLoginModel) {
        var user = userRepository.findByEmail(userLoginModel.getEmail())
                .orElseThrow(exceptionUtility::incorrectEmailOrPasswordException);

        if (!passwordEncoder.matches(userLoginModel.getPassword(), user.getPasswordHash())) {
            throw exceptionUtility.incorrectEmailOrPasswordException();
        }

        var accessToken = jwtUtil.generateAccessToken(user);
        var refreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiryDate(jwtUtil.getRefreshTokenExpirationDate());

        userRepository.save(user);

        return new TokenResponseModel(accessToken, refreshToken);
    }

    @Override
    public TokenResponseModel refreshTokens(String refreshToken) {
        try {
            jwtUtil.parseRefreshClaims(refreshToken);
        } catch (JwtException e) {
            throw exceptionUtility.invalidRefreshTokenException();
        }

        var userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
        var user = userRepository.findById(userId)
                .orElseThrow(exceptionUtility::userNotFoundException);


        var savedRefreshToken = user.getRefreshToken();
        var savedExpiryDate = user.getRefreshTokenExpiryDate();

        if (savedRefreshToken == null || savedExpiryDate == null || !savedRefreshToken.equals(refreshToken) || Date.from(savedExpiryDate).before(new Date())) {
            throw exceptionUtility.invalidRefreshTokenException();
        }

        var newAccessToken = jwtUtil.generateAccessToken(user);
        var newRefreshToken = jwtUtil.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiryDate(jwtUtil.getRefreshTokenExpirationDate());

        userRepository.save(user);

        return new TokenResponseModel(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(UUID userId, String accessToken) {
        var user = userRepository.findById(userId)
                .orElseThrow(exceptionUtility::userNotFoundException);

        loggedOutTokenService.addLoggedOutToken(accessToken);

        user.setRefreshToken(null);
        user.setRefreshTokenExpiryDate(null);

        userRepository.save(user);
    }
}
