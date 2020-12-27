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
import com.hus.student.application.object.Const;
import com.hus.student.application.object.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
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
            } else {
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
        if (!isSuccessful(user, password)) {
            Toast.makeText(this, "User or Password Wrong", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isSuccessful(String user, String password) {
        for (int i = 0; i < teachers.size(); i++) {
            if ((teachers.get(i).getUser().equals(user)
                    || (teachers.get(i).getEmail().equals(user) && (!teachers.get(i).getEmail().equals(""))))
                    && (teachers.get(i).getPassword().equals(password))) {
                Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user", teachers.get(i).getUser());
                startActivity(intent);
                finish();
                return true;
            } else {
                for (int j = 0; j < teachers.get(i).getStudent().size(); j++) {
                    if ((teachers.get(i).getStudent().get(j).getMsv().equals(user)
                            || (teachers.get(i).getStudent().get(j).getEmail().equals(user) && (teachers.get(i).getStudent().get(j).getEmail() != null) && (!teachers.get(i).getStudent().get(j).getEmail().equals(""))))
                            && (teachers.get(i).getStudent().get(j).getPassword().equals(password))) {
                        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("user", teachers.get(i).getStudent().get(j).getMsv());
                        intent.putExtra("root", teachers.get(i).getStudent().get(j).getRoot());
                        startActivity(intent);
                        finish();
                        return true;
                    }
                }
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
                if(dataSnapshot.getValue()!=null){
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    if(teacher!=null){
                        teachers.set(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())),teacher);
                    }
                }
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