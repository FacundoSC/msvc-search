package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.validator;

import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FechaValidaValidator implements ConstraintValidator<FechaValida, String> {
    private static final String FORMATO = "dd/MM/yyyy";

    @Override
    public boolean isValid(String fecha, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO);
            LocalDate.parse(fecha, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}