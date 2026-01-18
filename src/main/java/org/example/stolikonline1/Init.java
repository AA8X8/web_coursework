package org.example.stolikonline1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.example.stolikonline1.models.entities.Role;
import org.example.stolikonline1.models.entities.User;
import org.example.stolikonline1.models.enums.UserRoles;
import org.example.stolikonline1.repositories.UserRepository;
import org.example.stolikonline1.repositories.UserRoleRepository;

import java.util.List;

@Component
public class Init implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Init.class);

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public Init(UserRepository userRepository,
                UserRoleRepository userRoleRepository,
                PasswordEncoder passwordEncoder,
                @Value("${app.default.password:password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
        log.info("Init компонент инициализирован");
    }

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации начальных данных");
        initRoles();
        initUsers();
        log.info("Инициализация начальных данных завершена");
    }

    private void initRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("Создание базовых ролей...");
            userRoleRepository.saveAll(List.of(
                    new Role(UserRoles.ADMIN),
                    new Role(UserRoles.MODERATOR),
                    new Role(UserRoles.USER)
            ));
            log.info("Роли созданы: ADMIN, MODERATOR, USER");
        } else {
            log.debug("Роли уже существуют, пропуск инициализации");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Создание пользователей по умолчанию...");
            initAdmin();
            initModerator();
            initNormalUser();
            log.info("Пользователи по умолчанию созданы");
        } else {
            log.debug("Пользователи уже существуют, пропуск инициализации");
        }
    }

    private void initAdmin() {
        var adminRole = userRoleRepository
                .findByName(UserRoles.ADMIN)
                .orElseThrow(() -> new RuntimeException("Роль ADMIN не найдена"));

        var adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode(defaultPassword));
        adminUser.setEmail("admin@stolikonline.ru");
        adminUser.setFullName("Администратор Системы");
        adminUser.setPhone("+79990000001");
        adminUser.setAge(35);
        adminUser.setRoles(List.of(adminRole));

        userRepository.save(adminUser);
        log.info("Создан администратор: admin");
    }

    private void initModerator() {
        var moderatorRole = userRoleRepository
                .findByName(UserRoles.MODERATOR)
                .orElseThrow(() -> new RuntimeException("Роль MODERATOR не найдена"));

        var moderatorUser = new User();
        moderatorUser.setUsername("moderator");
        moderatorUser.setPassword(passwordEncoder.encode(defaultPassword));
        moderatorUser.setEmail("moderator@stolikonline.ru");
        moderatorUser.setFullName("Модератор Системы");
        moderatorUser.setPhone("+79990000002");
        moderatorUser.setAge(28);
        moderatorUser.setRoles(List.of(moderatorRole));

        userRepository.save(moderatorUser);
        log.info("Создан модератор: moderator");
    }

    private void initNormalUser() {
        var userRole = userRoleRepository
                .findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        var normalUser = new User();
        normalUser.setUsername("user");
        normalUser.setPassword(passwordEncoder.encode(defaultPassword));
        normalUser.setEmail("user@stolikonline.ru");
        normalUser.setFullName("Тестовый Пользователь");
        normalUser.setPhone("+79990000003");
        normalUser.setAge(25);
        normalUser.setRoles(List.of(userRole));

        userRepository.save(normalUser);
        log.info("Создан обычный пользователь: user");
    }
}