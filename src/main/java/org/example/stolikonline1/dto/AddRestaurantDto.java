package org.example.stolikonline1.dto;

import jakarta.validation.constraints.*;
import org.example.stolikonline1.utils.validation.UniqueRestaurantName;

public class AddRestaurantDto {

    @UniqueRestaurantName
    private String name;

    private String address;
    private String description;
    private String cuisine;
    private Double rating;
    private String photoUrl;
    private String phone;
    private String email;
    private Integer priceLevel;
    private String openingTime;
    private String closingTime;
    private Integer maxCapacity;

    @NotEmpty(message = "Название ресторана не должно быть пустым!")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов!")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @NotEmpty(message = "Адрес не должен быть пустым!")
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @NotEmpty(message = "Описание не должно быть пустым!")
    @Size(min = 10, message = "Описание должно содержать минимум 10 символов!")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @NotEmpty(message = "Тип кухни не должен быть пустым!")
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    @NotNull(message = "Рейтинг не должен быть пустым!")
    @DecimalMin(value = "0.0", message = "Рейтинг не может быть меньше 0!")
    @DecimalMax(value = "5.0", message = "Рейтинг не может быть больше 5!")
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @NotEmpty(message = "Телефон не должен быть пустым!")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX (11 цифр)!")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @NotEmpty(message = "Email не должен быть пустым!")
    @Email(message = "Введите корректный email!")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @NotNull(message = "Уровень цен не должен быть пустым!")
    @Min(value = 1, message = "Уровень цен должен быть от 1 до 3!")
    @Max(value = 3, message = "Уровень цен должен быть от 1 до 3!")
    public Integer getPriceLevel() { return priceLevel; }
    public void setPriceLevel(Integer priceLevel) { this.priceLevel = priceLevel; }

    @NotEmpty(message = "Время открытия не должно быть пустым!")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время должно быть в формате HH:MM")
    public String getOpeningTime() { return openingTime; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }

    @NotEmpty(message = "Время закрытия не должно быть пустым!")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время должно быть в формате HH:MM")
    public String getClosingTime() { return closingTime; }
    public void setClosingTime(String closingTime) { this.closingTime = closingTime; }

    @NotNull(message = "Вместимость не должна быть пустой!")
    @Min(value = 1, message = "Минимальная вместимость - 1 человек!")
    @Max(value = 1000, message = "Максимальная вместимость - 1000 человек!")
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    @AssertTrue(message = "Время закрытия должно быть позже времени открытия!")
    public boolean isClosingTimeAfterOpening() {
        if (openingTime == null || closingTime == null) {
            return true;
        }
        try {
            String[] openParts = openingTime.split(":");
            String[] closeParts = closingTime.split(":");

            int openHour = Integer.parseInt(openParts[0]);
            int openMinute = Integer.parseInt(openParts[1]);
            int closeHour = Integer.parseInt(closeParts[0]);
            int closeMinute = Integer.parseInt(closeParts[1]);

            return (closeHour > openHour) ||
                    (closeHour == openHour && closeMinute > openMinute);
        } catch (Exception e) {
            return false;
        }
    }
}