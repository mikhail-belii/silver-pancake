package silverpancake.presentation.handler;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.exception.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import silverpancake.application.exception.ExceptionWrapper;
import silverpancake.application.model.common.Response;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger logger =  LoggerFactory.getLogger(CustomExceptionHandler.class);
    private int getStatusCodeByExceptionClass(Class<? extends Exception> exceptionClass) {
        if(exceptionClass.equals(BadRequestException.class) ||
            exceptionClass.equals(MethodArgumentNotValidException.class)) {
            return 400;
        }

        if(exceptionClass.equals(AuthException.class)) {
            return 401;
        }

        if(exceptionClass.equals(SecurityException.class)) {
            return 403;
        }

        if(exceptionClass.equals(EntityNotFoundException.class)) {
            return 404;
        }

        return 500;
    }

    @ExceptionHandler(ExceptionWrapper.class)
    public ResponseEntity<Response<Dictionary<String, String>>> handleWrapper(
            ExceptionWrapper e) {
        int statusCode = getStatusCodeByExceptionClass(e.getExceptionClass());
        return buildErrorResponse(statusCode, e.getErrorMessage(), "Request failed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Response<Map<String, String>> response =
                Response.error(400, "Validation failed");
        response.setData(errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Dictionary<String, String>>> handleNotKnownException(
            Exception e) {
        logger.error(e.getMessage(), e);
        return buildErrorResponse(500, "Unknown error, please tell us about it", "Unknown error");
    }

    private ResponseEntity<Response<Dictionary<String, String>>> buildErrorResponse(int code,
                                                                                    String errorMessage,
                                                                                    String fallbackMessage) {
        Response<Dictionary<String, String>> response = Response.error(code, errorMessage != null ? errorMessage : fallbackMessage);
        return ResponseEntity.ok(response);
    }
}