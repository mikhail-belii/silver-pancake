package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerGradeModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModelWithAnswerId;
import silverpancake.application.model.taskanswer.TaskAnswerModel;
import silverpancake.application.serviceimpl.taskanswer.TaskAnswerService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/task-answer")
public class TaskAnswerController {

    private final TaskAnswerService taskAnswerService;

    @PostMapping("/task/{taskId}/answers")
    @Operation(summary = "Прикрепление ответа на задание, !!! возвращает модель финального задания команды")
    public FinalTaskAnswerModelWithAnswerId attachTaskAnswer(@PathVariable UUID taskId, @RequestBody List<FileModel> files,
                                                             @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.attachAnswer(taskId, files, authModel.getUserId());
    }

    @GetMapping("/task/{taskId}/team/{teamId}/final")
    @Operation(summary = "Получение финального ответа команды")
    public FinalTaskAnswerModel getTeamFinalAnswer(@PathVariable UUID taskId, @PathVariable UUID teamId,
                                                   @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.getTeamFinalAnswer(authModel.getUserId(), taskId, teamId);
    }

    @GetMapping("/{answerId}")
    @Operation(summary = "Получение TaskAnswer по id")
    public TaskAnswerModel getTaskAnswer(@PathVariable UUID answerId) {
        return taskAnswerService.getTaskAnswer(answerId);
    }

    @GetMapping("/final/{teamFinalTaskAnswerId}")
    @Operation(summary = "Получение TeamFinalTaskAnswer по id")
    public FinalTaskAnswerModel getTeamFinalTaskAnswer(@PathVariable UUID teamFinalTaskAnswerId) {
        return taskAnswerService.getTeamFinalTaskAnswer(teamFinalTaskAnswerId);
    }

    @GetMapping("/task/{taskId}/my-attached")
    @Operation(summary = "Получение всех TaskAnswer текущего пользователя по заданию")
    public List<TaskAnswerModel> getAllUserTaskAnswers(@PathVariable UUID taskId,
                                                       @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.getAllUserTaskAnswers(authModel.getUserId(), taskId);
    }

    @GetMapping("/task/{taskId}/my-votes")
    @Operation(summary = "Получение всех голосов текущего пользователя по заданию")
    public List<TaskAnswerModel> getAllUserVotedTaskAnswers(@PathVariable UUID taskId,
                                                            @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.getAllUserVotedTaskAnswers(authModel.getUserId(), taskId);
    }

    @GetMapping("/task/{taskId}/team/{teamId}/all")
    @Operation(summary = "Получение всех ответов команды")
    public List<TaskAnswerModel> getAllTeamTaskAnswers(@PathVariable UUID taskId, @PathVariable UUID teamId,
                                                       @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.getAllTeamTaskAnswers(authModel.getUserId(), taskId, teamId);
    }

    @PostMapping("/task/{taskId}/submit")
    @Operation(summary = "Отправить ответ на задание (финальный)")
    public void submitTaskAnswer(@PathVariable UUID taskId,
                                 @RequestAttribute("authModel") AuthorizationModel authModel) {
        taskAnswerService.submitTaskAnswer(authModel.getUserId(), taskId);
    }

    @PostMapping("/task/{taskId}/unsubmit")
    @Operation(summary = "Отменить отправку ответа на задание (финальный)")
    public void unsubmitTaskAnswer(@PathVariable UUID taskId,
                                 @RequestAttribute("authModel") AuthorizationModel authModel) {
        taskAnswerService.unsubmitTaskAnswer(authModel.getUserId(), taskId);
    }

    @PostMapping("/final/{teamFinalTaskAnswerId}/grade")
    @Operation(summary = "Оценить TeamFinalTaskAnswer")
    public FinalTaskAnswerModel gradeTaskAnswer(@PathVariable UUID teamFinalTaskAnswerId,
                                                @Valid @RequestBody FinalTaskAnswerGradeModel gradeModel,
                                                @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.gradeTaskAnswer(authModel.getUserId(), teamFinalTaskAnswerId, gradeModel.getScore());
    }

    @PostMapping("task/{taskId}/answers/{answerId}/vote")
    @Operation(summary = "Отдать голос за ответ на задание. При повторном голосовании за тот же вариант, убирает голос")
    public void voteForAnswer(@PathVariable UUID taskId, @PathVariable UUID answerId,
                              @RequestAttribute("authModel") AuthorizationModel authModel) {

    }

    @PostMapping("/task/{taskId}/answers/{answerId}/select")
    @Operation(summary = "[Капитан] Выбор ответа, который будет считаться финальным")
    public void selectAnswer(@PathVariable UUID taskId, @PathVariable UUID answerId,
                             @RequestAttribute("authModel") AuthorizationModel authModel) {

    }
}
