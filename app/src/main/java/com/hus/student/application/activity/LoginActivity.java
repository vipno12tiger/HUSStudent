package com.hus.student.application.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.object.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private final static int TIME_DELAY = 1000;

    private Snackbar snackbar;


    private TextInputEditText edt_user, edt_password;
    private Button bt_login;

    private List<Account> accounts;

    private DatabaseReference refDb;

    private SharedPreferences refShared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Init();

        edt_user.setText(refShared.getString("user",""));

        bt_login.setOnClickListener(v -> {
            bt_login.setEnabled(false);
            if (checkNetWork()) {
                if (Objects.requireNonNull(edt_user.getText()).toString().length() > 0 && Objects.requireNonNull(edt_password.getText()).toString().length() > 0) {
                    login(edt_user.getText().toString(), edt_password.getText().toString());
                }else {
                    bt_login.setEnabled(true);
                    Toast.makeText(this, "Field Is Empty", Toast.LENGTH_SHORT).show();
                }
            }else {
                bt_login.setEnabled(true);
            }
        });

        loadAccount();

        netWork();
    }

    private void netWork(){
        if(!checkNetWork()){
            if(!snackbar.isShown()){
                snackbar.show();
            }
        }else {
            snackbar.dismiss();
        }
        new Handler().postDelayed(this::netWork,TIME_DELAY);
    }

    private void Init() {
        edt_user = findViewById(R.id.edt_user);
        edt_password = findViewById(R.id.edt_password);
        bt_login = findViewById(R.id.bt_login);
        ConstraintLayout lo_login = findViewById(R.id.lo_login);

        snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);

        accounts = new ArrayList<>();

        refDb = FirebaseDatabase.getInstance().getReference();


        refShared = getSharedPreferences("user", MODE_PRIVATE);

    }


    private void loadAccount() {
        refDb.child(Const.ACCOUNT).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        accounts.add(account);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        for (Account ac : accounts) {
                            if (account.getUser().equals(ac.getUser())) {
                                accounts.remove(ac);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void login(String user, String password) {
        if (!isSuccess(user, password)) {
            Toast.makeText(this, "User or password wrong", Toast.LENGTH_SHORT).show();
            bt_login.setEnabled(true);
        }
    }

    private boolean isSuccess(String user, String password) {
        SharedPreferences.Editor editor = refShared.edit();
        for (Account account : accounts) {
            if (user.equals(account.getUser()) && (password.equals(account.getPassword()))) {
                editor.clear();
                editor.putString("user", user).apply();
                if (account.getCodeClass() == null) {
                    account.setCodeClass(UUID.randomUUID().toString());
                    refDb.child(Const.ACCOUNT).child(user).setValue(account);
                }
                new Handler().postDelayed(() -> {
                    if (account.getRoot().equals(Const.TEACHER)) {
                        routerTeacher(user, account.getCodeClass());
                    } else {
                        routerStudent(user, account.getCodeClass());
                    }
                },TIME_DELAY);
                return true;
            }
        }
        return false;
    }

    private void routerTeacher(String user, String codeClass) {
        Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("codeClass", codeClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void routerStudent(String user, String codeClass) {
        Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("codeClass", codeClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

}