package com.hus.student.application.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hus.student.application.R;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Init();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if(fUser==null){
            finish();
        }

    }
    private void Init(){
        toolbar = findViewById(R.id.toolbar);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}