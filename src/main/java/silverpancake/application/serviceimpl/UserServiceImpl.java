package silverpancake.application.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.UserMapper;
import silverpancake.application.model.user.UserModel;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.service.UserService;
import silverpancake.application.util.ExceptionUtility;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ExceptionUtility exceptionUtility;

    @Override
    public UserModel getUserProfile(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(exceptionUtility::userNotFoundException);

        return userMapper.toModel(user);
    }
}
