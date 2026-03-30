package silverpancake.application.model.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseShortListModel {
    private List<CourseShortModel> courseShortList;
}
