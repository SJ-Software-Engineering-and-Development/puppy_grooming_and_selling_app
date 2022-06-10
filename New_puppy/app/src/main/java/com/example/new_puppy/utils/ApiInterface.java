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

    @GET("post/getStatistics.php")
    Call<String> getPostCountStatistics();

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
                                  @Field("breed_id") String breed_id,
                                  @Field("owner_id") String owner_id);

    @FormUrlEncoded
    @POST("post/getPosts.php")
    Call<String> getPosts(@Field("status") String status);

    @FormUrlEncoded
    @POST("post/getPostsByUserId.php")
    Call<String> getPostsByUserId(@Field("status") String status, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("post/getPostById.php")
    Call<String> getPostById(@Field("id") String id);


    @FormUrlEncoded
    @POST("post/updatePostStatus.php")
    Call<String> updatePostStatus(@Field("id") String id, @Field("status") String status);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



    //TODO :   USER ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("user/getAllUsers.php")
    Call<String> getUsers();

    @FormUrlEncoded
    @POST("user/getUser.php")
    Call<String> getUserById(@Field("id") String id);

    @FormUrlEncoded
    @POST("user/updateAccountStatus.php")
    Call<String> updateAccountStatus(@Field("id") String id, @Field("status") String status);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



    //TODO :   Booking ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("booking/getBookingSlots.php")
    Call<String> getBookingSlots();

    @FormUrlEncoded
    @POST("booking/getBookedSlots.php")
    Call<String> getBookedSlots(@Field("date") String date);

    @FormUrlEncoded
    @POST("booking/getBookingByDate.php")
    Call<String> getBookedSlotsByDate(@Field("date") String date);

    @FormUrlEncoded
    @POST("booking/getBookingByUserid.php")
    Call<String> getBookedSlotsByUserId(@Field("date") String date, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("booking/addSlot.php.")
    Call<String> addBookingSlot(@Field("from_time") String from_time, @Field("to_time") String to_time, @Field("description") String description);

    @FormUrlEncoded
    @POST("booking/bookSlot.php")
    Call<String> bookSlot(@Field("package_type") String package_type,
                          @Field("date") String date,
                          @Field("user_id") String user_id,
                          @Field("booking_slot_id") String booking_slot_id);

    @FormUrlEncoded
    @POST("booking/deleteSlot.php")
    Call<String> deleteSlot(@Field("id") String id);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //TODO :   Verianry :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("veterinary/getVeterinaries.php")
    Call<String> getVeterinaries();

    @FormUrlEncoded
    @POST("veterinary/getVeterinaryById.php")
    Call<String> getVeterinaryById(@Field("id") String id);

    @FormUrlEncoded
    @POST("veterinary/getVeterinaryByCity.php")
    Call<String> getVeterinaryByCity(@Field("city") String city);


    @FormUrlEncoded
    @POST("veterinary/addVeterinary.php")
    Call<String> addVeterinary(@Field("title") String title,
                     @Field("city") String city,
                     @Field("address") String address,
                     @Field("contact") String contact,
                     @Field("open_close_times") String open_close_times,
                     @Field("longitude") String longitude,
                     @Field("latitude") String latitude);

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


    //TODO :   breed :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("breed/getBreeds.php")
    Call<String> getBreeds();

    @GET("breed/getMostDemandBreed.php")
    Call<String> getMostDemandBreed();

    //TODO :  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



    //TODO :   Youtube videos ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @GET("youtube_video/getVideos.php")
    Call<String> getVideos();

    @FormUrlEncoded
    @POST("youtube_video/addVideo.php")
    Call<String> addVideo(@Field("url") String url);

    @GET("youtube_video/getMaxStatus.php")
    Call<String> getMaxStatus();

    @FormUrlEncoded
    @POST("youtube_video/deleteVideo.php")
    Call<String> deleteVideo(@Field("id") String id);
    //TODO :  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

}
