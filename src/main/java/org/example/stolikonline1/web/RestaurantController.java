package org.example.stolikonline1.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.stolikonline1.dto.AddRestaurantDto;
import org.example.stolikonline1.dto.ShowRestaurantInfoDto;
import org.example.stolikonline1.services.RestaurantService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
        log.info("RestaurantController инициализирован");
    }

    @GetMapping("/add")
    public String addRestaurant(Model model) {
        log.debug("Отображение формы добавления ресторана");
        if (!model.containsAttribute("restaurantModel")) {
            model.addAttribute("restaurantModel", new AddRestaurantDto());
        }
        return "restaurant-add";
    }

    @PostMapping("/add")
    public String addRestaurant(@Valid @ModelAttribute("restaurantModel") AddRestaurantDto restaurantModel,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        log.debug("Обработка добавления ресторана: {}", restaurantModel.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении ресторана: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("restaurantModel", restaurantModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.restaurantModel",
                    bindingResult);
            return "redirect:/restaurants/add";
        }

        try {
            restaurantService.addRestaurant(restaurantModel);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ресторан '" + restaurantModel.getName() + "' успешно добавлен!");
        } catch (RuntimeException e) {
            log.error("Ошибка при добавлении ресторана: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("restaurantModel", restaurantModel);
            return "redirect:/restaurants/add";
        }

        return "redirect:/restaurants/all";
    }

    @GetMapping("/all")
    public String showAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Integer priceLevel,
            Model model) {

        log.debug("Отображение списка ресторанов: страница={}, размер={}, сортировка={}, поиск={}, кухня={}, цена={}",
                page, size, sortBy, search, cuisine, priceLevel);

        // Добавляем список кухонь
        model.addAttribute("cuisines", restaurantService.getAllCuisines());

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("restaurants", restaurantService.searchRestaurants(search));
            model.addAttribute("search", search);
        } else if (cuisine != null && !cuisine.trim().isEmpty()) {
            model.addAttribute("restaurants", restaurantService.filterByCuisine(cuisine));
            model.addAttribute("selectedCuisine", cuisine);
        } else if (priceLevel != null) {
            model.addAttribute("restaurants", restaurantService.filterByPriceLevel(priceLevel));
            model.addAttribute("selectedPriceLevel", priceLevel);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
            Page<ShowRestaurantInfoDto> restaurantPage = restaurantService.getAllRestaurants(pageable);

            model.addAttribute("restaurants", restaurantPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", restaurantPage.getTotalPages());
            model.addAttribute("totalItems", restaurantPage.getTotalElements());
            model.addAttribute("sortBy", sortBy);
        }

        return "restaurant-all";
    }

    @GetMapping("/details/{name}")
    public String restaurantDetails(@PathVariable("name") String restaurantName, Model model) {
        log.debug("Запрос деталей ресторана: {}", restaurantName);
        try {
            model.addAttribute("restaurant", restaurantService.getRestaurantDetails(restaurantName));

            return "restaurant-details";
        } catch (RuntimeException e) {
            log.error("Ресторан не найден: {}", restaurantName);
            return "redirect:/restaurants/all";
        }
    }

    @GetMapping("/delete/{name}")
    public String deleteRestaurant(@PathVariable("name") String restaurantName,
                                   RedirectAttributes redirectAttributes) {
        log.debug("Запрос на удаление ресторана: {}", restaurantName);
        try {
            restaurantService.deleteRestaurant(restaurantName);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ресторан '" + restaurantName + "' успешно удален!");
        } catch (RuntimeException e) {
            log.error("Ошибка при удалении ресторана: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/restaurants/all";
    }
}