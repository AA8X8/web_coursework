package org.example.stolikonline1.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.stolikonline1.dto.UserProfileDto;
import org.example.stolikonline1.dto.UserRegistrationDto;
import org.example.stolikonline1.dto.UserStatsDto;
import org.example.stolikonline1.models.entities.User;
import org.example.stolikonline1.repositories.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("UserServiceImpl инициализирован");
    }

    @Override
    @Transactional
    public void registerUser(UserRegistrationDto userRegistrationDto) {
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean phoneExists(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUsername(user.getUsername());
        profileDto.setFullName(user.getFullName());
        profileDto.setEmail(user.getEmail());
        profileDto.setPhone(user.getPhone());
        profileDto.setAge(user.getAge());

        String avatarUrl = user.getProfilePictureUrl();
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            avatarUrl = "/images/user-avatar.png"; // Стандартный аватар
        }
        profileDto.setProfilePictureUrl(avatarUrl);

        return profileDto;
    }

    @Override
    public UserStatsDto getUserStats(String username) {
        return new UserStatsDto(0, 0, 0, 0);
    }
}
