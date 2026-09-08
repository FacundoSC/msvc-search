package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SearchNotFoundExceptionTest {

    @Test
    void searchNotFoundExceptionShouldCreateInstanceWithFormattedMessageTest(){
        String searchId = "abc1234";
        SearchNotFoundException searchNotFoundException = new SearchNotFoundException(searchId);
        assertThat(searchNotFoundException)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(searchId);
    }

}