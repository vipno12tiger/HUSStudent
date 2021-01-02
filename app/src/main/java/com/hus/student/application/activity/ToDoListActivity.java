package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.adapter.StudentCollectionAdapter;
import com.hus.student.application.module.Const;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Class;
import com.hus.student.application.object.Collection;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private String codeClass,position;


    private StudentCollectionAdapter adapter;
    private String user;


    private Toolbar toolbar;
    private RecyclerView rv_collection;

    private DatabaseReference refDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);


        String api = getIntent().getStringExtra("Api");
        position = getIntent().getStringExtra("position");
        codeClass = getIntent().getStringExtra("codeClass");
        user = getIntent().getStringExtra("user");
        Init();
        adapter.setApi(api);
        rv_collection.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(v -> finish());
        load();
    }

    private void Init(){
        toolbar = findViewById(R.id.toolbar);

        adapter = new StudentCollectionAdapter();
        rv_collection = findViewById(R.id.rv_collection);
        rv_collection.setLayoutManager(new LinearLayoutManager(ToDoListActivity.this,RecyclerView.VERTICAL,false));

        refDb = FirebaseDatabase.getInstance().getReference();
    }

    private void load(){
        refDb.child(Const.ACCOUNT).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Account account = snapshot.getValue(Account.class);
                    if(account!=null){
                        if(account.getRoot()!= null){
                            adapter.setRoot(account.getRoot());
                        }
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
                if(snapshot.getValue()!=null){
                    Class cl = snapshot.getValue(Class.class);
                    if(cl!=null){
                        adapter.setStudent(cl.getStudents());
                        if(cl.getCollections().get(Integer.parseInt(position))!=null){
                            if(cl.getCollections().get(Integer.parseInt(position)).getStudent()!=null){
                                adapter.setCollection(cl.getCollections().get(Integer.parseInt(position)).getStudent());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}