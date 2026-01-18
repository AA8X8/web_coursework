package org.example.stolikonline1.dto;

import java.io.Serializable;

public class ShowRestaurantInfoDto implements Serializable {
    private String name;
    private String address;
    private String cuisine;
    private Double rating;
    private String photoUrl;
    private Integer priceLevel;
    private String openingTime;
    private String closingTime;
    private Integer maxCapacity;
    private String phone;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Integer getPriceLevel() { return priceLevel; }
    public void setPriceLevel(Integer priceLevel) { this.priceLevel = priceLevel; }

    public String getOpeningTime() { return openingTime; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }

    public String getClosingTime() { return closingTime; }
    public void setClosingTime(String closingTime) { this.closingTime = closingTime; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    // ДОБАВЬТЕ ЭТИ МЕТОДЫ ДЛЯ ТЕЛЕФОНА
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}