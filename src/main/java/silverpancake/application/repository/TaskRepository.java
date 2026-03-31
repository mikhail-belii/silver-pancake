package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import silverpancake.domain.entity.course.Course;
import silverpancake.domain.entity.task.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> getTasksByCourse(Course course);
}
