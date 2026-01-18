package org.example.stolikonline1.services;

import org.example.stolikonline1.dto.UserRegistrationDto;
import org.example.stolikonline1.models.entities.User;

public interface AuthService {
    void register(UserRegistrationDto registrationDTO);
    User getUser(String username);
}