package inventory.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ExceptionCode exceptionCode;
    
    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
    
    public CustomException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
    
    public CustomException(ExceptionCode exceptionCode, String message, Throwable cause) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }
}
