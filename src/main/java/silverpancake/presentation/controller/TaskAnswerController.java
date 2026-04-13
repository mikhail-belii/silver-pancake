package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
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
    public FinalTaskAnswerModel attachTaskAnswer(@PathVariable UUID taskId, @RequestBody List<FileModel> files,
                                                 @RequestAttribute("authModel") AuthorizationModel authModel) {
        return null;
    }

    @GetMapping("/task/{taskId}/team/{teamId}/final")
    @Operation(summary = "Получение финального ответа команды")
    public FinalTaskAnswerModel getTeamFinalAnswer(@PathVariable UUID taskId, @PathVariable UUID teamId,
                                                   @RequestAttribute("authModel") AuthorizationModel authModel) {
        return taskAnswerService.getTeamFinalAnswer(authModel.getUserId(), taskId, teamId);
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

    }

    @PostMapping("/task/{taskId}/unsubmit")
    @Operation(summary = "Отменить отправку ответа на задание (финальный)")
    public void unsubmitTaskAnswer(@PathVariable UUID taskId,
                                 @RequestAttribute("authModel") AuthorizationModel authModel) {

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
