package com.crossover.mobiliza.app.ui.retrieve;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crossover.mobiliza.app.R;
import com.crossover.mobiliza.app.ui.main.MainActivity;

public class GoogleProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView userType;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile);

        name = findViewById(R.id.googleNome);
        email = findViewById(R.id.googleEmail);
        profilePicture = findViewById(R.id.googlePicture);
        userType = findViewById(R.id.accountType);

        Intent intent = getIntent();
        if (intent.hasExtra("googleName")) {
            String gName = intent.getStringExtra("googleName");
            name.setText(gName);
        }  else {
            name.setText("");
        }

        if (intent.hasExtra("googleEmail")) {
            String gEmail = intent.getStringExtra("googleEmail");
            email.setText(gEmail);
        }  else {
            email.setText("");
        }

        if (intent.hasExtra("userType")) {
            String type = intent.getStringExtra("userType");
            userType.setText(type);
        }  else {
            userType.setText("");
        }

        if (intent.hasExtra("googlePicture")) {
            String pic = intent.getStringExtra("googlePicture");
            Glide.with(this).load(pic).into(profilePicture);
        }

    }
}
