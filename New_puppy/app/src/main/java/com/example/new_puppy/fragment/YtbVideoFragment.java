package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.adapter.VideoRVAdapter;

import com.example.new_puppy.model.YtbVideo;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
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

public class YtbVideoFragment extends Fragment {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static ProgressDialog progressDialog;
    private static Dialog appCustomDialog;
    private SharedPreferences sharedPre;
    private static FragmentManager fragmentManager;

    private TextView labelNoOfItems, txtDate;
    private Button btnAddNew, btnRefreshList;
    private TextInputEditText txtSearch;

    private static List<YtbVideo> videosList = new ArrayList<YtbVideo>();

    private CharSequence search="";
    private static String dateSelected ="";
    private static String login_id ="";


    private static RecyclerView postsRecycler;
    private static VideoRVAdapter videoRVAdapter;


    public YtbVideoFragment() {
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
        return inflater.inflate(R.layout.fragment_ytb_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        labelNoOfItems =(TextView) getView().findViewById(R.id.labelNoOfItems);
        postsRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);
        btnAddNew = (Button) getView().findViewById(R.id.btnAddNew);
        btnRefreshList = (Button) getView().findViewById(R.id.btnRefreshList);
        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                videoRVAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initCustomDialogAddVideo();
        btnClickActions();

        getYtbVideos();
    }

    private void btnClickActions(){
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        btnRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getYtbVideos();
            }
        });
    }

    private void initCustomDialogAddVideo(){

        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_video);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextInputEditText txt_url;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        txt_url = appCustomDialog.findViewById(R.id.txt_url);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);
        //labelFormHeading.setText("Add New "+ userType);
        txt_url.setText("");

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String video_url= txt_url.getText().toString().trim();

                if(  video_url.equals("") ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{
                    //TODO: Display Confirm Dialog
                    showConfirmDialog(getActivity(),() -> {
                        //TODO: confirmCall
                        txt_url.setText("");
                        appCustomDialog.dismiss();
                        addYtbVideo(video_url);
                    }, ()->{
                        //TODO: cancelCall
                        txt_url.setText("");
                    });
                }
            }
        });
        // TODO: Custom Dialog End ::::::::::::::::::::::::::::::::::::::::::
    }

    //Todo: Post Onclick
    public static void listItemOnClick(int postId){
       // Fragment fragment = new ViewUserFragment(postId);
       // replaceFragment(fragment);
    }

    private void getYtbVideos(){
        progressDialog.show();
        videosList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getVideos();

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
                                        YtbVideo video = new Gson().fromJson(jsonArray.get(i).toString(), YtbVideo.class);
                                        System.out.println("=============================== tostring ");
                                        System.out.println(video);
                                        videosList.add(video);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================JSONException");
                            e.printStackTrace();
                        }
                        labelNoOfItems.setText(videosList.size()+" videos");
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

    private void addYtbVideo(String video_url){
        progressDialog.show();
        videosList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.addVideo(video_url);

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
                                appCustomDialog.dismiss();
                                showDialog(context, "Done..!", "New video has been added!", () -> {
                                    getYtbVideos();
                                });
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================JSONException");
                            e.printStackTrace();
                        }
                        labelNoOfItems.setText(videosList.size()+" videos");
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
        videoRVAdapter = new VideoRVAdapter(context, videosList);
        postsRecycler.setAdapter(videoRVAdapter);
    }

    private static void replaceFragment(Fragment fragment){
        //TODO: Goto View ViewDisasterFragmentViewDisasterFragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure ?")
                .setMessage("Are you sure to continue this task")
                .setCancelable(false)
                .setPositiveButton("Confirm",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Cancel",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
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
}