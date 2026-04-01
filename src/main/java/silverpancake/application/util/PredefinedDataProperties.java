package silverpancake.application.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.predefined-data")
@Data
public class PredefinedDataProperties {
    private boolean enabled;
    private PredefinedCourseProperties course;
    private List<PredefinedUserProperties> users;

    @Data
    public static class PredefinedCourseProperties {
        private String name;
        private String description;
        private String joinCode;
    }

    @Data
    public static class PredefinedUserProperties {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }
}
