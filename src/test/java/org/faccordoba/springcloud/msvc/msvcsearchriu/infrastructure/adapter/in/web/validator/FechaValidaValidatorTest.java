package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FechaValidaValidatorTest {

    private FechaValidaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FechaValidaValidator();
    }

    @AfterEach
    void tearDown() {
        validator = null;
    }

    @Test
    public void isValidShouldReturnTrueWhenDateFormatIsValidTest() {
        boolean result = validator.isValid("15/08/2026", null);
        assertThat(result).isTrue();
    }

    @Test
    public void isValidShouldReturnFalseWhenDateFormatIsInvalidTest() {
        boolean result = validator.isValid("2026-08-15", null);
        assertThat(result).isFalse();
    }

    @Test
    public void isValidShouldReturnFalseWhenDayIsOutOfRangeTest() {
        boolean result = validator.isValid("32/08/2026", null);
        assertThat(result).isFalse();
    }

    @Test
    public void isValidShouldReturnFalseWhenMonthIsOutOfRangeTest() {
        boolean result = validator.isValid("15/13/2026", null);
        assertThat(result).isFalse();
    }

    @Test
    public void isValidShouldReturnFalseWhenStringIsGarbageTest() {
        boolean result = validator.isValid("not-a-date", null);
        assertThat(result).isFalse();
    }
}
