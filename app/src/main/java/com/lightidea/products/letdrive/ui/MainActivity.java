package com.lightidea.products.letdrive.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.fragment.FragmentHome;
import com.lightidea.products.letdrive.fragment.FragmentMap;
import com.lightidea.products.letdrive.fragment.FragmentProfile;
import com.lightidea.products.letdrive.utils.NetworkCheck;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();

        if (firebaseUser != null) {
            new Handler().postDelayed(() -> {
                if (NetworkCheck.isConnected(this)) {
                    loadFragment(new FragmentHome());
                } else {
                    showNoInternetDialog();
                }
            }, 300);
        } else {
            Intent loginIntent = new Intent(this, GoogleLoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    public void initComponent() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_view, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.home) {
            loadFragment(new FragmentHome());
            return true;
        }else if (item_id == R.id.map) {
            loadFragment(new FragmentMap());
            return true;
        }else if (item_id == R.id.profile) {
            loadFragment(new FragmentProfile());
            return true;
        } else {
            return false;
        }
    }

    public void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.no_internet_title))
                .setMessage(getString(R.string.no_internet_message))
                .setCancelable(false)
                .setPositiveButton("Yes", null)
                .show();
    }
}