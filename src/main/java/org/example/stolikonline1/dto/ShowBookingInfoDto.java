package org.example.stolikonline1.dto;

import java.time.LocalDateTime;

public class ShowBookingInfoDto {
    private String id;
    private String restaurantName;
    private String restaurantId; // Изменено на String!
    private LocalDateTime bookingDateTime;
    private Integer numberOfGuests;
    private String specialRequests;
    private String status;

    // Конструктор по умолчанию
    public ShowBookingInfoDto() {
    }

    // Конструктор для JPA запроса
    public ShowBookingInfoDto(String id, String restaurantName, String restaurantId,
                              LocalDateTime bookingDateTime, Integer numberOfGuests,
                              String specialRequests, String status) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.bookingDateTime = bookingDateTime;
        this.numberOfGuests = numberOfGuests;
        this.specialRequests = specialRequests;
        this.status = status;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantId() { // Изменено на String
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) { // Изменено на String
        this.restaurantId = restaurantId;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
