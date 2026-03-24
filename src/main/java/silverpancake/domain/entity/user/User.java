package silverpancake.domain.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import silverpancake.domain.entity.usercourse.UserCourse;
import silverpancake.domain.entity.userteam.UserTeam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
@Data
@Accessors(chain = true)
public class User {
    @Id
    private UUID id;

    @Length(min = 2, max = 128)
    private String firstName;

    @Length(min = 2, max = 128)
    private String lastName;

    private String email;

    @OneToMany(mappedBy = "user")
    private List<UserCourse> userCourses;

    @OneToMany(mappedBy = "user")
    private List<UserTeam> userTeams;

    @Column(length = 400)
    private String refreshToken;

    private Instant refreshTokenExpiryDate;

    private String passwordHash;

    private LocalDateTime createdAt;
}
