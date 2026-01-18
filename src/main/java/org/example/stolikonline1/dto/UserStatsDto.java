package org.example.stolikonline1.dto;

public class UserStatsDto {
    private int totalBookings;
    private int activeBookings;
    private int completedBookings;
    private int cancelledBookings;

    // Конструкторы
    public UserStatsDto() {
        this.totalBookings = 0;
        this.activeBookings = 0;
        this.completedBookings = 0;
        this.cancelledBookings = 0;
    }

    public UserStatsDto(int totalBookings, int activeBookings,
                        int completedBookings, int cancelledBookings) {
        this.totalBookings = totalBookings;
        this.activeBookings = activeBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    // Геттеры и сеттеры
    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }

    public int getActiveBookings() { return activeBookings; }
    public void setActiveBookings(int activeBookings) { this.activeBookings = activeBookings; }

    public int getCompletedBookings() { return completedBookings; }
    public void setCompletedBookings(int completedBookings) { this.completedBookings = completedBookings; }

    public int getCancelledBookings() { return cancelledBookings; }
    public void setCancelledBookings(int cancelledBookings) { this.cancelledBookings = cancelledBookings; }
}