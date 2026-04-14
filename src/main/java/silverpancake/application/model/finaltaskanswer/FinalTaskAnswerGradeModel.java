package silverpancake.application.model.finaltaskanswer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalTaskAnswerGradeModel {
    @NotNull
    @Min(value = 1, message = "Оценка должна быть не меньше 1")
    private Integer score;
}
