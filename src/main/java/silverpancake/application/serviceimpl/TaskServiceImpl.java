package silverpancake.application.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TaskMapper;
import silverpancake.application.model.task.TaskCreateModel;
import silverpancake.application.model.task.TaskEditModel;
import silverpancake.application.model.task.TaskModel;
import silverpancake.application.model.task.TaskShortListModel;
import silverpancake.application.repository.*;
import silverpancake.application.service.TaskService;
import silverpancake.application.service.TeamService;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.task.TeamFormationType;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.user.UserCourseRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final ExceptionUtility exceptionUtility;
    private final TeamService teamService;

    @Override
    public TaskModel createTask(UUID requestingUserId, UUID courseId, TaskCreateModel taskCreateModel) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        if (taskCreateModel.isNotDraftTypeAndDraftTime()) {
            throw exceptionUtility.notDraftTypeAndDraftTimeException();
        }

        if (taskCreateModel.isDraftTypeAndNotDraftTime()) {
            throw exceptionUtility.draftTypeAndNotDraftTimeException();
        }

        if (taskCreateModel.isDraftStartTimeAfterDeadline()) {
            throw exceptionUtility.draftTimeAfterDeadlineException();
        }

        var task = createTaskFromCreateModel(user, course, taskCreateModel);
        task = taskRepository.save(task);

        var files = buildTaskFiles(taskCreateModel.getFileIds(), task, user, null);
        task.setFiles(files);
        if (files != null && !files.isEmpty()) {
            fileRepository.saveAll(files);
        }

        taskRepository.saveAndFlush(task);

        teamService.createTeamsOnTaskCreated(task, taskCreateModel.getTeamsAmount(), taskCreateModel.getTeamFormationType());

        return TaskMapper.toModel(task);
    }

    @Override
    public TaskModel editTask(UUID requestingUserId, UUID taskId, TaskEditModel taskEditModel) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var task = taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var course = courseRepository.findById(task.getCourse().getId())
                .orElseThrow(exceptionUtility::courseNotFoundException);
        var userCourse = userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        if (userCourse.getUserRole().equals(UserCourseRole.STUDENT)) {
            throw exceptionUtility.securityException();
        }

        if (!task.getTeamFormationType().equals(TeamFormationType.DRAFT) && taskEditModel.getDraftStartTime() != null) {
            throw exceptionUtility.notDraftTypeAndDraftTimeException();
        }

        if (task.getTeamFormationType().equals(TeamFormationType.DRAFT) && taskEditModel.getDraftStartTime() == null) {
            throw exceptionUtility.draftTypeAndNotDraftTimeException();
        }

        if (taskEditModel.isDraftStartTimeAfterDeadline()) {
            throw exceptionUtility.draftTimeAfterDeadlineException();
        }

        task.setTitle(taskEditModel.getTitle())
                .setText(taskEditModel.getText())
                .setMaxScore(taskEditModel.getMaxScore())
                .setDeadline(taskEditModel.getDeadlineTime())
                .setDraftStartTime(taskEditModel.getDraftStartTime())
                .setUpdatedAt(LocalDateTime.now());

        task = taskRepository.save(task);

        var files = buildTaskFiles(taskEditModel.getFileIds(), task, user, taskId);
        task.setFiles(files);
        if (files != null && !files.isEmpty()) {
            fileRepository.saveAll(files);
        }

        taskRepository.saveAndFlush(task);

        return TaskMapper.toModel(task);
    }

    @Override
    public TaskShortListModel getTasks(UUID requestingUserId, UUID courseId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var course = courseRepository.findById(courseId)
                .orElseThrow(exceptionUtility::courseNotFoundException);
        userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);
        var tasks = taskRepository.getTasksByCourse(course);

        return new TaskShortListModel(tasks.stream().map(TaskMapper::toShortModel).collect(Collectors.toList()));
    }

    @Override
    public TaskModel getTask(UUID requestingUserId, UUID taskId) {
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);
        var task = taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var course = courseRepository.findById(task.getCourse().getId())
                .orElseThrow(exceptionUtility::courseNotFoundException);
        userCourseRepository.findByUserAndCourse(user.getId(), course.getId())
                .orElseThrow(exceptionUtility::requestingUserNotCourseMemberException);

        return TaskMapper.toModel(task);
    }

    private List<File> buildTaskFiles(List<UUID> fileIds,
                                      Task task,
                                      User user,
                                      UUID currentTaskId) {
        if (fileIds.isEmpty()) {
            return task.getFiles();
        }

        var files = fileRepository.findAllById(fileIds);
        if (files.size() != fileIds.size()) {
            throw exceptionUtility.filesNotFoundException();
        }

        var filesById = files.stream()
                .collect(Collectors.toMap(File::getId, Function.identity()));

        var newFiles = new ArrayList<File>();

        for (UUID fileId : fileIds) {
            var file = filesById.get(fileId);
            if (file == null) {
                throw exceptionUtility.filesNotFoundException();
            }

            if (file.getUploader() == null || !file.getUploader().getId().equals(user.getId())) {
                throw exceptionUtility.attachOnlyYourFilesException();
            }

            var attachedToAnotherPost = file.getTask() != null
                    && (currentTaskId == null || !file.getTask().getId().equals(currentTaskId));
            if (attachedToAnotherPost || file.getTaskAnswer() != null) {
                throw exceptionUtility.fileAlreadyAttachedException();
            }

            newFiles.add(file);
        }

        if (task.getFiles() != null) {
            task.getFiles().forEach(file -> file.setTask(null));
        }

        for (File file : newFiles) {
            file.setTask(task);
            file.setTaskAnswer(null);
        }

        return newFiles;
    }

    private Task createTaskFromCreateModel(User user, Course course, TaskCreateModel taskCreateModel) {
        return new Task()
                .setAuthor(user)
                .setCourse(course)
                .setTitle(taskCreateModel.getTitle())
                .setText(taskCreateModel.getText())
                .setTeamFormationType(taskCreateModel.getTeamFormationType())
                .setMaxScore(taskCreateModel.getMaxScore())
                .setDeadline(taskCreateModel.getDeadlineTime())
                .setDraftStartTime(taskCreateModel.getDraftStartTime())
                .setCreatedAt(LocalDateTime.now());
    }
}
