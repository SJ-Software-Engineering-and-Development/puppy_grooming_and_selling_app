package com.example.new_puppy.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.activity.UserPageContainerActivity;
import com.example.new_puppy.model.Breed;
import com.example.new_puppy.model.PostStatus;
import com.example.new_puppy.R;
import com.example.new_puppy.adapter.PostRecyclerviewAdapter;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.SearchBreedModel;
import com.example.new_puppy.model.SearchModel;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.BreedStorage;
import com.example.new_puppy.utils.FileUtils;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostListingFragment extends Fragment {

    private ChipGroup chip_group_choice;
    private ImageSlider sliderPostImages;
    private CircularProgressIndicator progress_circular;
    private LinearLayout cardLayout;
    private SwipeRefreshLayout swipeContainer;
    private TextView noListingsItemsLabel;
    private Chip chip_add_cource, chip_booking, chip_veterinary;

    private SharedPreferences sharedPre;
    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;

    private HorizontalScrollView bg_btn_add;
    private Button btnFullView, btnFullViewClose;
    private TextInputEditText txtSearch;

    private static RecyclerView courceRecycler;
    private static PostRecyclerviewAdapter postRecyclerviewAdapter;
    public ActivityResultLauncher<Intent> activityResultLaunch;

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;

    Bitmap selectedImageBitmap = null;
    public String selectedImage = "";
    public String fileDialogSelectedOption = "";

    private List<Post> postList = new ArrayList<Post>();
    private ArrayList<Breed> breedList = new ArrayList<Breed>();

    private ArrayList<SearchBreedModel> breedNameList = new ArrayList<SearchBreedModel>();
    private ArrayList<Integer> breedIDList = new ArrayList<Integer>();

    private CharSequence search="";
    private String selectedGender = "";
    private String selectedLocation = "";
    private String selectedBreedName = "";
    private int selectedBreedId = 0;
    private int this_user_login_id = 0;
    CharSequence[] options = {"Camera", "Gallery", "Cancel"};

    public PostListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        this.this_user_login_id= Integer.parseInt(AppSharedPreferences.getData(sharedPre,"login_id"));
        String user_nic =AppSharedPreferences.getData(sharedPre,"user_nic");
        fragmentManager = getActivity().getSupportFragmentManager();

       // initActivityResultLaunch();
        initPostAddDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_listing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courceRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);
        swipeContainer  = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);

        btnFullView = (Button) getView().findViewById(R.id.btnFullView);
        btnFullViewClose = (Button) getView().findViewById(R.id.btnFullViewClose);
        txtSearch = (TextInputEditText) getView().findViewById(R.id.txtSearch);

       //  addOnScrollListenerPostRecycler();
        noListingsItemsLabel = (TextView) getView().findViewById(R.id.noListingsItemsLabel);

        chip_add_cource = (Chip) getView().findViewById(R.id.chip_add_cource);
        chip_booking = (Chip) getView().findViewById(R.id.chip_booking);
        chip_veterinary = (Chip) getView().findViewById(R.id.chip_veterinary);
        bg_btn_add = (HorizontalScrollView) getView().findViewById(R.id.bg_btn_add);

        //Todo: Image slider : Docs https://github.com/denzcoskun/ImageSlideshow
        sliderPostImages= (ImageSlider) getView().findViewById(R.id.sliderPostImages);

        btnFullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderPostImages.setVisibility(View.GONE);
                btnFullView.setVisibility(View.GONE);
                btnFullViewClose.setVisibility(View.VISIBLE);
            }
        });

        btnFullViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderPostImages.setVisibility(View.VISIBLE);
                btnFullView.setVisibility(View.VISIBLE);
                btnFullViewClose.setVisibility(View.GONE);
            }
        });

        progress_circular = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

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

        setImagestoSliderPost();

        chip_add_cource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        chip_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new BookingSlotsFragment());
            }
        });

        chip_veterinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new UserVeterinaryListFragment());
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                postRecyclerviewAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getActivePosts();
        getBreeds();
    }

    //Todo: Post Onclick
    public static void listItemOnClick(int postId){
        Fragment fragment = new ViewPostFragment(postId, "PostListingFragment");
        replaceFragment(fragment);
    }

    private static void replaceFragment(Fragment fragment){
        //TODO: Goto View ViewDisasterFragmentViewDisasterFragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setImagestoSliderPost(){
        List<SlideModel> slideModels= new ArrayList<>();
        //TODO: Can add more than one images as bellow
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101972979.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101937329.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101931450.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653103765264.jpg?raw=true"));
        sliderPostImages.setImageList(slideModels, true);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
            getActivePosts();
            swipeContainer.setRefreshing(false);
    }

    private void addOnScrollListenerPostRecycler(){
        courceRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    //Scrolled Right
                } else if (dx < 0) {
                    //Scrolled Left
                } else {
                    //No Horizontal Scrolled
                }
                if (dy > 0) {
                    //Scrolled Downwards
                    System.out.println("== Scrolled Downwards =="+ sliderPostImages.getVisibility());
                    if(sliderPostImages.getVisibility() == View.GONE){
                      // sliderPostImages.setVisibility(View.GONE);
                        System.out.println("==in visible ==");
                    }else{
                        sliderPostImages.setVisibility(View.GONE);
                        System.out.println("== visible ==");
                    }

                } else if (dy < 0) {
                    //Scrolled Upwards
                    if(sliderPostImages.getVisibility() == View.GONE){
                         sliderPostImages.setVisibility(View.VISIBLE);
                    }

                } else {
                    //No Vertical Scrolled
                }
            }
        });
    }

    private void initPostAddDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_post);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txt_title = appCustomDialog.findViewById(R.id.txt_title);
        TextInputEditText txt_price = appCustomDialog.findViewById(R.id.txt_price);
        TextInputEditText txt_description = appCustomDialog.findViewById(R.id.txt_description);
        TextInputEditText txt_age = appCustomDialog.findViewById(R.id.txt_age);

        Chip chip_add_image = appCustomDialog.findViewById(R.id.chip_add_image);
        Chip chip_location = appCustomDialog.findViewById(R.id.chip_location);
        Chip chip_breed = appCustomDialog.findViewById(R.id.chip_breed);
        Chip chip_male = appCustomDialog.findViewById(R.id.chip_male);
        Chip chip_female = appCustomDialog.findViewById(R.id.chip_female);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        // labelFormHeading.setText("Add New ...");

        chip_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

        chip_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Male";
                chip_male.setChipBackgroundColorResource(R.color.colorAppPrimary);
                chip_male.setTextColor(Color.parseColor("#FFFFFF"));
                chip_female.setChipBackgroundColorResource(R.color.colorLightGray);
                chip_female.setTextColor(Color.parseColor("#000000"));
            }
        });

        chip_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Female";
                chip_female.setChipBackgroundColorResource(R.color.colorAppPrimary);
                chip_female.setTextColor(Color.parseColor("#FFFFFF"));
                chip_male.setChipBackgroundColorResource(R.color.colorLightGray);
                chip_male.setTextColor(Color.parseColor("#000000"));
            }
        });

        chip_location.setOnClickListener(new View.OnClickListener() {
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
                                chip_location.setText(item.getTitle());
                                selectedLocation = item.getTitle();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        chip_breed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(getActivity(), "Search...",
                        "Search  breed name here...?", null, getBreedNameList(),
                        new SearchResultListener<SearchBreedModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchBreedModel item, int position) {
                                // If filtering is enabled, [position] is the index of the item in the filtered result, not in the unfiltered source
                                chip_breed.setText(item.getTitle());
                                selectedBreedName = item.getTitle();
                                selectedBreedId = item.getId();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( txt_title.equals("") ||
                        txt_price.getText().toString().trim().equals("") ||
                        txt_description.getText().toString().trim().equals("") ||
                        txt_age.getText().toString().trim().equals("") ||
                        selectedBreedName.equals("") ||
                        selectedGender.equals("") ||
                        selectedLocation.equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{

                    if(selectedImage == "" || selectedImageBitmap == null){
                        showDialog(getActivity(),"Opps..!", "Choose an Image", ()->{});
                    }else{
                        //TODO: Display Confirm Dialog
                        showConfirmDialog(getActivity(),() -> {
                            //TODO: confirmCall
                            Post post = new Post();
                            post.setTitle(txt_title.getText().toString().trim());
                            post.setPrice(txt_price.getText().toString().trim());
                            post.setDescription(txt_description.getText().toString().trim());
                            post.setAge(txt_age.getText().toString().trim());
                            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            post.setDate(today);
                            post.setLocation(selectedLocation);
                            post.setGender(selectedGender);
                            post.setStatus(PostStatus.pending);
                            post.setBreed_id(selectedBreedId);
                            post.setOwner_id(this_user_login_id);
                            //Todo: Image has to be upload and will set it on server it selves.
                              post.setImageUrl("");

                            savePost(post);

                            txt_title.setText("");
                            txt_price.setText("");
                            txt_description.setText("");
                            txt_age.setText("");
                            selectedLocation="";
                            selectedBreedId=0;
                            selectedBreedName="";
                            selectedGender="";
                            selectedImage = "";
                            selectedImageBitmap = null;
                        }, ()->{
                            //TODO: cancelCall
                            appCustomDialog.dismiss();
                            txt_title.setText("");
                            txt_price.setText("");
                            txt_description.setText("");
                            txt_age.setText("");
                            selectedGender="";
                            selectedLocation="";
                            selectedBreedId=0;
                            selectedBreedName="";
                            selectedImage = "";
                            selectedImageBitmap = null;
                        });
                    }

                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::

        initActivityResultLaunch();
    }

    //Todo: Choosen image result
    private void initActivityResultLaunch(){
        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Chip chip_pick_image = appCustomDialog.findViewById(R.id.chip_add_image);
                        if (result.getResultCode() == -1) {
                            switch (fileDialogSelectedOption) {
                                case "Camera":
                                    //TODO : do stuff when image captured by camera
                                    Bitmap image = (Bitmap) result.getData().getExtras().get("data");
                                    selectedImageBitmap =image;
                                    // imageView.setImageBitmap(bitmap);
                                    selectedImage = FileUtils.getPath(context, getImageUri(context, image));
                                    chip_pick_image.setText("Image Added");
                                    // userImage.setImageBitmap(image);  // <-- Show image in ImageView
                                    break;
                                case "Gallery":
                                    // TODO : do stuff when image Selected from gallery
                                    Uri imageUri = result.getData().getData();
                                    selectedImage = FileUtils.getPath(context, imageUri);
                                    chip_pick_image.setText("Image Added");
                                    //  userImage.setImageURI(imageUri);// <-- Show image in ImageView
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                                        selectedImageBitmap =bitmap;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                            }
                        } else if (result.getResultCode() == 0) { // Not selected
                            // TODO : do stuff when image not Selected
                            chip_pick_image.setText("Pick an Image");
                            // try {
                            //   userImage.setImageResource(R.drawable.ic_baseline_camera_alt_24);
                            // }catch (Exception e){e.printStackTrace();}
                        }
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

    private ArrayList<SearchBreedModel> getBreedNameList(){
        if(BreedStorage.breeds !=null){
            if(BreedStorage.breeds.size()!=0)this.breedList = BreedStorage.breeds;
            else getBreeds();
        }else{
            getBreeds();
        }

        ArrayList<SearchBreedModel> items = new ArrayList<>();
        for(Breed breed: breedList){
            breedIDList.add(breed.getId());
            breedNameList.add(new SearchBreedModel(breed.getId(), breed.getBreed()));
            items.add(new SearchBreedModel(breed.getId(), breed.getBreed()));
        }
        return items;
    }

    // Todo: ref https://demonuts.com/android-upload-image-to-server-using-retrofit/
    private void savePost(Post post){
        progressDialog.setTitle("Saving...");
        progressDialog.show();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface
                .submitPost(post.getTitle(),
                        post.getPrice(),
                        post.getDescription(),
                        post.getGender(),
                        post.getAge(),
                        post.getDate(),
                        post.getLocation(),
                        imgname,
                        encodedImage,
                        post.getStatus().toString(),
                        String.valueOf(post.getBreed_id()),
                        String.valueOf(post.getOwner_id()));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                       try{
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                showDialog(context, "Done..!", "Post submited", () -> {
                                });
                            }else{
                                showDialog(context, "Opps..!", "Could not submit", () -> {
                                });
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    appCustomDialog.dismiss();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                    progressDialog.dismiss();
                }
              //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                showDialog(context, "Opps..!", "Time out \nCould not connect", () -> {
                });
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void getActivePosts(){
        progress_circular.setVisibility(View.VISIBLE);
        postList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getPosts("active");

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
                                       post.setImageUrl(apiBaseUrl+"post/uploadedFiles/"+post.getImageUrl());
                                       postList.add(post);
                                   }
                               }else{
                                   noListingsItemsLabel.setVisibility(View.VISIBLE);
                                   Toast.makeText(context, "No Post to show", Toast.LENGTH_LONG).show();
                               }
                           }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
                        if(postList.size() > 0){
                            setPostRecycler();
                            noListingsItemsLabel.setVisibility(View.GONE);
                        }else{
                            noListingsItemsLabel.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "No Post to show", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progress_circular.setVisibility(View.GONE);
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                    progress_circular.setVisibility(View.GONE);
                }
                //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progress_circular.setVisibility(View.GONE);
                showDialog(context, "Opps..!", "Time out \nCould not connect", () -> {
                });
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void getBreeds(){
        progressDialog.show();
        breedList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getBreeds();

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
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Breed breed = new Gson().fromJson(jsonArray.get(i).toString(), Breed.class);
                                        breedList.add(breed);
                                    }
                                    BreedStorage.breeds = breedList;
                                }else{
                                    noListingsItemsLabel.setVisibility(View.VISIBLE);
                                    Toast.makeText(context, "No Breeds to show", Toast.LENGTH_LONG).show();
                                    Toast.makeText(context, "Contact admin", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
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
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                    progressDialog.dismiss();
                }
                //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                showDialog(context, "Opps..!", "Time out \nCould not connect", () -> {
                });
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void  setPostRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        courceRecycler.setLayoutManager(layoutManager);
        postRecyclerviewAdapter = new PostRecyclerviewAdapter(context, postList);
        courceRecycler.setAdapter(postRecyclerviewAdapter);
    }

    private void selectFile() {
        requirePermission();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Camera")) {
                    Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLaunch.launch(takePic);
                    fileDialogSelectedOption = "Camera";
                } else if (options[which].equals("Gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLaunch.launch(gallery);
                    fileDialogSelectedOption = "Gallery";
                } else {
                    dialog.dismiss();
                    Chip chip_pick_image = appCustomDialog.findViewById(R.id.chip_add_image);
                    chip_pick_image.setText("Pick an Image");
                }
            }
        });
        builder.show();
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "myImage", "");
        return Uri.parse(path);
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