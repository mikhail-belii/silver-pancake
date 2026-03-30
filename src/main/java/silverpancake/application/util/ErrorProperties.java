package silverpancake.application.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.errors")
@Data
public class ErrorProperties {
    private String userNotFound;
    private String fileNotFound;
    private String filesNotFound;
    private String courseNotFound;
    private String invalidAccessToken;
    private String invalidRefreshToken;
    private String userWithEmailExists;
    private String incorrectEmailOrPassword;
    private String fileEmpty;
    private String fileFailedSave;
    private String fileFailedRetrieve;
    private String fileSize;
    private String fileExtensionRequired;
    private String fileAllowedExtensions;
    private String generateCodeFailed;
    private String security;
    private String targetUserNotCourseMember;
    private String requestingUserNotCourseMember;
    private String userAlreadyCourseMember;
    private String attachOnlyYourFiles;
    private String fileAlreadyAttached;
    private String notDraftTypeAndDraftTime;
    private String draftTypeAndNotDraftTime;
    private String draftTimeAfterDeadline;
}
