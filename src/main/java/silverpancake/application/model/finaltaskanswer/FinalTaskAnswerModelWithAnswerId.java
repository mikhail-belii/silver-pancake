package silverpancake.application.model.finaltaskanswer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain=true)
public class FinalTaskAnswerModelWithAnswerId {

    private UUID newTaskAnswerId;

    private FinalTaskAnswerModel finalTaskAnswer;
}
