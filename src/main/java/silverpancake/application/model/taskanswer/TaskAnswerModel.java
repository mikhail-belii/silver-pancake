package silverpancake.application.model.taskanswer;

import lombok.Data;
import lombok.experimental.Accessors;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.model.user.UserModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class TaskAnswerModel {

    private UUID id;

    List<FileModel> files = new ArrayList<>();

    private UserModel user;

    private TaskModel task;

    private boolean finalDecision = false;

    private int votesCount = 0;

    private List<UUID> votedUserIds = new ArrayList<>();

    private LocalDateTime uploadedAt;
}
