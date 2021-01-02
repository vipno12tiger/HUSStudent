package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.adapter.SelectionAdapter;
import com.hus.student.application.module.Const;
import com.hus.student.application.module.OnClickItemRecyclerView;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Class;
import com.hus.student.application.object.Person;
import com.hus.student.application.object.Selection;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity implements OnClickItemRecyclerView {

    private final static int TIME_DELAY = 1000;

    private String user, codeClass;

    List<Selection> selections;

    private Toolbar toolbar;
    private Snackbar snackbar;
    private RecyclerView rv_selection;

    private SelectionAdapter adapter;


    private Account account;
    private Class cl;

    private SharedPreferences refShared;
    private DatabaseReference refDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        user = getIntent().getStringExtra("user");
        codeClass = getIntent().getStringExtra("codeClass");
        if (user == null || codeClass == null) {
            finish();
        }
        Init();
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(user);

        rv_selection.setLayoutManager(new GridLayoutManager(StudentActivity.this, 2));
        rv_selection.setAdapter(adapter);
        adapter.setOnClickItemRecyclerView(this);

        selections.add(new Selection(R.drawable.user, "Profile"));
        selections.add(new Selection(R.drawable.event, "Events"));
        selections.add(new Selection(R.drawable.notepad, "List student"));
        selections.add(new Selection(R.drawable.dollar, "Class fund"));
        selections.add(new Selection(R.drawable.exit, "Log out"));

        adapter.setSelections(selections);


        loadUser();
        netWork();
    }

    private void Init() {
        selections = new ArrayList<>();

        adapter = new SelectionAdapter();
        toolbar = findViewById(R.id.toolbar);
        rv_selection = findViewById(R.id.rv_selection);


        ConstraintLayout layout = findViewById(R.id.layout);
        snackbar = Snackbar.make(layout, R.string.not_network, BaseTransientBottomBar.LENGTH_SHORT);

        refShared = getSharedPreferences("user", MODE_PRIVATE);

        refDb = FirebaseDatabase.getInstance().getReference();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClickItem(View view, int position) {
        if (checkNetWork()) {
            switch (selections.get(position).getID()) {
                case R.drawable.user:
                    routerProfile();
                    break;
                case R.drawable.event:
                    break;
                case R.drawable.exit:
                    logOut();
                    break;
                case R.drawable.notepad:
                    routerViewListStudent();
                    break;
                case R.drawable.dollar:
                    routerFund();
                    break;
            }
        }
    }

    private void routerFund() {
        Intent intent = new Intent(StudentActivity.this, ViewClassFundActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("codeClass", codeClass);
        startActivity(intent);
    }

    private void routerViewListStudent() {
        Intent intent = new Intent(StudentActivity.this, ViewListStudentActivity.class);
        intent.putExtra("codeClass", codeClass);
        intent.putExtra("root", Const.STUDENT);
        startActivity(intent);
    }


    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    account = snapshot.getValue(Account.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refDb.child(Const.PERSON).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        toolbar.setTitle(person.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refDb.child(Const.CLASS).child(codeClass).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    cl = snapshot.getValue(Class.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            routerSearch();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void logOut() {
        SharedPreferences.Editor editor = refShared.edit();
        editor.clear().apply();
        Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void routerSearch() {
        Intent intent = new Intent(StudentActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void routerProfile() {
        Intent intent = new Intent(StudentActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    private void netWork() {

        if (!checkNetWork()) {
            if (!snackbar.isShown()) {
                snackbar.show();
            }
        } else {
            snackbar.dismiss();
        }
        new Handler().postDelayed(this::netWork, TIME_DELAY);

    }


    private boolean checkNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && (info.isConnected());
    }
}