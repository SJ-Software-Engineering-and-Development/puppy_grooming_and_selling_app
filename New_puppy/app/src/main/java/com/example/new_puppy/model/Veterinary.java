package com.example.new_puppy.model;

public class Veterinary {
    private int id;
    private String title;
    private String city;
    private String address;
    private String contact;
    private String open_close_times;
    private String longitude;
    private String latitude;

    public Veterinary() {
    }

    public Veterinary(int id, String title, String city, String address, String contact, String open_close_times, String longitude, String latitude) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.address = address;
        this.contact = contact;
        this.open_close_times = open_close_times;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOpen_close_times() {
        return open_close_times;
    }

    public void setOpen_close_times(String open_close_times) {
        this.open_close_times = open_close_times;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Veterinary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                ", open_close_times='" + open_close_times + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
