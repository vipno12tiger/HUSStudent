package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.object.Person;

public class UpdateProfileActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private String user;
    private Person person;


    private DatabaseReference refDb;


    private EditText edt_name, edt_email, edt_phone;
    private TextView tv_day;
    private Button bt_picker, bt_confirm;

    private AlertDialog dialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        user = getIntent().getStringExtra("user");
        if (user == null) {
            finish();
        }

        Init();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(UpdateProfileActivity.this);
        View view = LayoutInflater.from(UpdateProfileActivity.this).inflate(R.layout.alert_picker_date, null);
        aBuilder.setView(view);
        DatePicker picker = view.findViewById(R.id.dp_date);
        Button bt_confirm = view.findViewById(R.id.bt_confirm);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        dialog = aBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        bt_cancel.setOnClickListener(v -> dialog.dismiss());

        bt_confirm.setOnClickListener(v -> {
            tv_day.setText(picker.getDayOfMonth() + "/" + (picker.getMonth()+1) + "/" + picker.getYear());
            dialog.dismiss();
        });


        tv_day.setOnClickListener(v -> {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
        bt_picker.setOnClickListener(v -> {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
        this.bt_confirm.setOnClickListener(v -> {
            if ((edt_name.getText().toString().trim().length() > 0) && (edt_email.getText().toString().trim().length() > 0) && (edt_phone.getText().toString().trim().length() > 0) && (tv_day.getText().toString().trim().length() > 0)) {
                updateUser(edt_name.getText().toString(), edt_email.getText().toString(), edt_phone.getText().toString(), tv_day.getText().toString());
            }
        });

        toolbar.setTitle(user);

        toolbar.setNavigationOnClickListener(v -> finish());

        loadUser();
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);

        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_name = findViewById(R.id.edt_name);

        bt_picker = findViewById(R.id.bt_picker);
        bt_confirm = findViewById(R.id.bt_confirm);

        tv_day = findViewById(R.id.tv_day);


        refDb = FirebaseDatabase.getInstance().getReference();
    }

    private void loadUser() {
        refDb.child(Const.PERSON).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (person.getDay() != null) {
                            tv_day.setText(person.getDay());
                        }
                        if (person.getEmail() != null) {
                            edt_email.setText(person.getEmail());
                        }
                        if (person.getPhone() != null) {
                            edt_phone.setText(person.getPhone());
                        }
                        if (person.getName() != null) {
                            edt_name.setText(person.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUser(String name, String email, String phone, String day) {
        if (person == null) {
            person = new Person();
        }
        person.setName(name);
        person.setDay(day);
        person.setPhone(phone);
        person.setEmail(email);
        refDb.child(Const.PERSON).child(user).setValue(person).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}