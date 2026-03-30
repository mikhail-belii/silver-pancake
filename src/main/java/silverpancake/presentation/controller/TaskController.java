package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.task.TaskCreateModel;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("api/course/{courseId}/task")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task")
    public Response<TaskModel> createTask(@RequestAttribute("authModel") AuthorizationModel authModel,
                                          @PathVariable UUID courseId,
                                          @Valid @RequestBody TaskCreateModel taskCreateModel) {
        return Response.success(taskService.createTask(authModel.getUserId(), courseId, taskCreateModel));
    }
}
