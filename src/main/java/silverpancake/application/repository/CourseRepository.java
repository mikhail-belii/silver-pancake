package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import silverpancake.domain.entity.course.Course;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByJoinCode(String joinCode);
    Optional<Course> findByJoinCode(String joinCode);
}
