package com.crossover.mobiliza.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mDisconnectButton;
    private TextView mUserNameTextView;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout();
        setupSignIn();
    }

    private void setupLayout() {
        // Set the layout and find the views.
        setContentView(R.layout.activity_login);
        mSignInButton = findViewById(R.id.sign_in_button);
        mSignOutButton = findViewById(R.id.sign_out_button);
        mDisconnectButton = findViewById(R.id.disconnect_button);
        mUserNameTextView = findViewById(R.id.user_name_text);

        // Set the dimensions and colors of the sign-in button.
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        // Listen to the buttons's clicks.
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mDisconnectButton.setOnClickListener(this);
    }

    private void setupSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // To request additional scopes, see:
        // https://developers.google.com/identity/sign-in/android/additional-scopes
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    protected void updateUI(GoogleSignInAccount account) {
        // If there's no account (i.e. is unauthenticated)
        if (account == null) {
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.GONE);
            mUserNameTextView.setText(getString(R.string.user_name_label, getString(R.string.user_name_guest)));
        }
        // If there is an account (i.e. is authenticated)
        else {
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
            mDisconnectButton.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(getString(R.string.user_name_label, account.getDisplayName()));
        }
    }

    @Override
    public void onClick(View v) {
        // Do something different based on which button was pressed.
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    /**
     * Starts an intent to display the Google Sign-in prompt,
     * which asks for the user's account and permissions.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Signs out the current account.
     */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    /**
     * Revokes access for the current account.
     */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the signIn intent.
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            // Couldn't sign in, show unauthenticated UI.
            updateUI(null);
        }
    }
}
