package silverpancake.application.util.teamformation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.util.teamformation.strategy.CustomTeamFormation;
import silverpancake.application.util.teamformation.strategy.DraftTeamFormation;
import silverpancake.application.util.teamformation.strategy.FreeTeamFormation;
import silverpancake.application.util.teamformation.strategy.RandomTeamFormation;
import silverpancake.domain.entity.task.TeamFormationType;

@Component
@RequiredArgsConstructor
public class TeamFormationFactory {
    private final RandomTeamFormation randomTeamFormation;
    private final FreeTeamFormation freeTeamFormation;
    private final CustomTeamFormation customTeamFormation;
    private final DraftTeamFormation draftTeamFormation;

    public TeamFormation createTeamFormation(TeamFormationType teamFormationType) {
        return switch (teamFormationType) {
            case RANDOM -> randomTeamFormation;
            case FREE -> freeTeamFormation;
            case CUSTOM -> customTeamFormation;
            case DRAFT -> draftTeamFormation;
        };
    }
}
