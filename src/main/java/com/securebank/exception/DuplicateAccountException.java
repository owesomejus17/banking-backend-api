package com.securebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to register an email that already exists,
 * or when a generated account number collides (extremely rare).
 * Mapped to HTTP 409 Conflict by GlobalExceptionHandler.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String message) {
        super(message);
    }
}
