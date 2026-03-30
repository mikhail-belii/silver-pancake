package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.file.FileModel;
import silverpancake.application.service.FileService;
import silverpancake.application.util.ExceptionUtility;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/file")
@AllArgsConstructor
public class FileController {
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "docx", "png", "pdf", "jpg");

    private final FileService fileService;
    private final ExceptionUtility exceptionUtility;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file")
    public Response<FileModel> upload(@RequestParam("file") MultipartFile file,
                                      @RequestAttribute("authModel") AuthorizationModel authModel) {
        validateSize(file);
        validateExtension(file);

        return Response.success(fileService.upload(authModel.getUserId(), file));
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Get file by id")
    public ResponseEntity<Resource> getById(@PathVariable UUID fileId) {
        return fileService.getById(fileId);
    }

    private void validateSize(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw exceptionUtility.fileEmptyException();
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw exceptionUtility.fileSizeException();
        }
    }

    private void validateExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank() || !fileName.contains(".")) {
            throw exceptionUtility.fileExtensionRequiredException();
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw exceptionUtility.fileAllowedExtensionsException();
        }
    }
}
