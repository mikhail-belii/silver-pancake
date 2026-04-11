package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.team.TeamModel;
import silverpancake.application.model.team.TeamShortModel;
import silverpancake.domain.entity.team.Team;

@UtilityClass
@ExtensionMethod(SimpleUserMapper.class)
public class TeamMapper {
    public TeamModel toModel(Team team) {
        if (team == null) {
            return null;
        }
        return new TeamModel()
                .setId(team.getId())
                .setName(team.getName())
                .setCaptain(team.getCaptain() == null ? null : team.getCaptain().toModel())
                .setMembers(team.getTeamMembers() == null
                        ? java.util.List.of()
                        : team.getTeamMembers().stream().map(ut -> ut.getUser().toModel()).toList());
    }

    public TeamShortModel toShortModel(Team team) {
        if (team == null) {
            return null;
        }
        return new TeamShortModel()
                .setId(team.getId())
                .setName(team.getName());
    }
}
