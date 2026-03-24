package silverpancake.application.model.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterModel {
    @Email(message = "Email должен иметь формат адреса электронной почты")
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    private String password;
    @NotBlank
    @Size(min = 2, max = 32, message = "Имя должно содержать от 2 до 32 символов")
    @Pattern(regexp = "^[\\u0410-\\u042F\\u0430-\\u044F\\u0401\\u0451]+$", message = "Имя должно содержать только символы кириллицы")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 32, message = "Фамилия должна содержать от 2 до 32 символов")
    @Pattern(regexp = "^[\\u0410-\\u042F\\u0430-\\u044F\\u0401\\u0451]+$", message = "Фамилия должна содержать только символы кириллицы")
    private String lastName;
}
