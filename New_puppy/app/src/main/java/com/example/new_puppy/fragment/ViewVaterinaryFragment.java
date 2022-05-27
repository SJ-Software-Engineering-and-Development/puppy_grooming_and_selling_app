package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.model.User;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.example.new_puppy.utils.UserStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewVaterinaryFragment extends Fragment {
    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static ProgressDialog progressDialog;
    private SharedPreferences sharedPre;

    private int vet_id;

    TextView txtTitle, txtOpenClose, txtCity, txtAdress, txtContact;
    // ImageView imgViewClinic;


    public ViewVaterinaryFragment() {
        // Required empty public constructor
    }

    public ViewVaterinaryFragment(int id) {
        this.vet_id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_vaterinary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        txtTitle = (TextView) getView().findViewById(R.id.txtTitle);
        txtOpenClose = (TextView) getView().findViewById(R.id.txtOpenClose);
        txtCity = (TextView) getView().findViewById(R.id.txtCity);
        txtAdress = (TextView) getView().findViewById(R.id.txtAdress);
        txtContact = (TextView) getView().findViewById(R.id.txtContact);

        //imgViewClinic = (ImageView) getView().findViewById(R.id.imgViewClinic);
        // String imgUrl = "https://leverageedublog.s3.ap-south-1.amazonaws.com/blog/wp-content/uploads/2019/11/23171851/Veterinary-Courses.png";
        // Picasso.get().load(imgUrl).into(imgViewClinic);

        getVeterinary();
    }

    private void getVeterinary(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getVeterinaryById(String.valueOf(this.vet_id));

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
                                //JSONObject userObj = new JSONObject(jsnobject.getString("data"));
                                Veterinary veterinary = new Gson().fromJson(jsnobject.getString("data"), Veterinary.class);
                             if(veterinary!=null) updateView(veterinary);
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

    private void updateView(Veterinary vet){
        txtTitle.setText(vet.getTitle());
        txtOpenClose.setText(vet.getOpen_close_times());
        txtCity.setText(vet.getCity());
        txtAdress.setText(vet.getAddress());
        txtContact.setText(vet.getContact());
    }
}