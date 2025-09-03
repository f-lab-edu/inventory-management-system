package inventory.common.exception;

import inventory.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("커스텀 예외 발생: 예외코드={}, 메시지={}", e.getExceptionCode(), e.getMessage());
        return ResponseEntity
                .status(e.getExceptionCode().getHttpStatus())
                .body(ApiResponse.error(e.getExceptionCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("입력 검증 실패: {}", e.getMessage());
        return ResponseEntity
                .status(ExceptionCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.VALIDATION_FAILED));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.error("바인딩 예외 발생: {}", e.getMessage());
        return ResponseEntity
                .status(ExceptionCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.VALIDATION_FAILED));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("타입 불일치 예외 발생: 파라미터명={}, 값={}, 예상타입={}",
                e.getName(), e.getValue(), e.getRequiredType());
        return ResponseEntity
                .status(ExceptionCode.INVALID_INPUT.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.INVALID_INPUT));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HTTP 메시지 읽기 실패: {}", e.getMessage());
        return ResponseEntity
                .status(ExceptionCode.INVALID_INPUT.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.INVALID_INPUT));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("필수 파라미터 누락: 파라미터명={}, 타입={}", e.getParameterName(), e.getParameterType());
        return ResponseEntity
                .status(ExceptionCode.INVALID_INPUT.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.INVALID_INPUT));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("지원하지 않는 HTTP 메서드: 요청 메서드={}, 지원 메서드={}", e.getMethod(), e.getSupportedHttpMethods());
        return ResponseEntity
                .status(ExceptionCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("핸들러를 찾을 수 없음: HTTP 메서드={}, URL={}", e.getHttpMethod(), e.getRequestURL());
        return ResponseEntity
                .status(ExceptionCode.RESOURCE_NOT_FOUND.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("접근 권한 없음: {}", e.getMessage());
        return ResponseEntity
                .status(ExceptionCode.ACCESS_DENIED.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.ACCESS_DENIED));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상하지 못한 예외 발생: 예외 타입={}, 메시지={}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.error(ExceptionCode.INTERNAL_SERVER_ERROR));
    }
}
