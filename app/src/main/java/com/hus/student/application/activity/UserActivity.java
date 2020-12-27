package com.hus.student.application.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.object.Const;
import com.hus.student.application.object.Person;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    private Person person;


    private String user;


    private Toolbar toolbar;

    private TextInputEditText edt_name;
    private Button bt_confirm;

    private DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = getIntent().getStringExtra("user");
        if (user == null) {
            finish();
        }

        Init();


        toolbar.setNavigationOnClickListener(v -> finish());

        bt_confirm.setOnClickListener(v -> {
            if(Objects.requireNonNull(edt_name.getText()).toString().length()>0){
                if(person==null){
                    person = new Person();
                }
                person.setName(edt_name.getText().toString());
            }
            refDb.child(Const.PERSON).child(user).setValue(person);
        });

        loadUser();

    }

    private void Init(){
        bt_confirm = findViewById(R.id.bt_confirm);
        edt_name = findViewById(R.id.edt_name);
        toolbar = findViewById(R.id.toolbar);

        refDb = FirebaseDatabase.getInstance().getReference();
    }


    private void loadUser() {
        refDb.child(Const.PERSON).child(user).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}