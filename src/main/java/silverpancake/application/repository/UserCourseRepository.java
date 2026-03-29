package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import silverpancake.domain.entity.usercourse.UserCourse;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, UUID> {
    @Query("""
            select uc
            from UserCourse uc
            where uc.user.id = :userId and uc.course.id = :courseId
            """)
    Optional<UserCourse> findByUserAndCourse(@Param("userId") UUID userId, @Param("courseId") UUID courseId);
}
