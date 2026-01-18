package org.example.stolikonline1.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniquePhoneValidator.class)
@Documented
public @interface UniquePhone {
    String message() default "Номер телефона уже используется!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}