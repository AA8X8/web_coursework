package org.example.stolikonline1.dto;

import jakarta.validation.constraints.*;
import org.example.stolikonline1.utils.validation.FutureWithinOneYear;

import java.time.LocalDateTime;

public class AddBookingDto {

    private String restaurantName;
    private LocalDateTime bookingDateTime;
    private Integer numberOfGuests;
    private String specialRequests;

    @NotEmpty(message = "Выберите ресторан!")
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    @NotNull(message = "Дата и время бронирования не должны быть пустыми!")
    @Future(message = "Дата бронирования должна быть в будущем!")
    @FutureWithinOneYear(message = "Бронирование возможно максимум на год вперед!")
    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public void setBookingDateTime(LocalDateTime bookingDateTime) { this.bookingDateTime = bookingDateTime; }

    @NotNull(message = "Количество гостей не должно быть пустым!")
    @Min(value = 1, message = "Минимальное количество гостей - 1!")
    @Max(value = 20, message = "Максимальное количество гостей - 20!")
    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    @Size(max = 500, message = "Особые пожелания не должны превышать 500 символов!")
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}