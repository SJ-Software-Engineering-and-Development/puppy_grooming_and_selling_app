package com.example.new_puppy.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    private SharedPreferences sharedPre;
    private String user_nic ="";

    private CircleImageView userImage;
    private TextView tvOwnerName, tvOwnerEmail,tvRole;
    private TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private Button btnLogout, btnGotoSettings,btnGoBack, btnUpdatePassoword;
    private MaterialCardView cardViewProfile, cardViewSettings;
    private static Dialog appProgressDialog;
    private static Context context;


    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPre= getActivity().getSharedPreferences("puppy_app",0);
        user_nic= AppSharedPreferences.getData(sharedPre,"user_nic");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userImage = (CircleImageView) getView().findViewById(R.id.imageDisaster);
        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        tvRole = (TextView) getView().findViewById(R.id.tvRole);
        btnGotoSettings = (Button) getView().findViewById(R.id.btnGotoSettings);
        btnGoBack = (Button) getView().findViewById(R.id.btnGoBack);
        btnUpdatePassoword = (Button) getView().findViewById(R.id.btnUpdatePassoword);
        cardViewProfile = (MaterialCardView) getView().findViewById(R.id.cardViewProfile);
        cardViewSettings = (MaterialCardView) getView().findViewById(R.id.cardViewSettings);
        txtCurrentPassword = (TextInputEditText) getView().findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (TextInputEditText) getView().findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = (TextInputEditText) getView().findViewById(R.id.txtConfirmNewPassword);
        btnLogout = (Button) getView().findViewById(R.id.btnLogout);
    }
}