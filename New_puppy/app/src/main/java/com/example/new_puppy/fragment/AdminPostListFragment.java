package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.new_puppy.adapter.AdminPostRecyclerviewAdapter;
import com.example.new_puppy.adapter.PostRecyclerviewAdapter;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.PostStatus;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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


public class AdminPostListFragment extends Fragment {

    private Chip chip_active, chip_pending, chip_denied, chip_deactive;
    private TextView labelNoOfItems;
    private TextInputEditText txtSearch;
    private Button btnRefreshList;

    private SharedPreferences sharedPre;
    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;

    private static RecyclerView postsRecycler;
    private static AdminPostRecyclerviewAdapter adminPostRecyclerviewAdapter;

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;

    private CharSequence search="";
    private String selectedPostStatus;
    private List<Post> postList = new ArrayList<Post>();

    public AdminPostListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_post_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chip_active =(Chip) getView().findViewById(R.id.chip_active);
        chip_pending =(Chip) getView().findViewById(R.id.chip_pending);
        chip_denied =(Chip) getView().findViewById(R.id.chip_denied);
        chip_deactive =(Chip) getView().findViewById(R.id.chip_deactive);
        labelNoOfItems =(TextView) getView().findViewById(R.id.labelNoOfItems);
        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);
        btnRefreshList = (Button) getView().findViewById(R.id.btnRefreshList);

        postsRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        chip_active.setChipBackgroundColorResource(R.color.darkYellow);
        chip_active.setTextColor(Color.parseColor("#FFFFFF"));
        selectedPostStatus = PostStatus.active.toString();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                adminPostRecyclerviewAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts();
            }
        });

        initChipsOnClicks();
        getPosts();
    }

    private void initChipsOnClicks(){
        chip_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllChipsUnselected();
                chip_active.setChipBackgroundColorResource(R.color.darkYellow);
                chip_active.setTextColor(Color.parseColor("#FFFFFF"));
                selectedPostStatus = PostStatus.active.toString();
                getPosts();
            }
        });
        chip_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllChipsUnselected();
                chip_pending.setChipBackgroundColorResource(R.color.darkYellow);
                chip_pending.setTextColor(Color.parseColor("#FFFFFF"));
                selectedPostStatus = PostStatus.pending.toString();
                getPosts();
            }
        });
        chip_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllChipsUnselected();
                chip_denied.setChipBackgroundColorResource(R.color.darkYellow);
                chip_denied.setTextColor(Color.parseColor("#FFFFFF"));
                selectedPostStatus = PostStatus.denied.toString();
                getPosts();
            }
        });
        chip_deactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllChipsUnselected();
                chip_deactive.setChipBackgroundColorResource(R.color.darkYellow);
                chip_deactive.setTextColor(Color.parseColor("#FFFFFF"));
                selectedPostStatus = PostStatus.deactive.toString();
                getPosts();
            }
        });
    }

    private void setAllChipsUnselected(){
        chip_active.setChipBackgroundColorResource(R.color.colorLightGray);
        chip_active.setTextColor(Color.parseColor("#000000"));
        chip_pending.setChipBackgroundColorResource(R.color.colorLightGray);
        chip_pending.setTextColor(Color.parseColor("#000000"));
        chip_denied.setChipBackgroundColorResource(R.color.colorLightGray);
        chip_denied.setTextColor(Color.parseColor("#000000"));
        chip_deactive.setChipBackgroundColorResource(R.color.colorLightGray);
        chip_deactive.setTextColor(Color.parseColor("#000000"));
    }

    //Todo: Post Onclick
    public static void listItemOnClick(int postId){
        replaceFragment(new ViewPostFragment(postId, "AdminPostListFragment"));
    }

    private static void replaceFragment(Fragment fragment){
        //TODO: Goto View ViewDisasterFragmentViewDisasterFragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getPosts(){
        postList.clear();
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getPosts(this.selectedPostStatus);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        //   System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                        Post post = new Gson().fromJson(jsonArray.get(i).toString(), Post.class);
                                        post.setImageUrl(apiBaseUrl+"uploadedFiles/"+post.getImageUrl());
                                        postList.add(post);
                                    }
                                }else{
                                    postList.clear();
                                    setPostRecycler();
                                    Toast.makeText(context, "No "+selectedPostStatus+" posts to show", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
                        if(postList.size() > 0){
                            setPostRecycler();
                        }else{
                            postList.clear();
                            setPostRecycler();
                            Toast.makeText(context, "No "+selectedPostStatus+" posts to show", Toast.LENGTH_LONG).show();
                        }
                        labelNoOfItems.setText(postList.size()+" posts");
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
                    progressDialog.dismiss();
                }
                //  appProgressDialog.dismiss();
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
        adminPostRecyclerviewAdapter = new AdminPostRecyclerviewAdapter(context, postList);
        postsRecycler.setAdapter(adminPostRecyclerviewAdapter);
    }
}