package silverpancake.application.serviceimpl.taskanswer.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

// Голосование - 67%
@Service
@RequiredArgsConstructor
public class MostVotesSolutionStrategy implements TaskAttachmentStrategy {

    @Override
    public FinalTaskAnswerModel process(Team team, Task task, TaskAnswer taskAnswer) {
        return null;
    }

    @Override
    public TaskAnswerFinalizationType getType() {
        return TaskAnswerFinalizationType.QUALIFIED_MAJORITY;
    }
}
