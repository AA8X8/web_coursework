package org.example.stolikonline1.services;

import org.example.stolikonline1.dto.UserProfileDto;
import org.example.stolikonline1.dto.UserRegistrationDto;
import org.example.stolikonline1.dto.UserStatsDto;
import org.example.stolikonline1.models.entities.User;
import java.util.Optional;

public interface UserService {
    void registerUser(UserRegistrationDto userRegistrationDto);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    boolean phoneExists(String phone);

    Optional<User> findByUsername(String username);
    UserProfileDto getUserProfile(String username);
    UserStatsDto getUserStats(String username);
}
