package silverpancake.application.model.finaltaskanswer;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.model.taskanswer.TaskAnswerModel;
import silverpancake.application.model.team.TeamModel;
import silverpancake.domain.entity.taskanswer.TaskAnswerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class FinalTaskAnswerModel {

    private UUID id;

    private Integer score = 0;

    private LocalDateTime submittedAt;

    private TaskAnswerStatus status = TaskAnswerStatus.NOT_COMPLETED;

    private TeamModel team;

    private TaskModel task;

    private TaskAnswerModel taskAnswer;

    private LocalDateTime updatedAt;
}
