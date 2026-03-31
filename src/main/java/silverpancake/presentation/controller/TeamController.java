package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.team.TeamShortListModel;
import silverpancake.application.service.TeamService;

import java.util.UUID;

@RestController
@RequestMapping("api/course/{courseId}/task/{taskId}/team")
@AllArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping("list")
    @Operation(summary = "Get teams")
    public Response<TeamShortListModel> getTeams(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                 @PathVariable UUID taskId,
                                                 @PathVariable UUID courseId) {
        return Response.success(teamService.getTeams(authModel.getUserId(), taskId));
    }

    @GetMapping("{teamId}")
    @Operation(summary = "Get concrete team")
    public Response<TeamModel> getTeam(@RequestAttribute("authModel") AuthorizationModel authModel,
                                       @PathVariable UUID teamId,
                                       @PathVariable UUID courseId,
                                       @PathVariable UUID taskId) {
        return Response.success(teamService.getTeam(authModel.getUserId(), teamId));
    }
}
