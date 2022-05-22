package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
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
import com.example.new_puppy.R;
import com.example.new_puppy.adapter.PostRecyclerviewAdapter;
import com.example.new_puppy.model.Post;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class PostListingFragment extends Fragment {

    private ChipGroup chip_group_choice;
    private Chip chip_location, chip_facilities, chip_gender;
    private ImageSlider sliderPostImages;
    private CircularProgressIndicator progress_circular;
    private LinearLayout cardLayout;
    private SwipeRefreshLayout swipeContainer;
    private TextView noListingsItemsLabel;
    private Chip chip_add_cource;

    private SharedPreferences sharedPre;
    private static Dialog appCustomDialog;
    private ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;

    private HorizontalScrollView bg_btn_add;

    private static RecyclerView courceRecycler;
    private static PostRecyclerviewAdapter postRecyclerviewAdapter;

    private static Context context;
    private static Activity activity;

    private static final String  COURCES= "cources";
    private Uri image_data;

    private List<Post> postList = new ArrayList<Post>();

//    private StorageReference storageReference;
//    private DatabaseReference databaseReference;
    private ActivityResultLauncher<Intent> activityResultLaunch;

    public PostListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity =getActivity();
        progressDialog = new ProgressDialog(context);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

       // storageReference = FirebaseStorage.getInstance().getReference();
       // databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        fragmentManager = getActivity().getSupportFragmentManager();

       // initActivityResultLaunch();
        initCourceAddDialog();
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

        //  cardLayout = (LinearLayout) getView().findViewById(R.id.cardLayout);
        swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        noListingsItemsLabel = (TextView) getView().findViewById(R.id.noListingsItemsLabel);
        chip_add_cource = (Chip) getView().findViewById(R.id.chip_add_cource);
        bg_btn_add = (HorizontalScrollView) getView().findViewById(R.id.bg_btn_add);

        //Image slider : Docs https://github.com/denzcoskun/ImageSlideshow
        sliderPostImages= (ImageSlider) getView().findViewById(R.id.sliderPostImages);

        progress_circular = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

        List<SlideModel> slideModels= new ArrayList<>();
        //TODO: Can add more than one images as bellow
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101972979.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101937329.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653101931450.jpg?raw=true"));
        slideModels.add(new SlideModel("https://github.com/SJ-Software-Engineering-and-Development/puppy_grooming_and_selling_app/blob/main/public/FB_IMG_1653103765264.jpg?raw=true"));
        sliderPostImages.setImageList(slideModels, true);

        chip_add_cource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        getActivePost();
    }

    private void initCourceAddDialog() {
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
        TextInputEditText txt_name = appCustomDialog.findViewById(R.id.txt_name);
        TextInputEditText txt_nvq_level = appCustomDialog.findViewById(R.id.txt_nvq_level);
        TextInputEditText txt_language = appCustomDialog.findViewById(R.id.txt_language);
        TextInputEditText txt_duration = appCustomDialog.findViewById(R.id.txt_duration);
        Chip chip_add_image = appCustomDialog.findViewById(R.id.chip_add_image);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        // labelFormHeading.setText("Add New ...");

        chip_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
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
                if( txt_name.equals("") ||
                        txt_nvq_level.getText().toString().trim().equals("") ||
                        txt_language.getText().toString().trim().equals("") ||
                        txt_duration.getText().toString().trim().equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{

                    if(image_data == null){
                        showDialog(getActivity(),"Opps..!", "Choose an Image", ()->{});
                    }else{
                        //TODO: Display Confirm Dialog
                        showConfirmDialog(getActivity(),() -> {
                            //TODO: confirmCall
                            Post cource = new Post();
//                            cource.setName(txt_name.getText().toString().trim());
//                            cource.setNvq_level(txt_nvq_level.getText().toString().trim());
//                            cource.setLanguage(txt_language.getText().toString().trim());
//                            cource.setImageUrl("");
//
//                            saveCource(cource);

                            txt_name.setText("");
                            txt_nvq_level.setText("");
                            txt_language.setText("");
                            txt_duration.setText("");
                            image_data = null;
                        }, ()->{
                            //TODO: cancelCall
                            appCustomDialog.dismiss();
                            txt_name.setText("");
                            txt_nvq_level.setText("");
                            txt_language.setText("");
                            txt_duration.setText("");
                            image_data = null;
                        });
                    }

                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }

    private void getActivePost(){
        progressDialog.setTitle("Fetching Cources...");
        progressDialog.show();

        progress_circular.setVisibility(View.VISIBLE);
        postList.clear();
//public Post(int id, String title, String price, String description, String gender, String age, String date, String location, String imageUrl) {
        postList.add(new Post(1,"Dog for sale 1", "5000", "desc", "Male", "3", "2022-05-22", "Kaluthara", "https://i.ikman-st.com/labrador-puppies-kasl-registration-for-sale-gampaha/a9a6a4ca-f5e3-4e7d-b96c-64ce3f4acbdc/620/466/fitted.jpg"));
        postList.add(new Post(2,"Dog for sale 2", "7000", "desc", "Male", "3", "2022-05-22", "Kaluthara", "https://i.ikman-st.com/rottweiler-puppies-imported-blood-line-for-sale-gampaha-2/e7cf87ce-ecac-4d9c-9325-8aaa6182ad43/620/466/fitted.jpg"));
        postList.add(new Post(3,"Dog for sale 3", "25000", "desc", "Male", "3", "2022-05-22", "Kaluthara", "https://i.ikman-st.com/rottweiler-puppies-kasl-registered-for-sale-gampaha-11/0a5fc97a-2a76-4ac5-9dd8-575f34406a2e/620/466/fitted.jpg"));

        if(postList.size() > 0){
            setCourceRecycler();
        }else{
            Toast.makeText(context, "No Cources to show", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            progress_circular.setVisibility(View.GONE);
        }
        progressDialog.dismiss();
        progress_circular.setVisibility(View.GONE);
    }

    private void  setCourceRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        courceRecycler.setLayoutManager(layoutManager);
        postRecyclerviewAdapter = new PostRecyclerviewAdapter(context, postList);
        courceRecycler.setAdapter(postRecyclerviewAdapter);
    }

    private void selectFile() {
        // Intent intent = new Intent();
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //   intent.setType("application/pdf");
        //   intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLaunch.launch(gallery);
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