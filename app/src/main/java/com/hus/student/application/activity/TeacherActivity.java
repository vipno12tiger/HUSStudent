package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class TeacherActivity extends AppCompatActivity implements OnClickItemRecyclerView {

    private final static int TIME_DELAY = 1000;

    private Toolbar toolbar;
    private RecyclerView rv_selection;
    private EditText edt_user;
    private AlertDialog dialog;

    private Snackbar snackbar;

    private SelectionAdapter adapter;

    private String user, codeClass;
    private List<Selection> selections;
    private Account account;
    private List<Account> accounts;

    private Class cl;


    private SharedPreferences refShared;

    private DatabaseReference refDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        user = getIntent().getStringExtra("user");
        codeClass = getIntent().getStringExtra("codeClass");
        if (user == null || codeClass == null) {
            finish();
        }
        Init();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(TeacherActivity.this);
        View view = LayoutInflater.from(TeacherActivity.this).inflate(R.layout.alert_add_student, null);
        aBuilder.setView(view);
        Button bt_add = view.findViewById(R.id.bt_add);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        edt_user = view.findViewById(R.id.edt_user);
        dialog = aBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        bt_cancel.setOnClickListener(v -> dialog.dismiss());
        bt_add.setOnClickListener(v -> {
            if (edt_user.getText().toString().trim().length() > 0) {
                addStudent(edt_user.getText().toString().trim());
            }
        });


        selections.add(new Selection(R.drawable.add, "Add student"));
        selections.add(new Selection(R.drawable.user, "Profile"));
        selections.add(new Selection(R.drawable.event, "Events"));
        selections.add(new Selection(R.drawable.notepad, "List student"));
        selections.add(new Selection(R.drawable.dollar,"Class fund"));
        selections.add(new Selection(R.drawable.exit, "Log out"));

        rv_selection.setLayoutManager(new GridLayoutManager(TeacherActivity.this, 2));
        rv_selection.setAdapter(adapter);
        adapter.setSelections(selections);

        setSupportActionBar(toolbar);

        toolbar.setSubtitle(user);


        loadUser();
        netWork();

    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        rv_selection = findViewById(R.id.rv_selection);
        ConstraintLayout lo_login = findViewById(R.id.layout);

        snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);

        accounts = new ArrayList<>();
        selections = new ArrayList<>();
        adapter = new SelectionAdapter();
        adapter.setOnClickItemRecyclerView(this);

        refShared = getSharedPreferences("user", MODE_PRIVATE);
        refDb = FirebaseDatabase.getInstance().getReference();
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

    private void routerSearch() {
        Intent intent = new Intent(TeacherActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void routerProfile() {
        Intent intent = new Intent(TeacherActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    private void logOut() {
        SharedPreferences.Editor editor = refShared.edit();
        editor.clear().apply();
        Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void loadUser() {
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
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        for (int i = 0; i < accounts.size(); i++) {
                            if (account.getUser().equals(accounts.get(i).getUser())) {
                                accounts.set(i, account);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        for (int i = 0; i < accounts.size(); i++) {
                            if (account.getUser().equals(accounts.get(i).getUser())) {
                                accounts.remove(i);
                                break;
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
                case R.drawable.add:
                    addStudent();
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
    private void routerFund(){
        Intent intent = new Intent(TeacherActivity.this,ViewClassFundActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("codeClass",codeClass);
        startActivity(intent);
    }

    private void routerViewListStudent(){
        Intent intent = new Intent(TeacherActivity.this,ViewListStudentActivity.class);
        intent.putExtra("codeClass",codeClass);
        intent.putExtra("root","teacher");
        startActivity(intent);
    }

    private void addStudent() {
        if (checkNetWork()) {
            dialog.show();
        }
    }

    private boolean checkNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && (info.isConnected());
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

    private void updateClass() {
        if (checkNetWork()) {
            if (cl != null) {
                refDb.child(Const.CLASS).child(codeClass).setValue(cl);
            }
        }
    }

    private void addStudent(String user) {
        if (checkExit(user)) {
            Account account = new Account(user, user, Const.STUDENT, this.account.getCodeClass());
            refDb.child(Const.ACCOUNT).child(user).setValue(account);
            if (cl == null) {
                cl = new Class();
            }
            List<String> student = cl.getStudents();
            if (student == null) {
                student = new ArrayList<>();
            }
            student.add(user);
            cl.setStudents(student);
            updateClass();
            edt_user.setText("");
        } else {
            Toast.makeText(this, "User Exits", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkExit(String user) {
        for (Account account : accounts) {
            if (account.getUser().equals(user)) {
                return false;
            }
        }
        return true;
    }

}