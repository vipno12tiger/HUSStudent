package com.hus.student.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hus.student.application.R;
import com.hus.student.application.object.Const;

import java.io.IOException;
import java.util.Objects;

public class ChangeUserActivity extends AppCompatActivity {

    private final static int PICK_IMAGE = 200;

    private String UUID;

    private StorageReference refStg;

    private ImageView iv_user;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        UUID = getIntent().getStringExtra("UUID");
        if (UUID == null) {
            finish();
        }

        Init();

        toolbar.setNavigationOnClickListener(v -> finish());

        iv_user.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/");
            startActivityForResult(intent,PICK_IMAGE);
        });

        loadUser();

    }

    private void loadUser() {
        refStg.child(Const.PERSON).child(UUID + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                iv_user.setImageBitmap(bitmap);
            }
        });
    }

    private void Init(){
        toolbar = findViewById(R.id.toolbar);
        iv_user = findViewById(R.id.iv_user);


        refStg = FirebaseStorage.getInstance().getReference();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    refStg.child(Const.PERSON).child(UUID+".png").putFile(data.getData()).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            setResult(RESULT_OK);
                            Toast.makeText(ChangeUserActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                                iv_user.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}