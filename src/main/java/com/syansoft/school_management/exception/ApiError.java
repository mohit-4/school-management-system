package com.syansoft.school_management.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String errorCode;
    private String message;
    private Instant timestamp = Instant.now();
}
