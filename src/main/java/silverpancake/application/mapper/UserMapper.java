package silverpancake.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import silverpancake.application.model.user.UserModel;
import silverpancake.application.model.user.UserRegisterModel;
import silverpancake.domain.entity.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "refreshTokenExpiryDate", ignore = true)
    User toEntity(UserRegisterModel userRegisterModel);
    UserModel toModel(User user);
}