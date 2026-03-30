package silverpancake.application.service;

import silverpancake.application.model.task.TaskCreateModel;
import silverpancake.application.model.task.TaskEditModel;
import silverpancake.application.model.task.TaskModel;

import java.util.UUID;

public interface TaskService {
    TaskModel createTask(UUID requestingUserId, UUID courseId, TaskCreateModel taskCreateModel);
    TaskModel editTask(UUID requestingUserId, UUID taskId, TaskEditModel taskEditModel);
}
