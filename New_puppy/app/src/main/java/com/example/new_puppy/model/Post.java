package com.example.new_puppy.model;

public class Post {
    private int id;
    private String title;
    private String price;
    private String description;
    private String gender;
    private String age;
    private String date;
    private String location;
    private String imageUrl;

    public Post() {
    }

    public Post(int id, String title, String price, String description, String gender, String age, String date, String location, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.gender = gender;
        this.age = age;
        this.date = date;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
