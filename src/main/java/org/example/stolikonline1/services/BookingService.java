package org.example.stolikonline1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.example.stolikonline1.dto.AddBookingDto;
import org.example.stolikonline1.dto.ShowBookingInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    ShowBookingInfoDto addBooking(AddBookingDto bookingDto, String username);
    void cancelBooking(String bookingId, String username);
    void confirmBooking(String bookingId);
    List<ShowBookingInfoDto> getUserBookings(String username);
    List<ShowBookingInfoDto> getRestaurantBookings(String restaurantName);
    boolean checkAvailability(String restaurantId, LocalDateTime dateTime, Integer numberOfGuests);
    Page<ShowBookingInfoDto> getAllBookings(Pageable pageable);
}