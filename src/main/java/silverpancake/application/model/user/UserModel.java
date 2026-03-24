package silverpancake.application.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserModel {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}