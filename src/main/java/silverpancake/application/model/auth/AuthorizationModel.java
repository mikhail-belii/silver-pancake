package silverpancake.application.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthorizationModel {
    private UUID userId;
    private String accessToken;
}
