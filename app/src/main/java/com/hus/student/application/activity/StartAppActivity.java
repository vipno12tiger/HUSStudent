package com.hus.student.application.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hus.student.application.R;

public class StartAppActivity extends AppCompatActivity {
    private final static int TIME_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(StartAppActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        },TIME_DELAY);

    }



}