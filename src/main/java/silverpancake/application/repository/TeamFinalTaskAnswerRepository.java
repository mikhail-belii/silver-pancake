package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import silverpancake.domain.entity.teamfinaltaskanswer.TeamFinalTaskAnswer;

import java.util.UUID;

public interface TeamFinalTaskAnswerRepository extends JpaRepository<TeamFinalTaskAnswer, UUID> {
}
