package silverpancake.application.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.repository.TaskAnswerRepository;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl {

    private final TaskAnswerRepository taskAnswerRepository;
    private final ExceptionUtility exceptionUtility;

    public void voteForAnswer(Team team, Task task, TaskAnswer taskAnswer) {
        var user = taskAnswer.getUser();
        if (user == null) {
            throw exceptionUtility.userNotFoundException();
        }

        if (taskAnswer.getTask() == null || !taskAnswer.getTask().getId().equals(task.getId())) {
            throw exceptionUtility.taskAnswerNotFoundException();
        }

        voteForAnswer(user, team, taskAnswer);
    }

    public TaskAnswer findTaskAnswerWithVotesPercentageMoreThan(Team team, Task task, TaskAnswer taskAnswer, int percent) {
        if (taskAnswer.getTask() == null || !taskAnswer.getTask().getId().equals(task.getId())) {
            throw exceptionUtility.taskAnswerNotFoundException();
        }

        if (!isTaskAnswerBelongsToTeam(taskAnswer, team)) {
            throw exceptionUtility.securityException();
        }

        var teamUserIds = getTeamUserIds(team);
        if (teamUserIds.isEmpty()) {
            return null;
        }

        return taskAnswerRepository.findAllByTaskIdAndUserIdInOrderByUploadedAtDesc(task.getId(), teamUserIds).stream()
                .filter(answer -> answer.getVotedUsers() != null
                        && answer.getVotedUsers().size() * 100 > percent * teamUserIds.size())
                .min(Comparator.comparing(TaskAnswer::getUploadedAt))
                .orElse(null);
    }

    private boolean isTaskAnswerBelongsToTeam(TaskAnswer taskAnswer, Team team) {
        if (taskAnswer.getUser() == null) {
            return false;
        }

        var answerUserId = taskAnswer.getUser().getId();
        if (team.getCaptain() != null && team.getCaptain().getId().equals(answerUserId)) {
            return true;
        }

        return team.getTeamMembers() != null && team.getTeamMembers().stream()
                .anyMatch(teamMember -> teamMember.getUser() != null
                        && teamMember.getUser().getId().equals(answerUserId));
    }

    private void voteForAnswer(User user, Team team, TaskAnswer taskAnswer) {
        if (taskAnswer.getTask() == null) {
            throw exceptionUtility.taskNotFoundException();
        }

        if (!isUserInTeam(user, team)) {
            throw exceptionUtility.securityException();
        }

        if (!isTaskAnswerBelongsToTeam(taskAnswer, team)) {
            throw exceptionUtility.securityException();
        }

        var taskId = taskAnswer.getTask().getId();
        var answerId = taskAnswer.getId();
        var alreadyVotedAnswers = new ArrayList<>(
                taskAnswerRepository.findAllByTaskIdAndVotedUsersIdOrderByUploadedAtDesc(taskId, user.getId())
        );

        boolean alreadyVotedForThisAnswer = alreadyVotedAnswers.stream()
                .anyMatch(answer -> answer.getId().equals(answerId));

        for (TaskAnswer votedAnswer : alreadyVotedAnswers) {
            votedAnswer.getVotedUsers().removeIf(votedUser -> votedUser.getId().equals(user.getId()));
        }

        if (!alreadyVotedForThisAnswer) {
            taskAnswer.getVotedUsers().add(user);
        }

        taskAnswerRepository.saveAll(alreadyVotedAnswers);
        taskAnswerRepository.save(taskAnswer);
    }

    private boolean isUserInTeam(User user, Team team) {
        if (team.getCaptain() != null && team.getCaptain().getId().equals(user.getId())) {
            return true;
        }

        return team.getTeamMembers() != null && team.getTeamMembers().stream()
                .anyMatch(teamMember -> teamMember.getUser() != null
                        && teamMember.getUser().getId().equals(user.getId()));
    }

    private Set<UUID> getTeamUserIds(Team team) {
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

        return teamUserIds;
    }
}
