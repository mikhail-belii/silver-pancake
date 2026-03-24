package silverpancake.application.model.common;

import lombok.Data;

@Data
public class Response<T> {
    private int statusCode;
    private String errorMessage;
    private T data;

    private Response(int statusCode, String errorMessage, T data) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static Response<Void> success() {
        return new Response<Void>(200, null, null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, null, data);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }
}