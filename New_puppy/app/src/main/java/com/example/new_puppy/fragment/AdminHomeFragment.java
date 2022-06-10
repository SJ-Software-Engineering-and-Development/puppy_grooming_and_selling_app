package com.example.new_puppy.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.User;
import com.example.new_puppy.model.YtbVideo;
import com.example.new_puppy.utils.AdminDashboardStorage;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.example.new_puppy.utils.UserStorage;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeFragment extends Fragment {

    private SharedPreferences sharedPre;
    static String apiBaseUrl = "";
    private String login_id ="";
    private ProgressDialog progressDialog;
    private static Context context;
    static FragmentManager fragmentManager;

    TextView txtNameOfUser, txtEmail, txtUserRole;
    TextView txtMostDemandBreed, txtMaxViewVideoTitle, txtMaxViewVideoNoOfViews, txtMaxLikesVideoTitle,txtMaxLikesVideoNoOfLikes;
    TextView txt_total_online_post, txt_total_pending_post;

    Button btnRefreshBreedsCard, btnRefreshPostStatusCard;
    MaterialCardView card_posts, card_bookings, card_users, card_veterinary, card_videos, card_post_statistics;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        sharedPre= getActivity().getSharedPreferences("puppy_app",0);
        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");
        progressDialog = new ProgressDialog(context);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        card_posts = (MaterialCardView) getView().findViewById(R.id.card_posts);
        card_bookings = (MaterialCardView) getView().findViewById(R.id.card_bookings);
        card_users = (MaterialCardView) getView().findViewById(R.id.card_users);
        card_veterinary = (MaterialCardView) getView().findViewById(R.id.card_veterinary);
        card_videos = (MaterialCardView) getView().findViewById(R.id.card_videos);
        card_post_statistics = (MaterialCardView) getView().findViewById(R.id.card_post_statistics);

        txtNameOfUser = (TextView) getView().findViewById(R.id.txtNameOfUser);
        txtEmail = (TextView) getView().findViewById(R.id.txtEmail);
        txtUserRole = (TextView) getView().findViewById(R.id.txtUserRole);

        btnRefreshBreedsCard = (Button) getView().findViewById(R.id.btnRefreshBreedsCard);
        btnRefreshPostStatusCard = (Button) getView().findViewById(R.id.btnRefreshPostStatusCard);

        txt_total_online_post = (TextView) getView().findViewById(R.id.txt_total_online_post);
        txt_total_pending_post = (TextView) getView().findViewById(R.id.txt_total_pending_post);

        txtMostDemandBreed= (TextView) getView().findViewById(R.id.txtMostDemandBreed);
        txtMaxViewVideoTitle= (TextView) getView().findViewById(R.id.txtMaxViewVideoTitle);
        txtMaxViewVideoNoOfViews= (TextView) getView().findViewById(R.id.txtMaxViewVideoNoOfViews);
        txtMaxLikesVideoTitle= (TextView) getView().findViewById(R.id.txtMaxLikesVideoTitle);
        txtMaxLikesVideoNoOfLikes= (TextView) getView().findViewById(R.id.txtMaxLikesVideoNoOfLikes);

        card_post_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new AdminPostListFragment());
            }
        });

        card_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new AdminPostListFragment());
            }
        });

        card_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new AdminBookingListFragment());
            }
        });

        card_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new UsersListFragment());
            }
        });

        card_veterinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new VeterinaryFragment());
            }
        });

        card_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new YtbVideoFragment());
            }
        });

        btnRefreshBreedsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMaxStatus();
                getMostDemandBreed();
            }
        });

        btnRefreshPostStatusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPostCountStatistics();
            }
        });

        if(UserStorage.user!=null){
            if(UserStorage.user.getId() != 0){
                updateView();
            }
        }else{
            getUser(this.login_id);
        }

        getMaxStatus();
        getMostDemandBreed();
        getPostCountStatistics();
    }

    private void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void getUser(String id){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUserById(id);

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        //  System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONObject userObj = new JSONObject(jsnobject.getString("data"));
                                User user = new User(Integer.parseInt(userObj.getString("id")),userObj.getString("Full Name"),userObj.getString("Nic Number"),userObj.getString("City"),userObj.getString("User Type"),userObj.getString("E-mail"),userObj.getString("Contact No"));
                                //Save user in static variable
                                UserStorage.user = user;
                                updateView();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void getPostCountStatistics(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getPostCountStatistics();

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONObject dataObj = new JSONObject(jsnobject.getString("data"));
                                // JSONObject max_likesObj = new JSONObject(jsnobject.getString("max_likes_video"));
                                try {

                                    AdminDashboardStorage.total_online_post = Integer.parseInt(dataObj.getString("total_online"));
                                    AdminDashboardStorage.total_pending_post = Integer.parseInt(dataObj.getString("total_pending"));

                                }catch (JSONException e){
                                    System.out.println("_==================JSONException=======");
                                    e.printStackTrace();
                                }

                                updatePostStatusCard();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }


    private void getMaxStatus(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getMaxStatus();

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                          System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONObject dataObj = new JSONObject(jsnobject.getString("data"));
                               // JSONObject max_likesObj = new JSONObject(jsnobject.getString("max_likes_video"));
                                try {
                                    YtbVideo max_likes_video = new Gson().fromJson(dataObj.getString("max_likes_video"), YtbVideo.class);
                                    YtbVideo max_views_video = new Gson().fromJson(dataObj.getString("max_views_video"), YtbVideo.class);

                                    AdminDashboardStorage.max_likes_video = max_likes_video;
                                    AdminDashboardStorage.max_views_video = max_views_video;
                                }catch (JSONException e){
                                    System.out.println("_==================JSONException=======");
                                   e.printStackTrace();
                                }

                                updateStatusCard();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void getMostDemandBreed(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getMostDemandBreed();

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                if(!jsnobject.getString("data").equals("")){
                                    JSONObject dataObj = new JSONObject(jsnobject.getString("data"));
                                    String breed_name = dataObj.getString("max_breed_name");
                                    Integer breed_id = dataObj.getInt("max_breed_id");
                                    Integer no_of_post = dataObj.getInt("no_of_post");
                                    AdminDashboardStorage.max_breed_id = breed_id;
                                    AdminDashboardStorage.max_breed_name = breed_name;
                                    AdminDashboardStorage.max_breed_posts = no_of_post;
                                    updateStatusCard_S2();
                                }else{
                                    System.out.println("_==================Returned empty data");
                                }
                            }else{
                                System.out.println("_==================Unsuccess");
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void updateView(){
        txtNameOfUser.setText(UserStorage.user.getFullName());
        txtEmail.setText(UserStorage.user.getEmail());
        txtUserRole.setText(UserStorage.user.getUserType());
    }

    private void updateStatusCard(){
        if(AdminDashboardStorage.max_likes_video!=null){
            YtbVideo max_likes_video = AdminDashboardStorage.max_likes_video;
            YtbVideo max_views_video = AdminDashboardStorage.max_views_video;
            txtMaxLikesVideoTitle.setText(max_likes_video.getTitle());
            txtMaxLikesVideoNoOfLikes.setText("LIKES: "+max_likes_video.getLikes() + " " + "VIEWS: " + max_likes_video.getViews());
            txtMaxViewVideoTitle.setText(max_views_video.getTitle());
            txtMaxViewVideoNoOfViews.setText("LIKES: "+max_views_video.getLikes() + " " + "VIEWS: " + max_views_video.getViews());
        }else{
            txtMaxLikesVideoTitle.setText("No videos so far");
            txtMaxLikesVideoNoOfLikes.setText("");
            txtMaxViewVideoTitle.setText("No videos so far");
            txtMaxViewVideoNoOfViews.setText("");
        }
    }

    private void updatePostStatusCard(){
       txt_total_online_post.setText("Total post online: "+AdminDashboardStorage.total_online_post);
       txt_total_pending_post.setText("New pending approval: "+AdminDashboardStorage.total_pending_post);
    }

    private void updateStatusCard_S2(){
        if(AdminDashboardStorage.max_breed_name!=null){
            txtMostDemandBreed.setText(AdminDashboardStorage.max_breed_name+" (" + AdminDashboardStorage.max_breed_posts + " posts)");
        }else{
            System.out.println("===================== NULL");
        }
    }
}