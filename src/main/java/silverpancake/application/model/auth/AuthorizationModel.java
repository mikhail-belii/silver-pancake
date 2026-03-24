package silverpancake.application.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizationModel {
    private String userId;
    private String accessToken;
}
