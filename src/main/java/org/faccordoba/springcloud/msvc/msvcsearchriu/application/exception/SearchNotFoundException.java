package org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception;

public class SearchNotFoundException extends RuntimeException {
    public SearchNotFoundException(String message) {
        super(message);
    }
}
