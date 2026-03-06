package com.example.common.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException {
    @Getter
    private final int code;
    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }
}
