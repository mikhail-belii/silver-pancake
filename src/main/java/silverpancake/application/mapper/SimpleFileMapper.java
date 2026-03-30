package silverpancake.application.mapper;

import lombok.experimental.UtilityClass;
import silverpancake.application.model.file.FileModel;
import silverpancake.domain.entity.file.File;

@UtilityClass
public class SimpleFileMapper {
    public FileModel toModel(File file) {
        return new FileModel()
                .setId(file.getId())
                .setFileName(file.getOriginalName());
    }
}
