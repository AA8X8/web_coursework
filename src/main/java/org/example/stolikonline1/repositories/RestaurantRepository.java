package org.example.stolikonline1.repositories;

import org.example.stolikonline1.models.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> { // Изменено на String!

    Optional<Restaurant> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);

    @Query("SELECT DISTINCT r.cuisine FROM Restaurant r")
    List<String> findAllDistinctCuisines();

    @Query("SELECT r FROM Restaurant r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Restaurant> searchByNameCuisineOrAddress(@Param("searchTerm") String searchTerm);

    List<Restaurant> findByCuisine(String cuisine);

    List<Restaurant> findByPriceLevel(Integer priceLevel);

    // Дополнительные методы для фильтрации по рейтингу
    @Query("SELECT r FROM Restaurant r JOIN Review rev ON rev.restaurant.id = r.id " +
            "GROUP BY r.id HAVING AVG(rev.rating) >= :minRating")
    List<Restaurant> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);

    // Добавьте метод findById для String ID
    Optional<Restaurant> findById(String id);
}