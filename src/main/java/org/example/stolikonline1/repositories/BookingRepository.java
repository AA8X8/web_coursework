package org.example.stolikonline1.repositories;

import org.example.stolikonline1.models.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByUserId(String userId);
    List<Booking> findByRestaurantId(String restaurantId);

    @Query("SELECT b FROM Booking b WHERE b.restaurant.id = :restaurantId AND " +
            "b.bookingDateTime BETWEEN :startTime AND :endTime")
    List<Booking> findBookingsForRestaurantInTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.restaurant.id = :restaurantId AND " +
            "b.bookingDateTime BETWEEN :startTime AND :endTime")
    Integer countBookingsForRestaurantInTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    Optional<Booking> findByIdAndUserId(String id, String userId);
}