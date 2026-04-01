package silverpancake.presentation.websocket.model.draft;

import lombok.Data;
import silverpancake.application.model.user.UserShortModel;

import java.util.List;

@Data
public class TimeToChooseStudentModel {

    private List<UserShortModel> availableStudentsToChoose;

}
