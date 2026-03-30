package silverpancake.application.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.model.user.UserModel;
import silverpancake.domain.entity.task.TeamFormationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class TaskModel {
    private UUID id;
    private String title;
    private String text;
    private LocalDateTime updatedAt;
    private UserModel author;
    private List<FileModel> files;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private LocalDateTime draftStartTime;
    private Integer maxScore;
    private TeamFormationType teamFormationType;
}
