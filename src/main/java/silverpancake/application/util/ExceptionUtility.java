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

    public ExceptionWrapper courseNotFoundException() {
        var notFoundEx = new ExceptionWrapper(new EntityNotFoundException());
        notFoundEx.setErrorMessage(errorProperties.getCourseNotFound());
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

    public ExceptionWrapper securityException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getSecurity());
        return secEx;
    }

    public ExceptionWrapper userNotCourseMemberException() {
        var secEx = new ExceptionWrapper(new SecurityException());
        secEx.setErrorMessage(errorProperties.getUserNotCourseMember());
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
