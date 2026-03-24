package silverpancake.application.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionWrapper extends RuntimeException {
    private final Class<? extends Exception> exceptionClass;
    private String errorMessage;

    public ExceptionWrapper(Exception originalException) {
        this.exceptionClass = originalException.getClass();
    }
}
