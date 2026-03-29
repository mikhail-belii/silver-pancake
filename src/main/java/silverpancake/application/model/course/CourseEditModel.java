package silverpancake.application.model.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class CourseEditModel {
    @NotBlank
    @Size(min=3, max=128)
    private String name;
    @NotBlank
    @Size(min=3, max=512)
    private String description;
}
