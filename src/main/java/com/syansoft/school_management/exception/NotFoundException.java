package com.syansoft.school_management.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String errorCode;

    public NotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
