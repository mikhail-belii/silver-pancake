package silverpancake.application.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginModel {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    private String password;
}
