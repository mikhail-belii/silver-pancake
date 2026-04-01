package silverpancake.application.service;

import silverpancake.application.model.course.UserCourseListModel;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.team.TeamShortListModel;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.task.TeamFormationType;

import java.util.UUID;

public interface TeamService {
    void createTeamsOnTaskCreated(Task task, Integer teamsAmount, TeamFormationType teamFormationType);
    TeamShortListModel getTeams(UUID requestingUserId, UUID taskId);
    TeamModel getTeam(UUID requestingUserId, UUID teamId);
    TeamModel assignTeamCaptain(UUID requestingUserId, UUID teamId, UUID studentId);
    UserCourseListModel getFreeStudentsForTask(UUID requestingUserId, UUID taskId);
    TeamModel addTeamMember(UUID requestingUserId, UUID teamId, UUID studentId);
    TeamModel removeTeamMember(UUID requestingUserId, UUID teamId, UUID teamMemberId);
}
