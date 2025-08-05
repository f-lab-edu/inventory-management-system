package io.inventory.common.response;

import io.inventory.common.exception.ErrorCode;

public record ApiResponse<T>(String status, T data) {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    public static <T> ApiResponse<T> success(final T data) {
        return new ApiResponse<>(SUCCESS, data);
    }

    public static ApiResponse<ErrorResponse> error(final ErrorCode errorCode) {
        return new ApiResponse<>(ERROR, new ErrorResponse(errorCode));
    }
}
