package org.example.stolikonline1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.example.stolikonline1.dto.AddRestaurantDto;
import org.example.stolikonline1.dto.ShowRestaurantInfoDto;
import org.example.stolikonline1.dto.ShowDetailedRestaurantInfoDto;

import java.util.List;

public interface RestaurantService {
    ShowRestaurantInfoDto addRestaurant(AddRestaurantDto restaurantDto);
    void deleteRestaurant(String name);
    ShowDetailedRestaurantInfoDto getRestaurantDetails(String name);
    Page<ShowRestaurantInfoDto> getAllRestaurants(Pageable pageable);
    List<ShowRestaurantInfoDto> searchRestaurants(String searchTerm);
    List<ShowRestaurantInfoDto> filterByCuisine(String cuisine);
    List<ShowRestaurantInfoDto> filterByPriceLevel(Integer priceLevel);
    List<ShowRestaurantInfoDto> filterByRatingGreaterThan(Double minRating);
    List<String> getAllCuisines();
    void updateRestaurantRating(String restaurantId);
    List<ShowRestaurantInfoDto> findByTown(String town);
    List<ShowRestaurantInfoDto> findByBudgetGreaterThan(Double minBudget);
    List<ShowRestaurantInfoDto> allRestaurants();
}