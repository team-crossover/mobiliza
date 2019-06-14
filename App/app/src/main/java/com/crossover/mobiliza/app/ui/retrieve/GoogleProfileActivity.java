package com.crossover.mobiliza.app.ui.retrieve;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.crossover.mobiliza.app.R;

public class GoogleProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile);

        TextView name = findViewById(R.id.googleName);
        Intent intent = getIntent();
        if (intent.hasExtra("googleName")) {
            String googleName = intent.getStringExtra("googleName");
            name.setText(googleName);
        }  else {
            name.setText("Não posui");
        }

    }
}
