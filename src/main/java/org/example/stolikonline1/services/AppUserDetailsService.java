package org.example.stolikonline1.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.example.stolikonline1.repositories.UserRepository;

import java.util.stream.Collectors;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Шаг 1: Пробуем найти по username
        var userOptional = userRepository.findByUsername(input);

        // Шаг 2: Если не нашли по username, пробуем по email
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(input);
        }

        // Шаг 3: Если и так не нашли - ошибка
        var user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Пользователь '" + input + "' не найден!")
        );

        // Шаг 4: Возвращаем UserDetails
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName().name()))
                        .collect(Collectors.toList())
        );
    }
}