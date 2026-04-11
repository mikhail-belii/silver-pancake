package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.draft.DraftModel;
import silverpancake.application.model.draft.DraftPickTurnModel;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.draft.DraftPickTurn;

@UtilityClass
@ExtensionMethod({SimpleUserMapper.class, TeamMapper.class})
public class DraftMapper {
    public DraftModel toModel(Draft draft) {
        if (draft == null) {
            return null;
        }
        return new DraftModel()
                .setId(draft.getId())
                .setDraftPickTurns(draft.getDraftPickTurns().stream().map(DraftMapper::toModel).toList())
                .setTeams(draft.getTeams().stream().map(t -> t.toModel()).toList())
                .setIsStarted(draft.getIsStarted())
                .setIsEnded(draft.getIsEnded());
    }

    public DraftPickTurnModel toModel(DraftPickTurn draftPickTurn) {
        if (draftPickTurn == null) {
            return null;
        }
        return new DraftPickTurnModel()
                .setId(draftPickTurn.getId())
                .setUser(draftPickTurn.getUser().toModel());
    }
}
