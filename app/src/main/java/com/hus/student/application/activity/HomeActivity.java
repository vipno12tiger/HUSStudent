package com.hus.student.application.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hus.student.application.R;
import com.hus.student.application.object.AccountStudent;
import com.hus.student.application.object.Const;
import com.hus.student.application.object.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;


    private TextInputEditText edt_user, edt_password;
    private Button bt_login;


    private List<Teacher> teachers;

    private DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Init();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        bt_login.setOnClickListener(v -> {
            if (!checkNetWork()) {
                login(Objects.requireNonNull(edt_user.getText()).toString().trim(), Objects.requireNonNull(edt_password.getText()).toString().trim());
            }else {
                Toast.makeText(this, "Internet not connection", Toast.LENGTH_SHORT).show();
            }
        });

        load();

    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        edt_user = findViewById(R.id.edt_text);
        edt_password = findViewById(R.id.edt_password);

        bt_login = findViewById(R.id.bt_login);

        teachers = new ArrayList<>();

        refDb = FirebaseDatabase.getInstance().getReference();
    }

    private void login(String user, String password) {
        if (isTeacher(user, password)) {
            routerIfTeacher(user);
        } else {
            if (isStudent(user, password)) {
                routerIfStudent(user);
            }else {
                Toast.makeText(this, "User or Password Wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void routerIfTeacher(String user) {
        Intent intent = new Intent(HomeActivity.this, TeacherActivity.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void routerIfStudent(String user) {
        Intent intent = new Intent(HomeActivity.this, StudentActivity.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isStudent(String user, String password) {
        for (Teacher teacher : teachers) {
            for (AccountStudent accountStudent : teacher.getStudent()) {
                if ((accountStudent.getEmail().equals(user) || (accountStudent.getMsv().equals(user))) && (accountStudent.getPassword().equals(password))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isTeacher(String user, String password) {
        for (Teacher teacher : teachers) {
            if ((teacher.getEmail().equals(user) || (teacher.getUser().equals(user))) && (teacher.getPassword().equals(password))) {
                return true;
            }
        }
        return false;
    }


    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info == null) || (!info.isConnected());
    }


    private void load() {
        refDb.child(Const.TEACHER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    Log.e("AAA", dataSnapshot.toString());
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teachers.add(teacher);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}