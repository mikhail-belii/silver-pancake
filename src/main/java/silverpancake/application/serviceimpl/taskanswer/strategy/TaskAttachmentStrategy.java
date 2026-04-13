package silverpancake.application.serviceimpl.taskanswer.strategy;

import org.springframework.stereotype.Service;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

@Service
public interface TaskAttachmentStrategy {

    FinalTaskAnswerModel process(Team team, Task task, TaskAnswer taskAnswer);
    TaskAnswerFinalizationType getType();
}
