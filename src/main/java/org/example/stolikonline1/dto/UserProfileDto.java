package org.example.stolikonline1.dto;

public class UserProfileDto {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private int age;
    private String profilePictureUrl;

    // Конструкторы
    public UserProfileDto() {}

    public UserProfileDto(String username, String fullName, String email,
                          String phone, int age, String profilePictureUrl) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}