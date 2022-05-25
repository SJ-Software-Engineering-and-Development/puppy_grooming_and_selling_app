package com.example.new_puppy.model;

public class User {
   private int id;
   private String fullName;
   private String NIC;
   private String city;
   private String userType;
   private String email;
   private String contactNo;

   public User() {
   }

   public User(int id, String fullName, String NIC, String city, String userType, String email, String contactNo) {
      this.id = id;
      this.fullName = fullName;
      this.NIC = NIC;
      this.city = city;
      this.userType = userType;
      this.email = email;
      this.contactNo = contactNo;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getNIC() {
      return NIC;
   }

   public void setNIC(String NIC) {
      this.NIC = NIC;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getUserType() {
      return userType;
   }

   public void setUserType(String userType) {
      this.userType = userType;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getContactNo() {
      return contactNo;
   }

   public void setContactNo(String contactNo) {
      this.contactNo = contactNo;
   }
}
