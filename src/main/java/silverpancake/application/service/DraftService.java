package silverpancake.application.service;

import silverpancake.application.model.draft.DraftModel;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.team.Team;

import java.util.List;
import java.util.UUID;

public interface DraftService {
    DraftModel getDraft(UUID userId, UUID id);
    void updateNextSelectingCaptain(Draft draft);
    void createDraft(List<Team> teams, Task task);
    void createOrReloadDraftPickTurns(Task task, boolean isCreating);
    void startDraft(Draft draft);
    void endDraft(Draft draft);
    List<Draft> getCurrentDraftsByCourse(Course course);
    boolean canUserObserveDraft(UUID userId, UUID draftId);
}
