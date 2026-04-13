package silverpancake.application.serviceimpl.taskanswer.strategy;

import org.springframework.stereotype.Service;
import silverpancake.application.model.finaltaskanswer.FinalTaskAnswerModel;

@Service
public interface TaskAttachmentStrategy {

    FinalTaskAnswerModel process();
    TaskAnswerFinalizationType getType();
}
