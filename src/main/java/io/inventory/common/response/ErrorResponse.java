package io.inventory.common.response;

import io.inventory.common.exception.ErrorCode;

public record ErrorResponse(String code, String description) {
    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode.name(), errorCode.getDescription());
    }
}
