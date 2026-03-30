package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.course.CourseCreateModel;
import silverpancake.application.model.course.CourseEditModel;
import silverpancake.application.model.course.CourseModel;
import silverpancake.application.service.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("api/course")
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create course")
    public Response<CourseModel> createCourse(@RequestAttribute("authModel") AuthorizationModel authModel,
                                              @Valid @RequestBody CourseCreateModel courseCreateModel) {
        return Response.success(courseService.createCourse(authModel.getUserId(), courseCreateModel));
    }

    @PatchMapping("/{courseId}")
    @Operation(summary = "Edit course")
    public Response<CourseModel> editCourse(@RequestAttribute("authModel") AuthorizationModel authModel,
                                          @PathVariable("courseId") UUID courseId,
                                          @RequestBody CourseEditModel courseEditModel) {
        return Response.success(courseService.editCourse(authModel.getUserId(), courseId, courseEditModel));
    }

    @PostMapping(value = "/join/{joinCode}")
    @Operation(summary = "Join course by code")
    public Response<CourseModel> joinCourseByCode(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                  @PathVariable String joinCode) {
        return Response.success(courseService.joinCourseByCode(authModel.getUserId(), joinCode));
    }
}
