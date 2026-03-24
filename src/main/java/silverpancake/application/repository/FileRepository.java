package silverpancake.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import silverpancake.domain.entity.file.File;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
}
