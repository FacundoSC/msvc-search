package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchDomainExceptionTest {
    @Test
    public void searchDomainExceptionShouldCreateInstanceWithMessageTest(){
        String errorMessage = "Domain bussiness rule violation";
        SearchDomainException exception = new SearchDomainException(errorMessage);
        assertThat(exception)
                .isInstanceOf(SearchDomainException.class)
                .hasMessage(errorMessage);
    }

}