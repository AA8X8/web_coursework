package org.example.stolikonline1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.stolikonline1.dto.UserProfileDto;
import org.example.stolikonline1.dto.UserRegistrationDto;
import org.example.stolikonline1.services.AuthService;
import org.example.stolikonline1.services.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService; // Добавляем UserService

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
        log.info("AuthController инициализирован");
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto initForm() {
        return new UserRegistrationDto();
    }

    @GetMapping("/register")
    public String register() {
        log.debug("Отображение страницы регистрации");
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegistrationDto userRegistrationDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        log.debug("Обработка регистрации пользователя: {}", userRegistrationDto.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при регистрации: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto",
                    bindingResult);
            return "redirect:/users/register";
        }

        try {
            this.authService.register(userRegistrationDto);
            log.info("Пользователь успешно зарегистрирован: {}", userRegistrationDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Войдите в систему.");
        } catch (RuntimeException e) {
            log.error("Ошибка при регистрации: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/register";
        }

        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String login() {
        log.debug("Отображение страницы входа");
        return "login";
    }

    @PostMapping("/login-error")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
            RedirectAttributes redirectAttributes) {

        log.warn("Неудачная попытка входа для пользователя: {}", username);
        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("badCredentials", true);
        redirectAttributes.addFlashAttribute("errorMessage", "Неверное имя пользователя или пароль!");

        return "redirect:/users/login";
    }


    // Вспомогательный метод для преобразования ролей
    private String getRoleDisplayName(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        // Преобразуем роли в читаемый формат
        if (roles.contains("ROLE_ADMIN")) {
            return "Администратор";
        } else if (roles.contains("ROLE_MODERATOR")) {
            return "Модератор";
        } else if (roles.contains("ROLE_USER")) {
            return "Пользователь";
        } else {
            return roles.stream().collect(Collectors.joining(", "));
        }
    }

    @GetMapping("/logout-success")
    public String logoutSuccess() {
        log.debug("Пользователь успешно вышел из системы");
        return "redirect:/";
    }
}