package com.proyecto.invoicepipeline.exception;

public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException(String message) {
        super(message);
    }
}