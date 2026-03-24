package silverpancake.application.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequestModel {
    @NotNull
    @NotBlank
    public String refreshToken;
}
