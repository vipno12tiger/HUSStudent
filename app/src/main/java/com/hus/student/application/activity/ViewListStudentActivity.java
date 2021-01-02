package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.adapter.StudentAdapter;
import com.hus.student.application.module.Const;
import com.hus.student.application.module.OnClickItemRecyclerView;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Class;
import com.hus.student.application.object.Person;

public class ViewListStudentActivity extends AppCompatActivity implements OnClickItemRecyclerView {


    private String codeClass;
    private String root;


    private Toolbar toolbar;

    private RecyclerView rv_student;

    private Class aClass;

    private StudentAdapter adapter;


    private DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_student);

        codeClass = getIntent().getStringExtra("codeClass");

        root = getIntent().getStringExtra("root");


        if (root == null || codeClass == null) {
            finish();
        }

        Init();

        toolbar.setNavigationOnClickListener(v -> finish());


        rv_student.setLayoutManager(new LinearLayoutManager(ViewListStudentActivity.this, RecyclerView.VERTICAL, false));
        rv_student.setAdapter(adapter);

        loadClass();


    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        rv_student = findViewById(R.id.rv_student);


        adapter = new StudentAdapter();
        adapter.setOnClickItemRecyclerView(this);


        refDb = FirebaseDatabase.getInstance().getReference();
    }


    private void loadClass() {
        refDb.child(Const.CLASS).child(codeClass).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    aClass = snapshot.getValue(Class.class);
                    if (aClass != null) {
                        if (aClass.getStudents() != null) {
                            adapter.setStudents(aClass.getStudents());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClickItem(View view, int position) {
        if (root.equals(Const.TEACHER)) {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(ViewListStudentActivity.this);

            View vDialog = LayoutInflater.from(ViewListStudentActivity.this).inflate(R.layout.alert_change_root_student, null);
            aBuilder.setView(vDialog);


            AlertDialog dialog = aBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            RadioGroup rg_root = vDialog.findViewById(R.id.rg_root);
            RadioButton rb_student = vDialog.findViewById(R.id.rb_student);
            RadioButton rb_monitor = vDialog.findViewById(R.id.rb_monitor);
            RadioButton rb_treasurer = vDialog.findViewById(R.id.rb_treasurer);

            Button bt_cancel = vDialog.findViewById(R.id.bt_cancel);
            Button bt_confirm = vDialog.findViewById(R.id.bt_confirm);

            bt_cancel.setOnClickListener(v -> dialog.dismiss());
            bt_confirm.setOnClickListener(v -> {
                changeRoot(aClass.getStudents().get(position), rg_root.getCheckedRadioButtonId());
                dialog.dismiss();
            });

            refDb.child(Const.ACCOUNT).child(aClass.getStudents().get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Account account = snapshot.getValue(Account.class);
                        if (account != null) {
                            if (account.getRoot() != null) {
                                switch (account.getRoot()) {
                                    case Const.STUDENT:
                                        rb_student.setChecked(true);
                                        break;
                                    case Const.MONITOR:
                                        rb_monitor.setChecked(true);
                                        break;
                                    case Const.TREASURER:
                                        rb_treasurer.setChecked(true);
                                        break;
                                    default:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            dialog.show();

        }
    }

    @SuppressLint("NonConstantResourceId")
    private void changeRoot(String studentID, int root) {
        switch (root) {
            case R.id.rb_student:
                refDb.child(Const.ACCOUNT).child(studentID).child("root").setValue(Const.STUDENT).addOnCompleteListener(task -> Toast.makeText(ViewListStudentActivity.this, "Success!", Toast.LENGTH_SHORT).show());
                break;
            case R.id.rb_monitor:
                refDb.child(Const.ACCOUNT).child(studentID).child("root").setValue(Const.MONITOR).addOnCompleteListener(task -> Toast.makeText(ViewListStudentActivity.this, "Success!", Toast.LENGTH_SHORT).show());
                break;
            case R.id.rb_treasurer:
                refDb.child(Const.ACCOUNT).child(studentID).child("root").setValue(Const.TREASURER).addOnCompleteListener(task -> Toast.makeText(ViewListStudentActivity.this, "Success!", Toast.LENGTH_SHORT).show());
                break;
        }

    }
}