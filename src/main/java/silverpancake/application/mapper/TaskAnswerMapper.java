package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.model.taskanswer.TaskAnswerModel;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.teamfinaltaskanswer.TeamFinalTaskAnswer;

import java.util.ArrayList;

@UtilityClass
@ExtensionMethod( { SimpleUserMapper.class, TeamMapper.class, TaskMapper.class, SimpleFileMapper.class } )
public class TaskAnswerMapper {

    public FinalTaskAnswerModel toModel(TeamFinalTaskAnswer teamFinalTaskAnswer) {
        return new FinalTaskAnswerModel()
                .setId(teamFinalTaskAnswer.getId())
                .setScore(teamFinalTaskAnswer.getScore())
                .setStatus(teamFinalTaskAnswer.getStatus())
                .setSubmittedAt(teamFinalTaskAnswer.getSubmittedAt())
                .setUpdatedAt(teamFinalTaskAnswer.getUpdatedAt())
                .setTask(teamFinalTaskAnswer.getTask() == null ?
                        null :
                        teamFinalTaskAnswer.getTask().toModel())
                .setTeam(teamFinalTaskAnswer.getTeam() == null ?
                        null :
                        teamFinalTaskAnswer.getTeam().toModel())
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
                .setUploadedAt(taskAnswer.getUploadedAt())
                .setFiles(taskAnswer.getFiles() == null ?
                        new ArrayList<>() :
                        taskAnswer.getFiles().stream()
                                .map(file -> file.toModel())
                                .toList());
    }
}
