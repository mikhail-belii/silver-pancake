package silverpancake.application.serviceimpl.taskanswer.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TaskAnswerMapper;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

@Service
@RequiredArgsConstructor
public class FirstSolutionStrategy implements TaskAttachmentStrategy {

    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;
    private final ExceptionUtility exceptionUtility;

    @Override
    public FinalTaskAnswerModel process(Team team, Task task, TaskAnswer taskAnswer) {
        var teamFinalTaskAnswer = teamFinalTaskAnswerRepository.findByTaskIdAndTeamId(task.getId(), team.getId())
                .orElseThrow(exceptionUtility::teamFinalTaskAnswerNotFoundException);

        if (teamFinalTaskAnswer.getFinalTaskAnswer() == null) {
            teamFinalTaskAnswer.setFinalTaskAnswer(taskAnswer);
            teamFinalTaskAnswer = teamFinalTaskAnswerRepository.save(teamFinalTaskAnswer);
        }

        return TaskAnswerMapper.toModel(teamFinalTaskAnswer);
    }

    @Override
    public TaskAnswerFinalizationType getType() {
        return TaskAnswerFinalizationType.FIRST_ATTACHMENT;
    }
}
