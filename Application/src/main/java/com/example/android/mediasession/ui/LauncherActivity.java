package com.example.android.mediasession.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.mediasession.R;

public class LauncherActivity extends AppCompatActivity {

    Button bGoToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        bGoToMain = findViewById(R.id.bGoToMain);
        bGoToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            }
        });
    }
}
