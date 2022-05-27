package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.adapter.PostRecyclerviewAdapter;
import com.example.new_puppy.adapter.VeterinaryRVAdapter;
import com.example.new_puppy.adapter.VeterinaryRVAdapter2;
import com.example.new_puppy.model.SearchModel;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserVeterinaryListFragment extends Fragment {
    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;

    private Chip chip_choose_city;
    private SwipeRefreshLayout swipeContainer;
    private CircularProgressIndicator progress_circular;
    private TextView noListingsItemsLabel;

    private Button btnFullView, btnFullViewClose;

    private static RecyclerView veterinaryRecycler;
    private static VeterinaryRVAdapter2 veterinaryRVAdapter2;

    private CharSequence search="";
    private String selectedCity="";

    private ImageView imgViewClinic;

    private static List<Veterinary> veterinaryList = new ArrayList<Veterinary>();

    public UserVeterinaryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_veterinary_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chip_choose_city =(Chip) getView().findViewById(R.id.chip_choose_city);
        veterinaryRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);
        swipeContainer  = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        noListingsItemsLabel  = (TextView) getView().findViewById(R.id.noListingsItemsLabel);

        chip_choose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(getActivity(), "Search...",
                        "Search  city name here...?", null, getCityList(),
                        new SearchResultListener<SearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchModel item, int position) {

                                // If filtering is enabled, [position] is the index of the item in the filtered result, not in the unfiltered source
                                //   Log.d("_location_", item.getTitle().toString() );
                                chip_choose_city.setText(item.getTitle());
                                selectedCity = item.getTitle();
                                System.out.println("===============selectedCity " + selectedCity);
                                dialog.dismiss();
                                getVeterianries();
                            }
                        }).show();
            }
        });
        btnFullView = (Button) getView().findViewById(R.id.btnFullView);
        btnFullViewClose = (Button) getView().findViewById(R.id.btnFullViewClose);

        progress_circular = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

        btnFullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgViewClinic.setVisibility(View.GONE);
                btnFullView.setVisibility(View.GONE);
                btnFullViewClose.setVisibility(View.VISIBLE);
            }
        });
        btnFullViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgViewClinic.setVisibility(View.VISIBLE);
                btnFullView.setVisibility(View.VISIBLE);
                btnFullViewClose.setVisibility(View.GONE);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getVeterianries();
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        getVeterianries();
        swipeContainer.setRefreshing(false);
    }

    private ArrayList<SearchModel> getCityList(){
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.city_list));
        ArrayList<SearchModel> items = new ArrayList<>();
        for(String city: listCity){
            items.add(new SearchModel(city));
        }
        return items;
    }

    private void getVeterianries(){
        veterinaryList.clear();
        progress_circular.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getVeterinaryByCity(selectedCity);

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
                                        Veterinary veterinary = new Gson().fromJson(jsonArray.get(i).toString(), Veterinary.class);
                                        // post.setImageUrl(apiBaseUrl+"uploadedFiles/"+post.getImageUrl());
                                        veterinaryList.add(veterinary);
                                    }
                                }else{
                                    veterinaryList.clear();
                                    Toast.makeText(context, "No posts to show", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        if(veterinaryList.size() > 0){
                            noListingsItemsLabel.setVisibility(View.GONE);
                        }else{
                            noListingsItemsLabel.setVisibility(View.VISIBLE);
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    //Todo display in list
                    setRecycler();
                    progress_circular.setVisibility(View.GONE);
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progress_circular.setVisibility(View.GONE);
                }
                //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progress_circular.setVisibility(View.GONE);
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void  setRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        veterinaryRecycler.setLayoutManager(layoutManager);
        veterinaryRVAdapter2 = new VeterinaryRVAdapter2(context, veterinaryList);
        veterinaryRecycler.setAdapter(veterinaryRVAdapter2);
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