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
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.example.new_puppy.utils.UserStorage;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeFragment extends Fragment {

    private static Context context;
    private SharedPreferences sharedPre;
    static FragmentManager fragmentManager;
    private ProgressDialog progressDialog;

    MaterialCardView card_posts, card_bookings, card_videos;
    TextView txtNameOfUser, txtEmail, txtUserRole;

    private String apiBaseUrl = "";
    private String login_id ="";

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");
        fragmentManager = getActivity().getSupportFragmentManager();
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNameOfUser = (TextView) getView().findViewById(R.id.txtNameOfUser);
        txtEmail = (TextView) getView().findViewById(R.id.txtEmail);
        txtUserRole = (TextView) getView().findViewById(R.id.txtUserRole);

        card_posts = (MaterialCardView) getView().findViewById(R.id.card_posts);
        card_bookings = (MaterialCardView) getView().findViewById(R.id.card_bookings);
        card_videos = (MaterialCardView) getView().findViewById(R.id.card_videos);

        card_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new UserPostListFragment());
            }
        });

        card_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  gotoFragment(new UserBookingListFragment());
            }
        });

        card_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  gotoFragment(new UserVideosListFragment());
            }
        });

        if(UserStorage.user!=null){
            if(UserStorage.user.getId() != 0){
                updateView();
            }
        }else{
            getUser(this.login_id);
        }
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

    private void updateView(){
        txtNameOfUser.setText(UserStorage.user.getFullName());
        txtEmail.setText(UserStorage.user.getEmail());
        txtUserRole.setText(UserStorage.user.getUserType());
    }

    private void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}