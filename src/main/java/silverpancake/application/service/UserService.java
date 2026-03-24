package silverpancake.application.service;

import silverpancake.application.model.user.UserModel;

import java.util.UUID;

public interface UserService {
    UserModel getUserProfile(UUID userId);
}
