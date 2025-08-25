package inventory.common.api;

import inventory.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

    private HttpStatus status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "성공하였습니다.", data);
    }

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, T data) {
        return new ApiResponse<>(httpStatus, "성공하였습니다.", data);
    }

    public static <T> ApiResponse<T> error(ExceptionCode exceptionCode) {
        return new ApiResponse<>(exceptionCode.getHttpStatus(), exceptionCode.getMessage(), null);
    }
}
