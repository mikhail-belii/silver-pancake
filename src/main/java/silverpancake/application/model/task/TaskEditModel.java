package silverpancake.application.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import silverpancake.domain.entity.task.TeamFormationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEditModel {
    @NotBlank
    @Size(min = 4, max = 128)
    private String title;
    @NotBlank
    @Size(min = 4, max = 2048)
    private String text;
    @NotNull
    @Min(value = 1, message = "Максимальная оценка должна находиться в диапазоне от 1 до 100")
    @Max(value = 100, message = "Максимальная оценка должна находиться в диапазоне от 1 до 100")
    private Integer maxScore;
    @NotNull
    @Future(message = "Дата дедлайна должна быть в будущем")
    private LocalDateTime deadlineTime;
    @Future(message = "Дата драфта должна быть в будущем")
    private LocalDateTime draftStartTime;
    @NotNull
    private List<UUID> fileIds;

    @JsonIgnore
    public boolean isDraftStartTimeAfterDeadline() {
        if (getDraftStartTime() == null) {
            return false;
        }
        return getDraftStartTime().isAfter(getDeadlineTime());
    }
}
