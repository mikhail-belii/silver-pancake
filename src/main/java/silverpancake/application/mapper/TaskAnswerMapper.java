package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.model.taskanswer.TaskAnswerModel;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.taskanswer.TaskAnswerStatus;
import silverpancake.domain.entity.teamfinaltaskanswer.TeamFinalTaskAnswer;
import silverpancake.domain.entity.user.User;

import java.util.ArrayList;

@UtilityClass
@ExtensionMethod( { SimpleUserMapper.class, TeamMapper.class, TaskMapper.class, SimpleFileMapper.class } )
public class TaskAnswerMapper {

    public FinalTaskAnswerModel toModel(TeamFinalTaskAnswer teamFinalTaskAnswer) {
        return new FinalTaskAnswerModel()
                .setId(teamFinalTaskAnswer.getId())
                .setScore(teamFinalTaskAnswer.getScore())
                .setStatus(resolveStatus(teamFinalTaskAnswer))
                .setSubmittedAt(teamFinalTaskAnswer.getSubmittedAt())
                .setUpdatedAt(teamFinalTaskAnswer.getUpdatedAt())
                .setTask(teamFinalTaskAnswer.getTask() == null ?
                        null :
                        teamFinalTaskAnswer.getTask().toModel())
                .setTeam(teamFinalTaskAnswer.getTeam() == null ?
                        null :
                        TeamMapper.toModel(teamFinalTaskAnswer.getTeam(), null))
                .setTaskAnswer(teamFinalTaskAnswer.getFinalTaskAnswer() == null ?
                        null :
                        toModel(teamFinalTaskAnswer.getFinalTaskAnswer(), true));
    }

    public TaskAnswerModel toModel(TaskAnswer taskAnswer, boolean isFinal) {
        return new TaskAnswerModel()
                .setId(taskAnswer.getId())
                .setTask(taskAnswer.getTask() == null ?
                        null :
                        taskAnswer.getTask().toModel())
                .setUser(taskAnswer.getUser() == null ?
                        null :
                        taskAnswer.getUser().toModel())
                .setFinalDecision(isFinal)
                .setVotesCount(taskAnswer.getVotedUsers() == null ? 0 : taskAnswer.getVotedUsers().size())
                .setVotedUserIds(taskAnswer.getVotedUsers() == null ?
                        new ArrayList<>() :
                        taskAnswer.getVotedUsers().stream()
                                .map(User::getId)
                                .toList())
                .setUploadedAt(taskAnswer.getUploadedAt())
                .setFiles(taskAnswer.getFiles() == null ?
                        new ArrayList<>() :
                        taskAnswer.getFiles().stream()
                                .map(file -> file.toModel())
                                .toList());
    }

    private TaskAnswerStatus resolveStatus(TeamFinalTaskAnswer teamFinalTaskAnswer) {
        if (teamFinalTaskAnswer.getSubmittedAt() == null) {
            return TaskAnswerStatus.NOT_COMPLETED;
        }

        var task = teamFinalTaskAnswer.getTask();
        if (task != null && task.getDeadline() != null && teamFinalTaskAnswer.getSubmittedAt().isAfter(task.getDeadline())) {
            return TaskAnswerStatus.COMPLETED_AFTER_DEADLINE;
        }

        return TaskAnswerStatus.COMPLETED;
    }
}
