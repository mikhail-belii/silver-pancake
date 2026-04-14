package silverpancake.application.serviceimpl.taskanswer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TaskAnswerMapper;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.repository.FileRepository;
import silverpancake.application.repository.TaskAnswerRepository;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;
import silverpancake.application.serviceimpl.VoteServiceImpl;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskAnswerDeclineService {

    private final TaskAnswerRepository taskAnswerRepository;
    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;
    private final FileRepository fileRepository;
    private final VoteServiceImpl voteService;
    private final ExceptionUtility exceptionUtility;

    @Transactional
    public FinalTaskAnswerModel declineAnswer(Team team, Task task, TaskAnswer taskAnswer) {
        var teamFinalTaskAnswer = teamFinalTaskAnswerRepository.findByTaskIdAndTeamId(task.getId(), team.getId())
                .orElseThrow(exceptionUtility::teamFinalTaskAnswerNotFoundException);

        boolean wasFinal = teamFinalTaskAnswer.getFinalTaskAnswer() != null
                && teamFinalTaskAnswer.getFinalTaskAnswer().getId().equals(taskAnswer.getId());

        detachTaskAnswerFiles(taskAnswer);
        taskAnswerRepository.delete(taskAnswer);
        taskAnswerRepository.flush();

        if (wasFinal) {
            teamFinalTaskAnswer.setFinalTaskAnswer(resolveNextFinalTaskAnswer(team, task, taskAnswer));
            teamFinalTaskAnswer = teamFinalTaskAnswerRepository.save(teamFinalTaskAnswer);
        }

        return TaskAnswerMapper.toModel(teamFinalTaskAnswer);
    }

    private TaskAnswer resolveNextFinalTaskAnswer(Team team, Task task, TaskAnswer declinedTaskAnswer) {
        var finalizationType = task.getTaskAnswerFinalizationType();
        if (finalizationType == null) {
            return null;
        }

        return switch (finalizationType) {
            case FIRST_ATTACHMENT -> findEarliestTaskAnswer(team, task);
            case LAST_ATTACHMENT -> findLatestTaskAnswer(team, task);
            case CAPTAIN_CHOOSE -> null;
            case MOST_VOTES -> voteService.findTaskAnswerWithVotesPercentageMoreThan(team, task, declinedTaskAnswer, 50);
            case QUALIFIED_MAJORITY -> voteService.findTaskAnswerWithVotesPercentageMoreThan(team, task, declinedTaskAnswer, 67);
        };
    }

    private TaskAnswer findEarliestTaskAnswer(Team team, Task task) {
        return findAllTeamTaskAnswers(team, task).stream()
                .min(Comparator.comparing(TaskAnswer::getUploadedAt))
                .orElse(null);
    }

    private TaskAnswer findLatestTaskAnswer(Team team, Task task) {
        return findAllTeamTaskAnswers(team, task).stream()
                .max(Comparator.comparing(TaskAnswer::getUploadedAt))
                .orElse(null);
    }

    private ArrayList<TaskAnswer> findAllTeamTaskAnswers(Team team, Task task) {
        var teamUserIds = new HashSet<UUID>();
        if (team.getCaptain() != null) {
            teamUserIds.add(team.getCaptain().getId());
        }

        if (team.getTeamMembers() != null) {
            team.getTeamMembers().forEach(teamMember -> {
                if (teamMember.getUser() != null) {
                    teamUserIds.add(teamMember.getUser().getId());
                }
            });
        }

        return new ArrayList<>(taskAnswerRepository.findAllByTaskIdAndUserIdInOrderByUploadedAtDesc(task.getId(), teamUserIds));
    }

    private void detachTaskAnswerFiles(TaskAnswer taskAnswer) {
        if (taskAnswer.getFiles() == null || taskAnswer.getFiles().isEmpty()) {
            return;
        }

        for (File file : taskAnswer.getFiles()) {
            file.setTaskAnswer(null);
        }

        fileRepository.saveAll(taskAnswer.getFiles());
    }
}
