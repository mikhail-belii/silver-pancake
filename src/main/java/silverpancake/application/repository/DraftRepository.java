package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import silverpancake.domain.entity.draft.Draft;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DraftRepository extends JpaRepository<Draft, UUID> {
    Optional<Draft> findByTaskId(UUID taskId);

    @Query(value = """
            SELECT d.*
              FROM draft d
                   JOIN task t
                   ON t.id = d.task_id
             WHERE t.course_id = :courseId
               AND d.is_ended = false;
            """, nativeQuery = true)
    List<Draft> findNotEndedByCourse(UUID courseId);

    @Query(value = """
            SELECT EXISTS(SELECT * 
                            FROM draft d
                                 JOIN task t
                                 ON t.id = d.task_id
                                 JOIN user_course uc
                                 ON t.course_id = uc.course_id
                           WHERE d.id = :draftId
                             AND uc.user_id = :userId)
            """, nativeQuery = true)
    Boolean isDraftCourseContainsUser(UUID draftId, UUID userId);

}
