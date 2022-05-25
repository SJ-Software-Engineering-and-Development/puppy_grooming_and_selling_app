package com.example.new_puppy.model;

public class BookedSlot {
   private int id;
    private String package_type;
    private String date;
    private int user_id;
    private int booking_slot_id;

    public BookedSlot() {
    }

    public BookedSlot(int id, String package_type, String date, int user_id, int booking_slot_id) {
        this.id = id;
        this.package_type = package_type;
        this.date = date;
        this.user_id = user_id;
        this.booking_slot_id = booking_slot_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBooking_slot_id() {
        return booking_slot_id;
    }

    public void setBooking_slot_id(int booking_slot_id) {
        this.booking_slot_id = booking_slot_id;
    }
}
