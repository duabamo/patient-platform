package com.healthcare.reporting.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s avec id %s introuvable", resource, id));
    }
}
