package silverpancake.application.serviceimpl.taskanswer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.mapper.TaskAnswerMapper;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModelWithAnswerId;
import silverpancake.application.model.taskanswer.TaskAnswerModel;
import silverpancake.application.repository.FileRepository;
import silverpancake.application.repository.TaskAnswerRepository;
import silverpancake.application.repository.TaskRepository;
import silverpancake.application.repository.TeamRepository;
import silverpancake.application.repository.TeamFinalTaskAnswerRepository;
import silverpancake.application.repository.UserRepository;
import silverpancake.application.repository.UserTeamRepository;
import silverpancake.application.util.ExceptionUtility;
import silverpancake.domain.entity.file.File;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;
import silverpancake.domain.entity.teamfinaltaskanswer.TeamFinalTaskAnswer;
import silverpancake.domain.entity.user.User;
import silverpancake.domain.entity.userteam.UserTeam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAnswerService {

    private final TaskAnswerAttachmentService taskAnswerAttachmentService;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final TaskAnswerRepository taskAnswerRepository;
    private final TeamFinalTaskAnswerRepository teamFinalTaskAnswerRepository;
    private final UserTeamRepository userTeamRepository;
    private final ExceptionUtility exceptionUtility;

    public FinalTaskAnswerModelWithAnswerId attachAnswer(UUID taskId, List<FileModel> files, UUID requestingUserId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);
        var user = userRepository.findById(requestingUserId)
                .orElseThrow(exceptionUtility::userNotFoundException);

        var team = getRequestingUserTeam(requestingUserId, taskId);
        checkIfUserInTeam(requestingUserId, team);

        var taskAnswer = createTaskAnswer(task, user, files);

        var finalTaskAnswer = taskAnswerAttachmentService.attachAnswer(team, task, taskAnswer);

        return new FinalTaskAnswerModelWithAnswerId()
                .setFinalTaskAnswer(finalTaskAnswer)
                .setNewTaskAnswerId(taskAnswer.getId());
    }

    public void createTaskAnswers(Task task) {
        var teams = teamRepository.findTeamsByTask(task);

        var teamFinalTaskAnswers = teams.stream()
                .map(team -> new TeamFinalTaskAnswer()
                        .setTask(task)
                        .setTeam(team)
                        .setSubmittedAt(null)
                        .setFinalTaskAnswer(null)
                )
                .collect(Collectors.toList());

        teamFinalTaskAnswerRepository.saveAll(teamFinalTaskAnswers);
    }

    public FinalTaskAnswerModel getTeamFinalAnswer(UUID requestingUserId, UUID taskId, UUID teamId) {
        var teamFinalTaskAnswer = getValidatedTeamFinalTaskAnswer(requestingUserId, taskId, teamId);
        return TaskAnswerMapper.toModel(teamFinalTaskAnswer);
    }

    public List<TaskAnswerModel> getAllTeamTaskAnswers(UUID requestingUserId, UUID taskId, UUID teamId) {
        var teamFinalTaskAnswer = getValidatedTeamFinalTaskAnswer(requestingUserId, taskId, teamId);
        var team = teamFinalTaskAnswer.getTeam();

        var teamUserIds = new HashSet<UUID>();
        if (team.getCaptain() != null) {
            teamUserIds.add(team.getCaptain().getId());
        }

        if (team.getTeamMembers() != null) {
            team.getTeamMembers().forEach(teamMember -> teamUserIds.add(teamMember.getUser().getId()));
        }

        if (teamUserIds.isEmpty()) {
            return new ArrayList<>();
        }

        var finalAnswerId = teamFinalTaskAnswer.getFinalTaskAnswer() == null
                ? null
                : teamFinalTaskAnswer.getFinalTaskAnswer().getId();

        return taskAnswerRepository.findAllByTaskIdAndUserIdInOrderByUploadedAtDesc(taskId, teamUserIds).stream()
                .map(taskAnswer -> TaskAnswerMapper.toModel(
                        taskAnswer,
                        taskAnswer.getId().equals(finalAnswerId)
                ))
                .toList();
    }

    public void submitTaskAnswer(UUID requestingUserId, UUID taskId) {
        var teamFinalTaskAnswer = getRequestingUserTeamFinalTaskAnswer(requestingUserId, taskId);
        teamFinalTaskAnswer.setSubmittedAt(LocalDateTime.now());
        teamFinalTaskAnswerRepository.save(teamFinalTaskAnswer);
    }

    public void unsubmitTaskAnswer(UUID requestingUserId, UUID taskId) {
        var teamFinalTaskAnswer = getRequestingUserTeamFinalTaskAnswer(requestingUserId, taskId);
        teamFinalTaskAnswer.setSubmittedAt(null);
        teamFinalTaskAnswerRepository.save(teamFinalTaskAnswer);
    }

    private TeamFinalTaskAnswer getValidatedTeamFinalTaskAnswer(UUID requestingUserId, UUID taskId, UUID teamId) {
        taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);

        var team = teamRepository.findById(teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);

        if (team.getTask() == null || !team.getTask().getId().equals(taskId)) {
            throw exceptionUtility.teamNotFoundException();
        }

        checkIfUserInTeam(requestingUserId, team);

        return teamFinalTaskAnswerRepository.findByTaskIdAndTeamId(taskId, teamId)
                .orElseThrow(exceptionUtility::teamNotFoundException);
    }

    private TeamFinalTaskAnswer getRequestingUserTeamFinalTaskAnswer(UUID requestingUserId, UUID taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(exceptionUtility::taskNotFoundException);

        var team = getRequestingUserTeam(requestingUserId, taskId);
        checkIfUserInTeam(requestingUserId, team);

        return teamFinalTaskAnswerRepository.findByTaskIdAndTeamId(taskId, team.getId())
                .orElseThrow(exceptionUtility::teamNotFoundException);
    }

    private Team getRequestingUserTeam(UUID requestingUserId, UUID taskId) {
        var captainTeam = teamRepository.findByCaptainIdAndTaskId(requestingUserId, taskId);

        return captainTeam.orElseGet(() -> userTeamRepository.findByUserIdAndTeamTaskId(requestingUserId, taskId)
                .map(UserTeam::getTeam)
                .orElseThrow(exceptionUtility::teamNotFoundException));
    }

    private TaskAnswer createTaskAnswer(Task task, User user, List<FileModel> files) {
        var taskAnswer = new TaskAnswer()
                .setTask(task)
                .setUser(user)
                .setUploadedAt(LocalDateTime.now());

        taskAnswer = taskAnswerRepository.save(taskAnswer);

        var attachedFiles = buildTaskAnswerFiles(files, taskAnswer, user);
        taskAnswer.setFiles(attachedFiles);

        if (!attachedFiles.isEmpty()) {
            fileRepository.saveAll(attachedFiles);
        }

        return taskAnswerRepository.save(taskAnswer);
    }

    private List<File> buildTaskAnswerFiles(List<FileModel> files, TaskAnswer taskAnswer, User user) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        var fileIds = files.stream()
                .map(FileModel::getId)
                .toList();

        var foundFiles = fileRepository.findAllById(fileIds);
        if (foundFiles.size() != fileIds.size()) {
            throw exceptionUtility.filesNotFoundException();
        }

        var filesById = foundFiles.stream()
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

            if (file.getTask() != null || file.getTaskAnswer() != null) {
                throw exceptionUtility.fileAlreadyAttachedException();
            }

            file.setTask(null);
            file.setTaskAnswer(taskAnswer);
            newFiles.add(file);
        }

        return newFiles;
    }

    private void checkIfUserInTeam(UUID requestingUserId, Team team) {
        boolean isCaptain = team.getCaptain() != null && team.getCaptain().getId().equals(requestingUserId);
        boolean isMember = team.getTeamMembers() != null && team.getTeamMembers().stream()
                .anyMatch(teamMember -> teamMember.getUser() != null
                        && teamMember.getUser().getId().equals(requestingUserId));

        if (!isCaptain && !isMember) {
            throw exceptionUtility.securityException();
        }
    }
}
