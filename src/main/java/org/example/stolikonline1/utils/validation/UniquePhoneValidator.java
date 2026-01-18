package org.example.stolikonline1.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.example.stolikonline1.repositories.UserRepository;

@Component
public class UniquePhoneValidator implements ConstraintValidator<UniquePhone, String> {
    private final UserRepository userRepository;

    public UniquePhoneValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return userRepository.findByPhone(value).isEmpty();
    }
}