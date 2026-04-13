package silverpancake.application.serviceimpl.taskanswer.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.repository.TaskAnswerRepository;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;

@Service
@RequiredArgsConstructor
public class FirstSolutionStrategy implements TaskAttachmentStrategy {

    private final TaskAnswerRepository taskAnswerRepository;
    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;

    @Override
    public FinalTaskAnswerModel process() {
        return null;
    }

    @Override
    public TaskAnswerFinalizationType getType() {
        return TaskAnswerFinalizationType.FIRST_ATTACHMENT;
    }
}
