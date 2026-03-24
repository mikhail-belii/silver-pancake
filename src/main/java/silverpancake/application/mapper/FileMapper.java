package silverpancake.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import silverpancake.application.model.file.FileModel;
import silverpancake.domain.entity.file.File;

@Mapper(componentModel = "spring")
public interface FileMapper {
    @Mapping(source = "originalName", target = "fileName")
    FileModel toModel(File file);
}
