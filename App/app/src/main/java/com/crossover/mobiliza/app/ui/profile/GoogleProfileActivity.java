package com.crossover.mobiliza.app.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.crossover.mobiliza.app.R;

public class GoogleProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView userType;
    private ImageView profilePicture;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile);

        name = findViewById(R.id.googleNome);
        email = findViewById(R.id.googleEmail);
        profilePicture = findViewById(R.id.googlePicture);
        userType = findViewById(R.id.accountType);
        deleteBtn = findViewById(R.id.googleDeleteButton);

        Intent intent = getIntent();
        if (intent.hasExtra("googleName")) {
            String gName = intent.getStringExtra("googleName");
            name.setText(gName);
        } else {
            name.setText("");
        }

        if (intent.hasExtra("googleEmail")) {
            String gEmail = intent.getStringExtra("googleEmail");
            email.setText(gEmail);
        } else {
            email.setText("");
        }

        if (intent.hasExtra("userType")) {
            String type = intent.getStringExtra("userType");
            userType.setText(type);
        } else {
            userType.setText("");
        }

        if (intent.hasExtra("googlePicture")) {
            String pic = intent.getStringExtra("googlePicture");
            Glide.with(this).load(pic).into(profilePicture);
        }

        deleteBtn.setOnClickListener(this::onDelete);
    }

    private void onDelete(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("shouldDeleteAccount", true);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
