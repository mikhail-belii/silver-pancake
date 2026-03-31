package silverpancake.application.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
@Data
public class TeamProperties {
    private List<String> teamNames;
}
