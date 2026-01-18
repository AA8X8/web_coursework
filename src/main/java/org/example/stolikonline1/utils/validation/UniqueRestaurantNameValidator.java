package org.example.stolikonline1.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.example.stolikonline1.repositories.RestaurantRepository;

@Component
public class UniqueRestaurantNameValidator implements ConstraintValidator<UniqueRestaurantName, String> {

    private final RestaurantRepository restaurantRepository;

    public UniqueRestaurantNameValidator(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        // Проверяем, существует ли ресторан с таким именем
        return restaurantRepository.findByName(value).isEmpty();
    }
}