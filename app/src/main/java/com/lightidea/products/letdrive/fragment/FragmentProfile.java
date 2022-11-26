package com.lightidea.products.letdrive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.ui.GoogleLoginActivity;
import com.lightidea.products.letdrive.utils.NetworkCheck;
import com.lightidea.products.letdrive.utils.Tools;

public class FragmentProfile extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private View root_view;
    private ImageView driverImage;
    private TextView txtName;
    private TextView txtEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.profile_fragment, container, false);
        initComponent();
        startAction();
        return root_view;
    }

    public void initComponent() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        driverImage = root_view.findViewById(R.id.driver_photo);
        txtName = root_view.findViewById(R.id.txt_name);
        txtEmail = root_view.findViewById(R.id.txt_email);
    }

    public void startAction() {
            Tools.displayImageCircle(requireContext(), driverImage, firebaseUser.getPhotoUrl());
            txtName.setText(firebaseUser.getDisplayName());
            txtEmail.setText(firebaseUser.getEmail());
    }

}
