package com.example.new_puppy.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import com.example.new_puppy.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.new_puppy.databinding.ActivityUserPageContainerBinding;
import com.example.new_puppy.fragment.MyProfileFragment;
import com.example.new_puppy.fragment.PostListingFragment;
import com.example.new_puppy.fragment.UserBookingListFragment;
import com.example.new_puppy.fragment.UserHomeFragment;
import com.example.new_puppy.fragment.UserPostListFragment;
import com.example.new_puppy.fragment.UsersListFragment;
import com.example.new_puppy.utils.Navigation;

public class UserPageContainerActivity extends AppCompatActivity {

    ActivityUserPageContainerBinding binding;
    private SharedPreferences sharedPre;
    private String user_nic ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_user_page_container);

        sharedPre= getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        user_nic= AppSharedPreferences.getData(sharedPre,"user_nic");

        //TODO:::::::::::::::::::::::IMPORTANT::::::::::::::::::::::
        /**
         * Make sure enable viewBingg at build.gradle file
         *     buildFeatures{
         *         viewBinding true
         *     }
         */
        binding = ActivityUserPageContainerBinding.inflate(getLayoutInflater());
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
                    replaceFragment(new UserHomeFragment());
                    break;
                case R.id.my_booking:
                    if(!user_nic.equals(""))
                       replaceFragment(new UserBookingListFragment());
                    break;
                case R.id.my_posts:
                    replaceFragment(new UserPostListFragment());
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
            case "UserBookingListFragment":
            case "UserPostListFragment":
                replaceFragment(new UserHomeFragment());
                break;
            case "ViewPostFragment":
            case "GOTO_PostListingFragment":
                replaceFragment(new PostListingFragment());
                break;
            case "GOTO_UserPostListFragment":
                replaceFragment(new UsersListFragment());
                break;
            case "UserHomeFragment":
                // super.onBackPressed();
                break;
        }
    }


}