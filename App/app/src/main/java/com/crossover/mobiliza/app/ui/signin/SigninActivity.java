package com.crossover.mobiliza.app.ui.signin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.remote.service.AppServices;
import com.crossover.mobiliza.app.data.remote.service.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SigninActivity.class.getSimpleName();
    private static final int RC_SIGN_IN_ONG = 9001;
    private static final int RC_SIGN_IN_VOLUNTARIO = 9002;

    private SignInButton mSignInOngButton;
    private SignInButton mSignInVoluntarioButton;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setupLayout();
        setupSignIn();
    }

    private void setupLayout() {
        mSignInOngButton = findViewById(R.id.sign_in_ong_button);
        mSignInVoluntarioButton = findViewById(R.id.sign_in_voluntario_button);

        mSignInOngButton.setOnClickListener(this);
        mSignInOngButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInOngButton.setColorScheme(SignInButton.COLOR_LIGHT);

        mSignInVoluntarioButton.setOnClickListener(this);
        mSignInVoluntarioButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInVoluntarioButton.setColorScheme(SignInButton.COLOR_LIGHT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_ong_button:
                signIn(true);
                break;
            case R.id.sign_in_voluntario_button:
                signIn(false);
                break;
        }
    }

    private void signIn(boolean asOng) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, asOng ? RC_SIGN_IN_ONG : RC_SIGN_IN_VOLUNTARIO);
    }

    private void setupSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the signIn intent.
        if (requestCode == RC_SIGN_IN_ONG || requestCode == RC_SIGN_IN_VOLUNTARIO) {
            Log.i(TAG, "onActivityResult:finished " + requestCode);
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task, requestCode == RC_SIGN_IN_ONG);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask, boolean asOng) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_message));
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            GoogleSignInAccount account = completedTask.getResult();
            String googleIdToken = account.getIdToken();
            Call<User> call = AppServices
                    .getInstance(this)
                    .createService(UserService.class)
                    .findByGoogleTokenId(googleIdToken, asOng);
            AppServices.runCallAsync(call,
                    // On success
                    user -> {
                        progressDialog.dismiss();
                        Log.i(TAG, "handleSignInResult:success userId=" + user.getId());
                        this.finish();
                    },
                    // On failure
                    message -> {
                        mGoogleSignInClient.signOut();
                        progressDialog.dismiss();
                        Toast.makeText(this, this.getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "handleSignInResult:failedGetUser " + message);
                    });
        } catch (Exception e) {
            mGoogleSignInClient.signOut();
            progressDialog.dismiss();
            Toast.makeText(this, this.getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
            Log.w(TAG, "handleSignInResult:failed ", e);
        }
    }
}
