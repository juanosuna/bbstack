package com.brownbag.core.util.assertion;


public class ProgrammingException extends AssertionException {
    public ProgrammingException() {
    }

    public ProgrammingException(String message) {
        super(message);
    }

    public ProgrammingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgrammingException(Throwable cause) {
        super(cause);
    }
}
