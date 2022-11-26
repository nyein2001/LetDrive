package com.lightidea.products.letdrive.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lightidea.products.letdrive.R;
import com.lightidea.products.letdrive.utils.NetworkCheck;

public class GoogleLoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient googleSignInClient;
    private final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        initComponent();
        if (firebaseUser == null) {
            new Handler().postDelayed(() -> {
                if (NetworkCheck.isConnected(GoogleLoginActivity.this)) {
                    loginProcess();
                } else {
                    showNoInternetDialog();
                }
            }, 300);
        } else {
            Intent loginIntent = new Intent(this, GoogleLoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void initComponent() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    public void loginProcess() {
        findViewById(R.id.google_sign_in).setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("onActivityResult : ", account.getId());
                LoginWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void LoginWithGoogle(String tokenId) {
        Log.d("LoginWithGoogle : ", tokenId);
        AuthCredential credential = GoogleAuthProvider.getCredential(tokenId, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        goToMainActivity();
                    } else {
                        Log.d("NotSuccessError : ", task.toString());
                    }
                })
                .addOnFailureListener(e -> Log.d("onFailure : ", e.getMessage()));
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.no_internet_title))
                .setMessage(R.string.no_internet_message)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (NetworkCheck.isConnected(GoogleLoginActivity.this)) {
                        loginProcess();
                    } else {
                        showNoInternetDialog();
                    }
                })
                .show();
    }
}