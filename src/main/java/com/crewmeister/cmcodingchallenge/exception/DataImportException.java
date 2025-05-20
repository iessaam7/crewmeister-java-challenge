package com.crewmeister.cmcodingchallenge.exception;

public class DataImportException extends RuntimeException {
    public DataImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataImportException(String message) {
        super(message);
    }
}
