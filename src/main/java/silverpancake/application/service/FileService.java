package silverpancake.application.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import silverpancake.application.model.file.FileModel;

import java.util.UUID;

public interface FileService {
    FileModel upload(UUID userId, MultipartFile file);
    ResponseEntity<Resource> getById(UUID fileId);
}
