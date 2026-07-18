package com.hms.doctor_service.exception;

public class InvalidSlotException extends RuntimeException {
    public InvalidSlotException(String message) {
        super(message);
    }
}
