package com.hms.billing_service.exception;

public class BillConflictException extends RuntimeException {
    public BillConflictException(String message) {
        super(message);
    }
}
