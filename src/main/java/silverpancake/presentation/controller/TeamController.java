package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.course.UserCourseListModel;
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
                                                 @PathVariable UUID taskId) {
        return Response.success(teamService.getTeams(authModel.getUserId(), taskId));
    }

    @GetMapping("{teamId}")
    @Operation(summary = "Get concrete team")
    public Response<TeamModel> getTeam(@RequestAttribute("authModel") AuthorizationModel authModel,
                                       @PathVariable UUID teamId) {
        return Response.success(teamService.getTeam(authModel.getUserId(), teamId));
    }

    @GetMapping("my")
    @Operation(summary = "Get requesting user's team for task")
    public Response<TeamModel> getMyTeam(@RequestAttribute("authModel") AuthorizationModel authModel,
                                         @PathVariable UUID taskId) {
        return Response.success(teamService.getMyTeam(authModel.getUserId(), taskId));
    }

    @PostMapping("{teamId}/captain/{studentId}")
    @Operation(summary = "Assign captain to concrete team (only for teachers)")
    public Response<TeamModel> assignTeamCaptain(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                 @PathVariable UUID teamId,
                                                 @PathVariable UUID studentId) {
        return Response.success(teamService.assignTeamCaptain(authModel.getUserId(), teamId, studentId));
    }

    @PostMapping("{teamId}/member/{studentId}")
    @Operation(summary = "Add team member (only for teachers/captains)")
    public Response<TeamModel> addTeamMember(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                 @PathVariable UUID teamId,
                                                 @PathVariable UUID studentId) {
        return Response.success(teamService.addTeamMember(authModel.getUserId(), teamId, studentId));
    }

    @DeleteMapping("{teamId}/member/{teamMemberId}")
    @Operation(summary = "Remove team member including captains (only for teachers)")
    public Response<TeamModel> removeTeamMember(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                 @PathVariable UUID teamId,
                                                 @PathVariable UUID teamMemberId) {
        return Response.success(teamService.removeTeamMember(authModel.getUserId(), teamId, teamMemberId));
    }

    @GetMapping("free-students")
    @Operation(summary = "Get free students for task (only for teachers)")
    public Response<UserCourseListModel> getFreeStudentsForTask(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                                @PathVariable UUID taskId) {
        return Response.success(teamService.getFreeStudentsForTask(authModel.getUserId(), taskId));
    }

    @PostMapping("{teamId}/join")
    @Operation(summary = "Join team for free team formation (only for students)")
    public Response<TeamModel> joinTeam(@RequestAttribute("authModel") AuthorizationModel authModel,
                                        @PathVariable UUID teamId) {
        return Response.success(teamService.joinTeam(authModel.getUserId(), teamId));
    }

    @DeleteMapping("{teamId}/leave")
    @Operation(summary = "Leave team for free team formation (only for students)")
    public Response<TeamModel> leaveTeam(@RequestAttribute("authModel") AuthorizationModel authModel,
                                         @PathVariable UUID teamId) {
        return Response.success(teamService.leaveTeam(authModel.getUserId(), teamId));
    }
}
