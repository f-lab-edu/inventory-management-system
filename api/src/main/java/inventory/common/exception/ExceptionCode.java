package inventory.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // 입력 검증 관련
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력 검증에 실패했습니다"),
    
    // HTTP 메서드 관련
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다"),
    
    // 리소스 관련
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),
    
    // 권한 관련
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    
    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    
    // 데이터 관련
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다"),
    DUPLICATE_DATA(HttpStatus.CONFLICT, "중복된 데이터입니다"),
    
    // 비즈니스 로직 관련
    INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 상태입니다"),
    OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "작업에 실패했습니다"),
    
    // 입고 관련
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 상태 전환입니다"),
    INBOUND_COMPLETED(HttpStatus.BAD_REQUEST, "입고 완료된 건은 수정할 수 없습니다"),
    INBOUND_NOT_DELETABLE(HttpStatus.BAD_REQUEST, "입고 완료된 건은 삭제할 수 없습니다"),
    
    // 출고 관련
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다"),
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "창고에 해당 상품의 재고가 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
