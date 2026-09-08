package org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchNotFoundExceptionTest {

    @Test
    public void searchNotFoundExceptionShouldCreateInstanceWithMessageTest() {
        String message = "Search session not found for id: test-id";
        SearchNotFoundException exception = new SearchNotFoundException(message);

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);
    }
}
