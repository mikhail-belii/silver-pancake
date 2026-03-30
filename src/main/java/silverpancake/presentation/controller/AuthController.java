package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.auth.RefreshTokenRequestModel;
import silverpancake.application.model.auth.TokenResponseModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.user.UserLoginModel;
import silverpancake.application.model.user.UserRegisterModel;
import silverpancake.application.service.AuthService;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register")
    public Response<TokenResponseModel> register(@Valid @RequestBody UserRegisterModel userRegisterModel) {
        return Response.success(authService.register(userRegisterModel));
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public Response<TokenResponseModel> login(@Valid @RequestBody UserLoginModel userLoginModel) {
        return Response.success(authService.login(userLoginModel));
    }

    @PostMapping("/refresh-tokens")
    @Operation(summary = "Refresh tokens")
    public Response<TokenResponseModel> refreshTokens(@Valid @RequestBody RefreshTokenRequestModel model) {
        return Response.success(authService.refreshTokens(model.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    public Response<Void> logout(@RequestAttribute("authModel") AuthorizationModel authModel) {
        authService.logout(authModel.getUserId(), authModel.getAccessToken());
        return Response.success();
    }
}
