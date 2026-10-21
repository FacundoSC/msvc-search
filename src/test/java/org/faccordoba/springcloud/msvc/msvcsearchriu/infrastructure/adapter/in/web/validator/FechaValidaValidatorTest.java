package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class FechaValidaValidatorTest {

    private final FechaValidaValidator validator = new FechaValidaValidator();

    @ParameterizedTest
    @CsvSource({
            "15/08/2026, true",
            "2026-08-15, false",
            "32/08/2026, false",
            "15/13/2026, false",
            "not-a-date, false"
    })
    void isValidShouldMatchExpectedResult(String date, boolean expected) {
        assertThat(validator.isValid(date, null)).isEqualTo(expected);
    }
}
