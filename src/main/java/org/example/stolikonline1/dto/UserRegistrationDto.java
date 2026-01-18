package org.example.stolikonline1.dto;

import jakarta.validation.constraints.*;
import org.example.stolikonline1.utils.validation.UniqueEmail;
import org.example.stolikonline1.utils.validation.UniqueUsername;

public class UserRegistrationDto {

    @UniqueUsername
    private String username;
    private String fullName;

    @UniqueEmail
    private String email;
    private String phone;
    private int age;
    private String password;
    private String confirmPassword;

    public UserRegistrationDto() {}

    @NotEmpty(message = "Имя пользователя не должно быть пустым!")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов!")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @NotEmpty(message = "Полное имя не должно быть пустым!")
    @Size(min = 2, max = 50, message = "Полное имя должно быть от 2 до 50 символов!")
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @NotEmpty(message = "Email не должен быть пустым!")
    @Email(message = "Введите корректный email!")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @NotEmpty(message = "Телефон не должен быть пустым!")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX (11 цифр)!")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Min(value = 14, message = "Возраст не может быть меньше 14!")
    @Max(value = 90, message = "Возраст не может быть больше 90!")
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @NotEmpty(message = "Пароль не должен быть пустым!")
    @Size(min = 5, max = 20, message = "Пароль должен быть от 5 до 20 символов!")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @NotEmpty(message = "Подтверждение пароля не должно быть пустым!")
    @Size(min = 5, max = 20, message = "Подтверждение пароля должно быть от 5 до 20 символов!")
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    @Override
    public String toString() {
        return "UserRegistrationDto{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}