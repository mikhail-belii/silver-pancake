package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import silverpancake.domain.entity.taskanswer.TaskAnswer;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, UUID> {
    List<TaskAnswer> findAllByTaskIdAndUserIdOrderByUploadedAtDesc(UUID taskId, UUID userId);

    List<TaskAnswer> findAllByTaskIdAndVotedUsersIdOrderByUploadedAtDesc(UUID taskId, UUID votedUserId);

    List<TaskAnswer> findAllByTaskIdAndUserIdInOrderByUploadedAtDesc(UUID taskId, Collection<UUID> userIds);
}
