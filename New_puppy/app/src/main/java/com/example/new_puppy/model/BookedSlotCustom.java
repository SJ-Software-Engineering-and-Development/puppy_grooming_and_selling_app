package com.example.new_puppy.model;

public class BookedSlotCustom {
    private int id;
    private String package_type;
    private String date;
    private String slot_time;

    public BookedSlotCustom() {
    }

    public BookedSlotCustom(int id, String package_type, String date, String slot_time) {
        this.id = id;
        this.package_type = package_type;
        this.date = date;
        this.slot_time = slot_time;
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

    public String getSlot_time() {
        return slot_time;
    }

    public void setSlot_time(String slot_time) {
        this.slot_time = slot_time;
    }
}
