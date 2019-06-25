package com.crossover.mobiliza.app.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.data.local.entity.Evento;
import com.crossover.mobiliza.app.data.local.entity.User;
import com.crossover.mobiliza.app.data.remote.service.AppServices;
import com.crossover.mobiliza.app.data.remote.service.UserService;
import com.crossover.mobiliza.app.ui.event.AddEventActivity;
import com.crossover.mobiliza.app.ui.main.adapters.SectionsPagerAdapter;
import com.crossover.mobiliza.app.ui.profile.ProfileOngActivity;
import com.crossover.mobiliza.app.ui.profile.ProfileVoluntarioActivity;
import com.crossover.mobiliza.app.ui.profile.GoogleProfileActivity;
import com.crossover.mobiliza.app.ui.signin.SigninActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private static User mUser;
    private Evento event;
    private GoogleSignInAccount googleAccount;
    private static FloatingActionButton fab;

    public static User getUser(){
        if (mUser != null) {
            return mUser;
        } else {
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayout();
        setupToolbar();
        setupAuth();
        setupFab();

        event = new Evento();


    }


    @Override
    protected void onStart() {
        super.onStart();
        refreshAuth();
    }

    private void setupFab() {
        //Create floating action button
        fab = findViewById(R.id.fabAddEvent);
        fab.setOnClickListener(v -> Snackbar.make(v, "Alguma ação", Snackbar.LENGTH_SHORT).show());
        fab.hide();
    }

    private void setupLayout() {
        // Create progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_message));
        mProgressDialog.setCancelable(false);

        // Create tabs
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    // ------------------------------------
    // TOOLBAR
    // ------------------------------------

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mUser == null) {
            menu.add(0, 1, Menu.NONE, R.string.action_signin);
        } else {
            if (mUser.isLastUsedAsOng()) {
                menu.add(0, 2, Menu.NONE, R.string.action_edit_profile);
                menu.add(0, 4, Menu.NONE, R.string.action_new_event);
            }
            menu.add(0, 5, Menu.NONE, R.string.action_google_info);
            menu.add(0, 3, Menu.NONE, R.string.action_signout);
        }

        //TODO: Aprimorar essa parte.

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                showSignIn();
                return true;
            case 2:
                showProfile();
                return true;
            case 3:
                signOut();
                return true;
            case 4:
                showAddEvent();
                return true;
            case 5:
                showGoogleAccountInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSignIn() {
        Intent myIntent = new Intent(this, SigninActivity.class);
        this.startActivity(myIntent);
    }

    private void showProfile() {
        Intent myIntent;
        if (mUser.isLastUsedAsOng()) {
            myIntent = new Intent(this, ProfileOngActivity.class);
            myIntent.putExtra("idOng", mUser.getIdOng());
        } else {
            myIntent = new Intent(this, ProfileVoluntarioActivity.class);
            myIntent.putExtra("idVoluntario", mUser.getIdVoluntario());
        }
        myIntent.putExtra("googleIdToken", mUser.getGoogleIdToken());
        this.startActivity(myIntent);
    }

    private void showAddEvent() {
        Intent myIntent = new Intent(this, AddEventActivity.class);
        myIntent.putExtra("idEvent", event.getId());
        myIntent.putExtra("googleIdToken", mUser.getGoogleIdToken());
        this.startActivity(myIntent);
    }

    private void showGoogleAccountInfo() {
        Intent myIntent = new Intent(this, GoogleProfileActivity.class);
        StringBuilder nome = new StringBuilder();

        // Available info only!
        if (googleAccount == null) {
            return;
        }

        if (googleAccount.getGivenName() != null) {
            nome.append(googleAccount.getGivenName());
        }

        if (googleAccount.getFamilyName() != null) {
            nome.append(" ");
            nome.append(googleAccount.getFamilyName());
        }

        if (googleAccount.getEmail() != null) {
            myIntent.putExtra("googleEmail", googleAccount.getEmail());
        }

        if (googleAccount.getPhotoUrl() != null) {
            myIntent.putExtra("googlePicture", googleAccount.getPhotoUrl().toString());
        }

        if (mUser.isLastUsedAsOng()) {
            myIntent.putExtra("userType", "Conta tipo ONG");
        } else {
            myIntent.putExtra("userType", "Conta tipo Voluntário");
        }

        myIntent.putExtra("googleName", nome.toString());
        this.startActivity(myIntent);
    }


    // ------------------------------------
    // AUTHENTICATION
    // ------------------------------------

    private void setupAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void refreshAuth() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the userService has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the userService silently and findById a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mProgressDialog.show();
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, task -> {
                    try {
                        if (task.isSuccessful())
                            updateUserFromAccount(task.getResult());
                        else
                            mProgressDialog.dismiss();
                    } catch (Exception e) {
                        mProgressDialog.dismiss();
                        Log.e(TAG, "refreshAuth:failed " + e.getMessage());
                    }
                });
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> updateUser(null));
        Toast.makeText(this, "Você foi deslogado", Toast.LENGTH_LONG).show();
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> updateUser(null));
    }

    private void updateUserFromAccount(GoogleSignInAccount account) {
        if (account == null) {
            updateUser(null);
            return;
        }
        Call<User> call = AppServices
                .getInstance(this)
                .createService(UserService.class)
                .findByGoogleTokenId(account.getIdToken());
        AppServices.runCallAsync(call,
                // On success
                user -> {
                    Log.e(TAG, "refreshAuth:success userId=" + user.getId());
                    updateUser(user);
                    googleAccount = account;
                },
                // On failure
                message -> {
                    Log.e(TAG, "refreshAuth:failed " + message);
                    updateUser(null);
                });
    }

    private void updateUser(User user) {
        mProgressDialog.dismiss();
        mUser = user;
        invalidateOptionsMenu(); // Forces the toolbar to refresh its buttons

        if (user != null) {
            Log.i(TAG, "updateUser: " + user.isLastUsedAsOng());
            if (user.isLastUsedAsOng()) {
                // TODO: Show userService's name or profile picture somewhere...
            } else {
                // TODO: Show userService's name or profile picture somewhere...
            }
        }
    }
}