package silverpancake.application.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.repository.CourseRepository;

import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CourseCodeGenerator {
    private final CourseRepository courseRepository;
    private final ExceptionUtility exceptionUtility;
    private final Random random = new SecureRandom();
    private static final String CODE_SYMBOLS = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ1234567890";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_ATTEMPTS = 20;

    public String generateNewCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = generateCode();
            if (!courseRepository.existsByJoinCode(code)) {
                return code;
            }
        }
        throw exceptionUtility.generateCodeFailedException();
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_SYMBOLS.charAt(random.nextInt(CODE_SYMBOLS.length())));
        }
        return code.toString();
    }
}
