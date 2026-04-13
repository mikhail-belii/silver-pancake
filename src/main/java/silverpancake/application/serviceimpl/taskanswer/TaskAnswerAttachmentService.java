package silverpancake.application.serviceimpl.taskanswer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;
import silverpancake.application.serviceimpl.taskanswer.strategy.TaskAnswerFinalizationType;
import silverpancake.application.serviceimpl.taskanswer.strategy.TaskAttachmentStrategy;
import silverpancake.domain.entity.task.Task;
import silverpancake.domain.entity.taskanswer.TaskAnswer;
import silverpancake.domain.entity.team.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAnswerAttachmentService {

    private final List<TaskAttachmentStrategy> processors;
    private Map<TaskAnswerFinalizationType, TaskAttachmentStrategy> finalTaskAnswerModelMap = new HashMap<>();

    @PostConstruct
    private void init() {
        finalTaskAnswerModelMap = processors.stream()
                .collect(Collectors.toUnmodifiableMap(TaskAttachmentStrategy::getType, Function.identity()));
    }

    public FinalTaskAnswerModel attachAnswer(Team team, Task task, TaskAnswer taskAnswer) {

        var processor = finalTaskAnswerModelMap.get(TaskAnswerFinalizationType.FIRST_ATTACHMENT);
//        var processor = finalTaskAnswerModelMap.get(task.getTaskAnswerFinalizationType());

        return processor.process(team, task, taskAnswer);
    }
}
