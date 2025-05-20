package com.crewmeister.cmcodingchallenge.exception;

public class BundesBankClientException extends RuntimeException {
    public BundesBankClientException(String message, Throwable cause) {
            super(message, cause);
        }

    public BundesBankClientException(String message) {
            super(message);
        }
}
