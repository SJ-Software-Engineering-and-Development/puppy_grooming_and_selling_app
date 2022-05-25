package com.example.new_puppy.utils;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    //TODO :   POST ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @FormUrlEncoded
    @POST("post/submitPost.php")
    Call<String> submitPost(@Field("title") String title,
                                  @Field("price") String price,
                                  @Field("description") String description,
                                  @Field("gender") String gender,
                                  @Field("age") String age,
                                  @Field("date") String date,
                                  @Field("location") String location,
                                  @Field("imageName") String image,
                                  @Field("encodedImage") String encodedImage,
                                  @Field("status") String status,
                                  @Field("owner_id") String owner_id);

    @FormUrlEncoded
    @POST("post/getPosts.php")
    Call<String> getPosts(@Field("status") String status);

    @FormUrlEncoded
    @POST("post/getPostById.php")
    Call<String> getPostById(@Field("id") String id);


    @FormUrlEncoded
    @POST("post/updatePostStatus.php")
    Call<String> updatePostStatus(@Field("id") String id, @Field("status") String status);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



    //TODO :   USER ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @FormUrlEncoded
    @POST("user/getUser.php")
    Call<String> getUserById(@Field("id") String id);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



    //TODO :   Booking ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("booking/getBookingSlots.php")
    Call<String> getBookingSlots();

    @FormUrlEncoded
    @POST("booking/getBookedSlots.php")
    Call<String> getBookedSlots(@Field("date") String date);

    @FormUrlEncoded
    @POST("booking/bookSlot.php")
    Call<String> bookSlot(@Field("package_type") String package_type, @Field("date") String date, @Field("user_id") String user_id, @Field("booking_slot_id") String booking_slot_id);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
}
