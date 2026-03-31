package silverpancake.application.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class TaskShortModel {
    private UUID id;
    private String title;
    private String text;
}
