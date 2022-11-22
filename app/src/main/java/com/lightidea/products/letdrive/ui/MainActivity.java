package com.lightidea.products.letdrive.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.lightidea.products.letdrive.fragment.FragmentProfile;
import com.lightidea.products.letdrive.utils.NetworkCheck;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        new Handler().postDelayed(() -> {
            if (NetworkCheck.isConnected(MainActivity.this)) {
                startAction();
            } else {
                showNoInternetDialog();
            }
        },300);

        loadFragment(new FragmentProfile());

    }

    public void initComponent() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public void startAction() {
        if (firebaseUser != null) {
//            txtDriverName.setText(firebaseUser.getDisplayName());
        } else {
            Intent loginIntent = new Intent(this,GoogleLoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_view,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.home) {
            loadFragment(new FragmentHome());
            return true;
        } else if (item_id == R.id.profile){
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
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (NetworkCheck.isConnected(MainActivity.this)) {
                        startAction();
                    } else {
                        showNoInternetDialog();
                    }
                })
                .show();
    }
}