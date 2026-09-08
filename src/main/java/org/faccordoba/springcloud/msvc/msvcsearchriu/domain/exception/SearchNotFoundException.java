package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception;

public class SearchNotFoundException extends RuntimeException {
    public SearchNotFoundException(String searchId) {
        super(String.format("Search with id %s not found", searchId));
    }
}
