package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import silverpancake.domain.entity.taskanswer.TaskAnswer;

import java.util.UUID;

public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, UUID> {
}
