package org.example.stolikonline1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.example.stolikonline1.models.enums.UserRoles;
import org.example.stolikonline1.repositories.UserRepository;
import org.example.stolikonline1.services.AppUserDetailsService;

@Configuration
public class AppSecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AppSecurityConfiguration.class);

    private final UserRepository userRepository;

    public AppSecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("AppSecurityConfiguration инициализирована");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Статические ресурсы и публичные страницы
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/favicon.ico", "/error").permitAll()
                        .requestMatchers("/pic/**", "/images/**", "/css/**", "/js/**", "/webjars/**").permitAll()

                        // 2. Публичные страницы (доступны всем)
                        .requestMatchers("/", "/home", "/static", "/about", "/contact").permitAll()
                        .requestMatchers("/users/login", "/users/register", "/users/login-error").permitAll()
                        .requestMatchers("/restaurants/all", "/restaurants/details/**").permitAll()
                        .requestMatchers("/bookings/public/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // 3. Страницы, требующие аутентификации
                        .requestMatchers("/users/profile", "/users/settings").authenticated()
                        .requestMatchers("/bookings/my", "/bookings/add", "/bookings/cancel/**").authenticated()

                        // 4. Страницы для модераторов
                        .requestMatchers("/bookings/manage/**", "/restaurants/add", "/restaurants/edit/**", "/restaurants/delete/**")
                        .hasAnyRole(UserRoles.MODERATOR.name(), UserRoles.ADMIN.name())

                        // 5. Страницы только для админов
                        .requestMatchers("/admin/**", "/users/manage/**", "/system/**")
                        .hasRole(UserRoles.ADMIN.name())

                        // 6. Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                        .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                        .defaultSuccessUrl("/", true)
                        .failureForwardUrl("/users/login-error")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400 * 7) // 7 дней
                        .userDetailsService(userDetailsService())
                        .rememberMeParameter("remember-me")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Используем /logout для единообразия
                        .logoutSuccessUrl("/?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/actuator/**", "/api/**", "/h2-console/**")
                );

        log.info("SecurityFilterChain настроен");
        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService(userRepository);
    }
}