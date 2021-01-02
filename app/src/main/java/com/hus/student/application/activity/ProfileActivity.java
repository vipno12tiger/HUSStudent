package com.hus.student.application.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Person;

import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private final static int PICK_IMAGE = 2000;


    private String user;
    private Person person;

    private Toolbar toolbar;

    private TextView tv_user, tv_email, tv_id, tv_day, tv_phone;
    private Button bt_change_profile;
    private ImageView iv_user;

    private SharedPreferences refShared;

    private DatabaseReference refDb;
    private StorageReference refStg;


    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = getIntent().getStringExtra("user");
        if (user == null) {
            finish();
        }
        Init();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ProfileActivity.this);
        aBuilder.setView(R.layout.alert_load);
        dialog = aBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (!user.equals(refShared.getString("user", ""))) {
            bt_change_profile.setVisibility(View.GONE);
        }

        bt_change_profile.setOnClickListener(v -> routerChangeProfile());

        tv_id.setText(user);

        toolbar.setNavigationOnClickListener(v -> finish());

        iv_user.setOnClickListener(v -> {
            if (checkNetWork()) {
                pickImage();
            }
        });

        loadUser();
    }

    @SuppressLint("CutPasteId")
    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        tv_user = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_id = findViewById(R.id.tv_id);
        tv_day = findViewById(R.id.tv_day);
        tv_phone = findViewById(R.id.tv_phone);
        bt_change_profile = findViewById(R.id.bt_change_profile);
        iv_user = findViewById(R.id.iv_user);

        refShared = getSharedPreferences("user", MODE_PRIVATE);

        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();

    }

    private void routerChangeProfile() {
        Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private boolean checkNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getData() != null) {
                        try {
                            dialog.show();
                            refStg.child(Const.PERSON).child(user + ".png").putFile(data.getData()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                                        iv_user.setImageBitmap(bitmap);
                                        Toast.makeText(ProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } catch (IOException e) {
                                        dialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }else {
                                    dialog.dismiss();
                                    Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        if (account.getRoot().equals(Const.TEACHER)) {
                            tv_day.setVisibility(View.GONE);
                        }
                    }
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
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (person.getName() != null) {
                            tv_user.setText(person.getName());
                        }
                        if (person.getPhone() != null) {
                            tv_phone.setText(person.getPhone());
                        }
                        if (person.getEmail() != null) {
                            tv_email.setText(person.getEmail());
                        }
                        if (person.getDay() != null) {
                            tv_day.setText(String.valueOf(person.getDay()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refStg.child(Const.PERSON).child(user + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                iv_user.setImageBitmap(bitmap);
            }
        });
    }
}