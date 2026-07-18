package com.hms.triage_service.exception;

public class TriageException extends RuntimeException {
    public TriageException(String message) {
        super(message);
    }

    public TriageException(String message, Throwable cause) {
        super(message, cause);
    }
}
