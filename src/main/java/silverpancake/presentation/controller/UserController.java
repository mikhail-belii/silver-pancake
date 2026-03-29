package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.user.UserModel;
import silverpancake.application.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get my profile")
    public Response<UserModel> getMyProfile(@RequestAttribute("authModel") AuthorizationModel authModel) {
        return Response.success(userService.getUserProfile(authModel.getUserId()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile")
    public Response<UserModel> getUserProfile(@PathVariable String userId) {
        return Response.success(userService.getUserProfile(UUID.fromString(userId)));
    }
}
