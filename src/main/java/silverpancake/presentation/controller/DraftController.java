package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.draft.DraftModel;
import silverpancake.application.model.task.TaskCreateModel;
import silverpancake.application.model.task.TaskEditModel;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.model.task.TaskShortListModel;
import silverpancake.application.service.DraftService;
import silverpancake.application.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("api/course/task/draft")
@AllArgsConstructor
public class DraftController {
    private final DraftService draftService;

    @GetMapping("{draftId}")
    @Operation(summary = "Get draft")
    public Response<DraftModel> getDraft(@RequestAttribute("authModel") AuthorizationModel authModel,
                                        @PathVariable UUID draftId) {
        return Response.success(draftService.getDraft(authModel.getUserId(), draftId));
    }
}
