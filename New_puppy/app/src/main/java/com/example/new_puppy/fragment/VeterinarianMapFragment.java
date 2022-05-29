package com.example.new_puppy.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.model.MapLocation;
import com.example.new_puppy.model.SearchModel;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
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

public class VeterinarianMapFragment extends Fragment {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;

    private ProgressDialog progressDialog;
    private Chip chip_choose_city;

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private GoogleMap gMap;

    private String selectedCity="";
    private static List<Veterinary> veterinaryList = new ArrayList<Veterinary>();

    public VeterinarianMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity = getActivity();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_veterinarian_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chip_choose_city =(Chip) getView().findViewById(R.id.chip_choose_city);

        supportMapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
       // supportMapFragment.getMapAsync(this);

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

        getVeterianries();
    }

    private void removeLocations(){
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                //remove previose markers from map
                gMap.clear();
            }
        });
    }

    private void showtLocation(List<MapLocation> locations) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            System.out.println("==================Permition not granted FOR ACCESS_FINE_LOCATION");
         //   return;
        }
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                //remove previose markers from map
                gMap.clear();
                ArrayList<LatLng> locationArrayList = getLocations();
                // Draw multiple markers.
                    /*
                    for (int i = 0; i < locationArrayList.size(); i++) {
                        // Add marker to each location of our array list.
                        gMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Marker"));
                        // Zoom our camera on map.
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(28.0f));
                        // Move our camera to the specific location.
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
                    }
                    */

                for (MapLocation place: locations) {
                    // Add marker to each location of our array list.
                    gMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getLocationName()));
                    // Zoom our camera on map.
                  //  gMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    // Move our camera to the specific location.
                  //  gMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                }
                if(locations.size() > 0)
                    gMap.animateCamera((CameraUpdateFactory.newLatLngZoom(locations.get(0).getLatLng(), 8)));
            }
        });
    }

    private void getVeterianries(){
        veterinaryList.clear();
        progressDialog.show();

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
                                    List<MapLocation> locations = new ArrayList<>();
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                        Veterinary veterinary = new Gson().fromJson(jsonArray.get(i).toString(), Veterinary.class);
                                        // post.setImageUrl(apiBaseUrl+"uploadedFiles/"+post.getImageUrl());
                                       // veterinaryList.add(veterinary);

                                        locations.add(
                                                new MapLocation(
                                                        new LatLng(
                                                                Double.parseDouble(veterinary.getLatitude()),
                                                                Double.parseDouble(veterinary.getLongitude())),
                                                        veterinary.getTitle()));
                                    }
                                    if(locations.size()>0) showtLocation(locations);
                                    else removeLocations();
                                }else{
                                    removeLocations();
                                    Toast.makeText(context, "No posts to show", Toast.LENGTH_LONG).show();
                                }
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
                    removeLocations();
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

    private ArrayList<SearchModel> getCityList(){
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.city_list));
        ArrayList<SearchModel> items = new ArrayList<>();
        for(String city: listCity){
            items.add(new SearchModel(city));
        }
        return items;
    }

    private ArrayList<LatLng> getLocations(){

        ArrayList<LatLng> locationArrayList = new ArrayList<>();

        LatLng sydney = new LatLng(-34, 151);
        LatLng TamWorth = new LatLng(-31.083332, 150.916672);
        LatLng NewCastle = new LatLng(-32.916668, 151.750000);
        LatLng Brisbane = new LatLng(-27.470125, 153.021072);

        // on below line we are adding our
        // locations in our array list.
        locationArrayList.add(sydney);
        locationArrayList.add(TamWorth);
        locationArrayList.add(NewCastle);
        locationArrayList.add(Brisbane);

        return  locationArrayList;
    }

}