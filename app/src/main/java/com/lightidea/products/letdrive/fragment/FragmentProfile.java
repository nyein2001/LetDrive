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
        root_view = inflater.inflate(R.layout.profile_fragment,container, false);
        initComponent();
        new Handler().postDelayed(() -> {
            if (NetworkCheck.isConnected(getActivity())) {
                startAction();
            } else {
                showNoInternetDialog();
            }
        },300);
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
        if (firebaseUser != null) {
            Tools.displayImageCircle(requireContext(), driverImage, firebaseUser.getPhotoUrl());
            txtName.setText(firebaseUser.getDisplayName());
            txtEmail.setText(firebaseUser.getEmail());
        } else {
            Intent loginIntent = new Intent(getContext(), GoogleLoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.no_internet_title))
                .setMessage(getString(R.string.no_internet_message))
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (NetworkCheck.isConnected(getActivity())) {
                        startAction();
                    } else {
                        showNoInternetDialog();
                    }
                })
                .show();
    }

}
