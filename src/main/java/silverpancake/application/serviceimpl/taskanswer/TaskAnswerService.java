package silverpancake.application.serviceimpl.taskanswer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.repository.TeamRepository;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.teamfinaltaskanswer.TeamFinalTaskAnswer;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAnswerService {

    private final TeamRepository teamRepository;
    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;

    public void createTaskAnswers(Task task) {
        var teams = teamRepository.findTeamsByTask(task);

        var teamFinalTaskAnswers = teams.stream()
                .map(team -> new TeamFinalTaskAnswer()
                        .setTask(task)
                        .setTeam(team)
                        .setSubmittedAt(null)
                        .setFinalTaskAnswer(null)
                )
                .collect(Collectors.toList());

        teamFinalTaskAnswerRepository.saveAll(teamFinalTaskAnswers);
    }
}
