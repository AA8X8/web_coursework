package org.example.stolikonline1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.stolikonline1.services.RestaurantService;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final RestaurantService restaurantService;

    public HomeController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
        log.info("HomeController инициализирован");
    }

    @GetMapping("/")
    public String homePage(Model model) {
        log.debug("Отображение главной страницы");
        // Получаем 6 ресторанов для главной страницы
        var popularRestaurants = restaurantService.allRestaurants().stream()
                .limit(6)
                .toList();
        model.addAttribute("popularRestaurants", popularRestaurants);
        return "index";
    }
}