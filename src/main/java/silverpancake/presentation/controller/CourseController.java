package silverpancake.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.model.course.*;
import silverpancake.application.service.CourseService;
import silverpancake.domain.entity.user.UserCourseRole;

import java.util.List;
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
                                            @PathVariable UUID courseId,
                                            @RequestBody CourseEditModel courseEditModel) {
        return Response.success(courseService.editCourse(authModel.getUserId(), courseId, courseEditModel));
    }

    @PostMapping(value = "/join/{joinCode}")
    @Operation(summary = "Join course by code")
    public Response<CourseModel> joinCourseByCode(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                  @PathVariable String joinCode) {
        return Response.success(courseService.joinCourseByCode(authModel.getUserId(), joinCode));
    }

    @PostMapping(value = "/{courseId}/user/{userId}/role/{newRole}")
    @Operation(summary = "Change user role in course")
    public Response<Void> changeUserRoleOnCourse(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                 @PathVariable UUID courseId,
                                                 @PathVariable UUID userId,
                                                 @PathVariable UserCourseRole newRole) {
        courseService.changeUserRoleOnCourse(authModel.getUserId(), courseId, userId, newRole);
        return Response.success();
    }

    @GetMapping(value = "/{courseId}/user/list")
    @Operation(summary = "Get course users")
    public Response<UserCourseListModel> getCourseUsers(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                        @PathVariable UUID courseId) {
        return Response.success(courseService.getCourseUsers(authModel.getUserId(), courseId));
    }

    @GetMapping(value = "/my")
    @Operation(summary = "Get user courses")
    public Response<CourseShortListModel> getUserCourses(@RequestAttribute("authModel") AuthorizationModel authModel) {
        return Response.success(courseService.getUserCourses(authModel.getUserId()));
    }

    @GetMapping(value = "/{courseId}")
    @Operation(summary = "Get concrete course")
    public Response<CourseModel> getConcreteCourse(@RequestAttribute("authModel") AuthorizationModel authModel,
                                                   @PathVariable UUID courseId) {
        return Response.success(courseService.getConcreteCourse(authModel.getUserId(), courseId));
    }
}
