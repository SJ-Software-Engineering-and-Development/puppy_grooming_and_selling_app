package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.adapter.UsersRVAdapter;
import com.example.new_puppy.model.BookedSlot;
import com.example.new_puppy.model.BookedSlotCustom;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UsersListFragment extends Fragment {
    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static ProgressDialog progressDialog;
    private SharedPreferences sharedPre;
    private static FragmentManager fragmentManager;

    private TextView labelNoOfItems, txtDate;
    private Button btnGoBack, btnGoForward;

    private static List<User> usersList = new ArrayList<User>();

    private static String dateSelected ="";
    private static String login_id ="";

    private static RecyclerView postsRecycler;
    private static UsersRVAdapter usersRVAdapter;

    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelNoOfItems =(TextView) getView().findViewById(R.id.labelNoOfItems);
        postsRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        getUsers();
    }

    //Todo: Post Onclick
    public static void listItemOnClick(int postId){
        Fragment fragment = new ViewUserFragment(postId);
        replaceFragment(fragment);
    }


    private void getUsers(){
        progressDialog.show();
        usersList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUsers();

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
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                        JSONObject userObj =  new JSONObject(jsonArray.get(i).toString());
                                        usersList
                                                .add(new User(Integer.parseInt(userObj.getString("id")),
                                                        userObj.getString("Full Name"),
                                                        userObj.getString("Nic Number"),
                                                        userObj.getString("City"),
                                                        userObj.getString("User Type"),
                                                        userObj.getString("E-mail"),
                                                        userObj.getString("Contact No")));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        labelNoOfItems.setText(usersList.size()+" users");
                    } else {
                        labelNoOfItems.setText("0 users");
                        Toast.makeText(context, "No Users to show", Toast.LENGTH_LONG).show();
                        System.out.println("_==================Returned empty response");
                    }
                    setPostRecycler();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    progressDialog.dismiss();
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void  setPostRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);
        usersRVAdapter = new UsersRVAdapter(context, usersList);
        postsRecycler.setAdapter(usersRVAdapter);
    }

    private static void replaceFragment(Fragment fragment){
        //TODO: Goto View ViewDisasterFragmentViewDisasterFragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}