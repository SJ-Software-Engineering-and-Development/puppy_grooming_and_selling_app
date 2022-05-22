package com.example.new_puppy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.new_puppy.R;
import com.example.new_puppy.databinding.ActivityAdminageContainerBinding;

import com.example.new_puppy.fragment.AdminHomeFragment;
import com.example.new_puppy.fragment.MyProfileFragment;
import com.example.new_puppy.fragment.PostListingFragment;
import com.example.new_puppy.fragment.UserHomeFragment;
import com.example.new_puppy.utils.Navigation;

public class AdminageContainerActivity extends AppCompatActivity {

    ActivityAdminageContainerBinding binding;
    private SharedPreferences sharedPre;
    private String user_nic ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_adminage_container);


        sharedPre= getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        user_nic= AppSharedPreferences.getData(sharedPre,"user_nic");

        //TODO:::::::::::::::::::::::IMPORTANT::::::::::::::::::::::
        /**
         * Make sure enable viewBingg at build.gradle file
         *     buildFeatures{
         *         viewBinding true
         *     }
         */
        binding = ActivityAdminageContainerBinding.inflate(getLayoutInflater());
        //Avoid Status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        //Start up view
        replaceFragment(new PostListingFragment());


        //Avoid changing of selected icon's color
        binding.bottomNavigationView.setItemIconTintList(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new PostListingFragment());
                    break;
                case R.id.dashboard:
                    replaceFragment(new AdminHomeFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new MyProfileFragment());
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        switch (Navigation.currentScreen){
            case "f1":
            case "f2":
                // replaceFragment(new GNHomeFragment());
                break;
            case "f3":
                //  replaceFragment(new AddDisasterFragment());
                break;
            case "AdminHomeFragment":
                // super.onBackPressed();
                break;
        }
    }
}