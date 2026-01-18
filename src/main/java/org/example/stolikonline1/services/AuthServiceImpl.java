package org.example.stolikonline1.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.stolikonline1.dto.UserRegistrationDto;
import org.example.stolikonline1.models.entities.User;
import org.example.stolikonline1.models.enums.UserRoles;
import org.example.stolikonline1.repositories.UserRepository;
import org.example.stolikonline1.repositories.UserRoleRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(UserRegistrationDto registrationDTO) {
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают!");
        }

        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email уже используется!");
        }

        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Имя пользователя уже занято!");
        }

        if (userRepository.findByPhone(registrationDTO.getPhone()).isPresent()) {
            throw new RuntimeException("Номер телефона уже используется!");
        }

        // Проверка возраста
        if (registrationDTO.getAge() < 14 || registrationDTO.getAge() > 90) {
            throw new RuntimeException("Возраст должен быть от 14 до 90 лет!");
        }

        var userRole = userRoleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена!"));

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setFullName(registrationDTO.getFullName());
        user.setPhone(registrationDTO.getPhone());
        user.setAge(registrationDTO.getAge());
        user.setRoles(List.of(userRole));

        userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " не найден!"));
    }
}
