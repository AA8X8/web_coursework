package org.example.stolikonline1.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureWithinOneYearValidator.class)
@Documented
public @interface FutureWithinOneYear {
    String message() default "Бронирование возможно максимум на год вперед!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}