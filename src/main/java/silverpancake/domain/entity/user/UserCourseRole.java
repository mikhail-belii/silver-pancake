package silverpancake.domain.entity.user;

import lombok.Getter;

@Getter
public enum UserCourseRole {
    STUDENT(0),
    TEACHER(1),
    HEAD_TEACHER(2);

    private final int importance;

    UserCourseRole(int importance) {
        this.importance = importance;
    }

    public static boolean isUserHigherThan(UserCourseRole role1, UserCourseRole role2) {
        return role1.importance > role2.importance;
    }
}
