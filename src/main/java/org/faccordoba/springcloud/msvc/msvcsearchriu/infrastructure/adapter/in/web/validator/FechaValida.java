package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FechaValidaValidator.class)
@Documented
public @interface FechaValida {
    String message() default "Formato de fecha inválido";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
