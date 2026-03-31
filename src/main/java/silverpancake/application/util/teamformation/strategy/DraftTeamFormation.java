package silverpancake.application.util.teamformation.strategy;

import org.springframework.stereotype.Component;
import silverpancake.application.util.teamformation.TeamFormation;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;

import java.util.List;

@Component
public class DraftTeamFormation implements TeamFormation {
    @Override
    public void formTeams(Task task, List<Team> teams) {
    }
}
