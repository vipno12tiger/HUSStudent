package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hus.student.application.R;
import com.hus.student.application.object.Const;
import com.hus.student.application.object.Person;
import com.hus.student.application.object.Teacher;

import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {

    private final static int CHANGE_IMAGE = 1000;

    private Toolbar toolbar;
    private ImageView iv_user;

    private String user;

    private Teacher teacher;
    private Person person;

    private DatabaseReference refDb;
    private StorageReference refStg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        user = getIntent().getStringExtra("user");
        if (user == null) {
            finish();
        }

        Init();

        iv_user.setOnClickListener(v -> {
            if (person == null) {
                routerUser();
            } else {
                routerChangeUser();
            }
        });

        loadUser();
    }

    private void routerChangeUser() {
        Intent intent = new Intent(TeacherActivity.this, ChangeUserActivity.class);
        intent.putExtra("UUID", person.getUUID());
        startActivityForResult(intent, CHANGE_IMAGE);
    }


    private void loadUser() {

        refDb.child(Const.TEACHER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Teacher user = snapshot.getValue(Teacher.class);
                    if (user != null) {
                        teacher = user;
                        toolbar.setSubtitle(teacher.getUser());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refDb.child(Const.PERSON).child(user).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (person.getName() != null && (!person.getName().equals(""))) {
                            toolbar.setTitle(person.getName());
                            refStg.child(Const.PERSON).child(person.getUUID() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                                    iv_user.setImageBitmap(bitmap);
                                }
                            });
                        }
                    } else {
                        routerUser();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void routerUser() {
        Intent intent = new Intent(TeacherActivity.this, UserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        iv_user = findViewById(R.id.iv_user);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_IMAGE) {
            if (resultCode == RESULT_OK) {
                refStg.child(Const.PERSON).child(person.getUUID() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                        iv_user.setImageBitmap(bitmap);
                    }
                });
            }
        }


    }
}