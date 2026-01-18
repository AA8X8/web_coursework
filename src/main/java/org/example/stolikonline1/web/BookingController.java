package org.example.stolikonline1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.stolikonline1.dto.AddBookingDto;
import org.example.stolikonline1.services.BookingService;
import org.example.stolikonline1.services.RestaurantService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final RestaurantService restaurantService;

    public BookingController(BookingService bookingService, RestaurantService restaurantService) {
        this.bookingService = bookingService;
        this.restaurantService = restaurantService;
        log.info("BookingController инициализирован");
    }

    @GetMapping("/add")
    public String showBookingForm(Model model) {
        log.debug("Отображение формы бронирования");

        if (!model.containsAttribute("bookingModel")) {
            model.addAttribute("bookingModel", new AddBookingDto());
        }

        model.addAttribute("availableRestaurants", restaurantService.allRestaurants());
        return "booking-add";
    }

    @PostMapping("/add")
    public String addBooking(@Valid @ModelAttribute("bookingModel") AddBookingDto bookingModel,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.debug("Обработка бронирования для пользователя: {}", userDetails.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при бронировании: {}", bindingResult.getAllErrors());
            model.addAttribute("availableRestaurants", restaurantService.allRestaurants());
            return "booking-add";
        }

        try {
            bookingService.addBooking(bookingModel, userDetails.getUsername());
            log.info("Бронирование успешно создано для пользователя: {}", userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Бронирование успешно создано!");
            return "redirect:/bookings/my";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании бронирования: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("availableRestaurants", restaurantService.allRestaurants());
            return "booking-add";
        }
    }

    @GetMapping("/my")
    public String showMyBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.debug("Отображение бронирований пользователя: {}", userDetails.getUsername());
        model.addAttribute("bookings", bookingService.getUserBookings(userDetails.getUsername()));
        return "booking-my";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") String bookingId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        log.debug("Отмена бронирования {} пользователем {}", bookingId, userDetails.getUsername());
        try {
            bookingService.cancelBooking(bookingId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Бронирование успешно отменено!");
        } catch (RuntimeException e) {
            log.error("Ошибка при отмене бронирования: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings/my";
    }
}