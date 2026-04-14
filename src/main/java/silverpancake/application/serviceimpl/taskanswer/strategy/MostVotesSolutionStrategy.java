package silverpancake.application.serviceimpl.taskanswer.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TaskAnswerMapper;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;
import silverpancake.application.serviceimpl.VoteServiceImpl;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

// Голосование - 67%
@Service
@RequiredArgsConstructor
public class MostVotesSolutionStrategy implements TaskAttachmentStrategy {

    private final VoteServiceImpl voteService;
    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;
    private final ExceptionUtility exceptionUtility;

    @Override
    public FinalTaskAnswerModel process(Team team, Task task, TaskAnswer taskAnswer) {
        voteService.voteForAnswer(team, task, taskAnswer);

        var teamFinalTaskAnswer = teamFinalTaskAnswerRepository.findByTaskIdAndTeamId(task.getId(), team.getId())
                .orElseThrow(exceptionUtility::teamFinalTaskAnswerNotFoundException);

        var mostVotedTaskAnswer = voteService.findTaskAnswerWithVotesPercentageMoreThan(team, task, taskAnswer, 67);

        teamFinalTaskAnswer.setFinalTaskAnswer(mostVotedTaskAnswer);
        teamFinalTaskAnswerRepository.save(teamFinalTaskAnswer);

        return TaskAnswerMapper.toModel(teamFinalTaskAnswer);
    }

    @Override
    public TaskAnswerFinalizationType getType() {
        return TaskAnswerFinalizationType.QUALIFIED_MAJORITY;
    }
}
