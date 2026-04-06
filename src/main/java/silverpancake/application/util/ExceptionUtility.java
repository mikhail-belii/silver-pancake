package silverpancake.application.util;

import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import silverpancake.application.exception.ExceptionWrapper;

@Component
@RequiredArgsConstructor
public class ExceptionUtility {
    private final ErrorProperties errorProperties;

    public ExceptionWrapper userNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getUserNotFound());
        return notFoundEx;
    }

    public ExceptionWrapper filesNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getFilesNotFound());
        return notFoundEx;
    }

    public ExceptionWrapper courseNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getCourseNotFound());
        return notFoundEx;
    }

    public ExceptionWrapper taskNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getTaskNotFound());
        return notFoundEx;
    }

    public ExceptionWrapper teamNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getTeamNotFound());
        return notFoundEx;
    }

    public ExceptionWrapper userWithEmailExistsException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getUserWithEmailExists());
        return badRequestEx;
    }

    public ExceptionWrapper incorrectEmailOrPasswordException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getIncorrectEmailOrPassword());
        return badRequestEx;
    }

    public ExceptionWrapper fileEmptyException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getFileEmpty());
        return badRequestEx;
    }

    public ExceptionWrapper fileSizeException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getFileSize());
        return badRequestEx;
    }

    public ExceptionWrapper fileExtensionRequiredException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getFileExtensionRequired());
        return badRequestEx;
    }

    public ExceptionWrapper fileAllowedExtensionsException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getFileAllowedExtensions());
        return badRequestEx;
    }

    public ExceptionWrapper userAlreadyCourseMemberException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getUserAlreadyCourseMember());
        return badRequestEx;
    }

    public ExceptionWrapper fileAlreadyAttachedException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getFileAlreadyAttached());
        return badRequestEx;
    }

    public ExceptionWrapper notDraftTypeAndDraftTimeException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getNotDraftTypeAndDraftTime());
        return badRequestEx;
    }

    public ExceptionWrapper draftTypeAndNotDraftTimeException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getDraftTypeAndNotDraftTime());
        return badRequestEx;
    }

    public ExceptionWrapper draftTimeAfterDeadlineException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getDraftTimeAfterDeadline());
        return badRequestEx;
    }

    public ExceptionWrapper onlyStudentCanBeTeamMemberException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getOnlyStudentCanBeTeamMember());
        return badRequestEx;
    }

    public ExceptionWrapper studentAlreadyCaptainException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getStudentAlreadyCaptain());
        return badRequestEx;
    }

    public ExceptionWrapper teamAlreadyHasCaptainException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getTeamAlreadyHasCaptain());
        return badRequestEx;
    }

    public ExceptionWrapper studentAlreadyInAnotherTeamException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getStudentAlreadyInAnotherTeam());
        return badRequestEx;
    }

    public ExceptionWrapper studentAlreadyInThisTeamException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getStudentAlreadyInThisTeam());
        return badRequestEx;
    }

    public ExceptionWrapper studentNotInThisTeamException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getStudentNotInThisTeam());
        return badRequestEx;
    }

    public ExceptionWrapper teamJoiningAvailableOnlyForFreeFormationException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getTeamJoiningAvailableOnlyForFreeFormation());
        return badRequestEx;
    }

    public ExceptionWrapper taskDeadlineExpiredException() {
        var badRequestEx = new ExceptionWrapper(new BadRequestException());
        badRequestEx.setErrorMessage(errorProperties.getTaskDeadlineExpired());
        return badRequestEx;
    }

    public ExceptionWrapper invalidAccessTokenException() {
        var authEx = new ExceptionWrapper(new AuthException());
        authEx.setErrorMessage(errorProperties.getInvalidAccessToken());
        return authEx;
    }

    public ExceptionWrapper invalidRefreshTokenException() {
        var authEx = new ExceptionWrapper(new AuthException());
        authEx.setErrorMessage(errorProperties.getInvalidRefreshToken());
        return authEx;
    }

    public ExceptionWrapper invalidWebsocketMessageException() {
        var authEx = new ExceptionWrapper(new BadRequestException());
        authEx.setErrorMessage(errorProperties.getInvalidWebsocketMessage());
        return authEx;
    }

    public ExceptionWrapper securityException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getSecurity());
        return secEx;
    }

    public ExceptionWrapper requestingUserNotCourseMemberException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getRequestingUserNotCourseMember());
        return secEx;
    }

    public ExceptionWrapper targetUserNotCourseMemberException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getTargetUserNotCourseMember());
        return secEx;
    }

    public ExceptionWrapper attachOnlyYourFilesException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getAttachOnlyYourFiles());
        return secEx;
    }

    public ExceptionWrapper fileFailedSaveException() {
        var ex = new ExceptionWrapper(new Exception());
        ex.setErrorMessage(errorProperties.getFileFailedSave());
        return ex;
    }

    public ExceptionWrapper fileFailedRetrieveException() {
        var ex = new ExceptionWrapper(new Exception());
        ex.setErrorMessage(errorProperties.getFileFailedRetrieve());
        return ex;
    }

    public ExceptionWrapper generateCodeFailedException() {
        var ex = new ExceptionWrapper(new Exception());
        ex.setErrorMessage(errorProperties.getGenerateCodeFailed());
        return ex;
    }
}
