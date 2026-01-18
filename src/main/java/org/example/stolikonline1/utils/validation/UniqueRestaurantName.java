package org.example.stolikonline1.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueRestaurantNameValidator.class)
@Documented
public @interface UniqueRestaurantName {
    String message() default "Ресторан с таким названием уже существует!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}