package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.PostStatus;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.Navigation;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDivider;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPostFragment extends Fragment {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPre;

    private ImageSlider sliderPostImages;

    private TextView txt_title, txtLocation, txtPrice, txtGender, txtDescription, txtDate, txtSellerName, txtSellerEmail, txtSellerContact;
    private TextView labelCurrentStatus, txtCurrentStatus;
    private ChipGroup chip_group;
    private Chip chip_publish, chip_deny, chip_deactive;
    private MaterialDivider deviver_3;

    private int post_id;
    private String currentUserRole;

    public ViewPostFragment() {
        // Required empty public constructor
    }

    public ViewPostFragment(int postId, String cameFrom) {
        this.post_id = postId;
        if(cameFrom.equals("AdminPostListFragment"))
            Navigation.currentScreen = "GOTO_AdminPostListFragment";
        else if(cameFrom.equals("PostListingFragment"))
            Navigation.currentScreen = "GOTO_PostListingFragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);

        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        currentUserRole = AppSharedPreferences.getData(sharedPre,"user_role");

        System.out.println("================usertype=====: "+ this.currentUserRole);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_title  = (TextView) getView().findViewById(R.id.txt_title);
        txtLocation = (TextView) getView().findViewById(R.id.txtLocation);
        txtPrice = (TextView) getView().findViewById(R.id.txtPrice);
        txtGender = (TextView) getView().findViewById(R.id.txtGender);
        txtDescription = (TextView) getView().findViewById(R.id.txtDescription);
        txtDate = (TextView) getView().findViewById(R.id.txtDate);
        txtSellerName = (TextView) getView().findViewById(R.id.txtSellerName);
        txtSellerEmail = (TextView) getView().findViewById(R.id.txtSellerEmail);
        txtSellerContact = (TextView) getView().findViewById(R.id.txtSellerContact);

        labelCurrentStatus = (TextView) getView().findViewById(R.id.labelCurrentStatus);
        txtCurrentStatus = (TextView) getView().findViewById(R.id.txtCurrentStatus);

        sliderPostImages= (ImageSlider) getView().findViewById(R.id.sliderPostImages);
        deviver_3= (MaterialDivider) getView().findViewById(R.id.deviver_3);

        chip_group= (ChipGroup) getView().findViewById(R.id.chip_group);
        chip_publish= (Chip) getView().findViewById(R.id.chip_publish);
        chip_deny= (Chip) getView().findViewById(R.id.chip_deny);
        chip_deactive= (Chip) getView().findViewById(R.id.chip_deactive);

        if(this.currentUserRole.equals("admin")){
            deviver_3.setVisibility(View.VISIBLE);
            chip_group.setVisibility(View.VISIBLE);
            labelCurrentStatus.setVisibility(View.VISIBLE);
            txtCurrentStatus.setVisibility(View.VISIBLE);
        }else{
            deviver_3.setVisibility(View.GONE);
            chip_group.setVisibility(View.GONE);
            labelCurrentStatus.setVisibility(View.GONE);
            txtCurrentStatus.setVisibility(View.GONE);
        }

        initChipsOnClicks();
        getPostById();
    }

    private void getPostById(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getPostById(String.valueOf(this.post_id));

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                Post post = new Gson().fromJson(jsnobject.getString("data"), Post.class);
                                updateView(post);
                                getPostOwner(post.getOwner_id());
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

    private void getPostOwner(int id){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUserById(String.valueOf(id));

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
                                updateView(user);
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

    private void updateView(Post post){
        txt_title.setText(post.getTitle());
        txtLocation.setText(post.getLocation());
        txtPrice.setText("Rs."+post.getPrice());
        txtGender.setText(post.getGender());
        txtDescription.setText(post.getDescription());
        txtDate.setText(post.getDate());

        List<SlideModel> slideModels= new ArrayList<>();
        //TODO: Can add more than one images as bellow
        slideModels.add(new SlideModel(apiBaseUrl+"uploadedFiles/"+post.getImageUrl()));

        sliderPostImages.setImageList(slideModels, true);
        if(this.currentUserRole.equals("admin")){
            txtCurrentStatus.setText("Current Status: "+post.getStatus().toString());

            switch (post.getStatus().toString()){
                case "pending":
                    chip_publish.setVisibility(View.VISIBLE);
                    chip_deny.setVisibility(View.VISIBLE);
                    break;
                case "active":
                    chip_publish.setVisibility(View.GONE);
                    chip_deny.setVisibility(View.GONE);
                    break;
                case "denied":
                    chip_deny.setVisibility(View.GONE);
                    chip_publish.setVisibility(View.VISIBLE);
                    break;
                case "deactive":
                    chip_publish.setVisibility(View.VISIBLE);
                    chip_deactive.setVisibility(View.GONE);
                    chip_deny.setVisibility(View.GONE);
                    break;
            }
        }

    }

    private void updateView(User user){
        txtSellerName.setText(user.getFullName());
        txtSellerEmail.setText(user.getEmail());
        txtSellerContact.setText(user.getContactNo());
    }

    private void initChipsOnClicks(){
        chip_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAction(PostStatus.active);
            }
        });
        chip_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAction(PostStatus.denied);
            }
        });
        chip_deactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAction(PostStatus.deactive);
            }
        });
    }

    private void takeAction(PostStatus postStatus){
        showConfirmDialog(context, "Confirm", "Are you sure to "+postStatus.toString() +" this post?",
                ()->{ updatePostStatus(postStatus);},
                ()->{});
    }

    private void updatePostStatus(PostStatus postStatus){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.updatePostStatus(String.valueOf(this.post_id), postStatus.toString());

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
                                showDialog(context,"Done",("Post has been "+postStatus.toString()), ()->{ });
                                getPostById();
                            }else{
                                showDialog(context,"Opps..!",("Something went wrong!"), ()->{ });
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        showDialog(context,"Opps..!",("Something went wrong!"), ()->{ });
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                        showDialog(context,"Opps..!",("Something went wrong!"), ()->{ });
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
                showDialog(context,"Opps..!",("Something went wrong!"), ()->{ });
            }
        });
    }


    public static  void showDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Not now",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }
}