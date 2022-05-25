package com.example.new_puppy.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.new_puppy.R;
import com.google.android.material.card.MaterialCardView;

public class AdminHomeFragment extends Fragment {

    private SharedPreferences sharedPre;
    private String apiBaseUrl = "";
    static FragmentManager fragmentManager;

    MaterialCardView card_posts, card_bookings, card_users;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        card_posts = (MaterialCardView) getView().findViewById(R.id.card_posts);
        card_bookings = (MaterialCardView) getView().findViewById(R.id.card_bookings);
        card_users = (MaterialCardView) getView().findViewById(R.id.card_users);

        card_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFragment(new AdminPostListFragment());
            }
        });

        card_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  gotoFragment(new AddUsersFragment(true,myDivisionID));
            }
        });

        card_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  gotoFragment(new AddUsersFragment(true,myDivisionID));
            }
        });
    }

    private void gotoFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}