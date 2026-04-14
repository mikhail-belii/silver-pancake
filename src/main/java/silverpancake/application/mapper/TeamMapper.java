package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.team.TeamShortModel;
import silverpancake.domain.entity.team.Team;

import java.util.List;
import java.util.UUID;

@UtilityClass
@ExtensionMethod(SimpleUserMapper.class)
public class TeamMapper {
    public TeamModel toModel(Team team, UUID requestingUserId) {
        if (team == null) {
            return null;
        }
        boolean isCaptain = team.getCaptain() != null && team.getCaptain().getId().equals(requestingUserId);
        boolean isMember = team.getTeamMembers() != null
                && team.getTeamMembers().stream().anyMatch(ut -> ut.getUser().getId().equals(requestingUserId));
        return new TeamModel()
                .setId(team.getId())
                .setName(team.getName())
                .setCaptain(team.getCaptain() == null ? null : team.getCaptain().toModel())
                .setMembers(team.getTeamMembers() == null
                        ? List.of()
                        : team.getTeamMembers().stream().map(ut -> ut.getUser().toModel()).toList())
                .setIsMember(isMember)
                .setIsCaptain(isCaptain);
    }

    public TeamShortModel toShortModel(Team team, UUID requestingUserId) {
        if (team == null) {
            return null;
        }
        boolean isCaptain = team.getCaptain() != null && team.getCaptain().getId().equals(requestingUserId);
        boolean isMember = team.getTeamMembers() != null
                && team.getTeamMembers().stream().anyMatch(ut -> ut.getUser().getId().equals(requestingUserId));
        return new TeamShortModel()
                .setId(team.getId())
                .setName(team.getName())
                .setIsMember(isMember)
                .setIsCaptain(isCaptain);
    }
}
