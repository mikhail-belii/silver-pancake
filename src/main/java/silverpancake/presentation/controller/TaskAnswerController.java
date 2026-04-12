package silverpancake.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import silverpancake.application.serviceimpl.taskanswer.TaskAnswerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/task-answer")
public class TaskAnswerController {

    private final TaskAnswerService taskAnswerService;
}
