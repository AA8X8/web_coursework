package org.example.stolikonline1.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.stolikonline1.dto.UserProfileDto;
import org.example.stolikonline1.dto.UserStatsDto;
import org.example.stolikonline1.services.UserService;
import org.example.stolikonline1.services.BookingService;
import org.example.stolikonline1.dto.ShowBookingInfoDto;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    public UserController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();

        try {
            // 1. Получаем профиль пользователя
            UserProfileDto userProfile = userService.getUserProfile(username);
            model.addAttribute("user", userProfile);

            // 2. Получаем статистику бронирований
            UserStatsDto stats = calculateUserStats(username);
            model.addAttribute("stats", stats);

        } catch (Exception e) {
            // Если произошла ошибка, создаем заглушку
            UserProfileDto userProfile = new UserProfileDto();
            userProfile.setUsername(username);
            userProfile.setFullName("Пользователь");
            userProfile.setEmail("email@example.com");
            userProfile.setPhone("+7 (999) 000-00-00");
            userProfile.setAge(25);

            model.addAttribute("user", userProfile);
            model.addAttribute("stats", new UserStatsDto(0, 0, 0, 0));
        }

        return "profile";
    }

    private UserStatsDto calculateUserStats(String username) {
        try {
            List<ShowBookingInfoDto> bookings = bookingService.getUserBookings(username);

            int total = bookings.size();
            int active = (int) bookings.stream()
                    .filter(b -> "PENDING".equals(b.getStatus()) || "CONFIRMED".equals(b.getStatus()))
                    .count();
            int completed = (int) bookings.stream()
                    .filter(b -> "COMPLETED".equals(b.getStatus()))
                    .count();
            int cancelled = (int) bookings.stream()
                    .filter(b -> "CANCELLED".equals(b.getStatus()))
                    .count();

            return new UserStatsDto(total, active, completed, cancelled);
        } catch (Exception e) {
            return new UserStatsDto(0, 0, 0, 0);
        }
    }
}