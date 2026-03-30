package silverpancake.application.mapper;

import lombok.experimental.UtilityClass;
import silverpancake.application.model.user.UserModel;
import silverpancake.domain.entity.user.User;

@UtilityClass
public class SimpleUserMapper {
    public UserModel toModel(User userEntity) {
        return new UserModel()
                .setId(userEntity.getId())
                .setFirstName(userEntity.getFirstName())
                .setLastName(userEntity.getLastName())
                .setEmail(userEntity.getEmail());
    }
}
