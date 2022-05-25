package com.example.new_puppy.model;

public class BookingSlot {

    private int id;
    private String fromTime;
    private String toTime;
    private String description;
    private boolean isAvailable;

    public BookingSlot() {
    }

    public BookingSlot(int id, String fromTime, String toTime, String description, boolean isAvailable) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
