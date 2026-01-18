package org.example.stolikonline1.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class FutureWithinOneYearValidator implements ConstraintValidator<FutureWithinOneYear, LocalDateTime> {

    @Override
    public void initialize(FutureWithinOneYear constraintAnnotation) {
        // Инициализация при необходимости
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Пустое значение проверяется другой аннотацией @NotNull
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxDate = now.plusYears(1);

        boolean isValid = value.isAfter(now) && (value.isBefore(maxDate) || value.isEqual(maxDate));

        if (!isValid) {
            // Кастомное сообщение об ошибке
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Дата бронирования должна быть между " +
                                    now.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                                    " и " + maxDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                    .addConstraintViolation();
        }

        return isValid;
    }
}