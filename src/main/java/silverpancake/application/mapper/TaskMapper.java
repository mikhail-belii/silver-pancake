package silverpancake.application.mapper;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.model.task.TaskShortModel;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.user.User;

@UtilityClass
@ExtensionMethod({SimpleUserMapper.class, SimpleFileMapper.class})
public class TaskMapper {
    public TaskModel toModel(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskModel()
                .setId(task.getId())
                .setAuthor(task.getAuthor().toModel())
                .setFiles(task.getFiles() == null? null: task.getFiles().stream().map(f -> f.toModel()).toList())
                .setCreatedAt(task.getCreatedAt())
                .setDeadline(task.getDeadline())
                .setText(task.getText())
                .setTitle(task.getTitle())
                .setDraftId(task.getDraft() == null ? null : task.getDraft().getId())
                .setMaxScore(task.getMaxScore())
                .setUpdatedAt(task.getUpdatedAt())
                .setTeamFormationType(task.getTeamFormationType())
                .setTaskAnswerFinalizationType(task.getTaskAnswerFinalizationType());
    }

    public TaskShortModel toShortModel(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskShortModel()
                .setId(task.getId())
                .setText(task.getText())
                .setTitle(task.getTitle());
    }
}
