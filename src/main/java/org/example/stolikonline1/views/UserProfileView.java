package org.example.stolikonline1.views;

public class UserProfileView {
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private int age;

    public UserProfileView() {
    }

    public UserProfileView(String username, String email, String fullName, String phone, int age) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}