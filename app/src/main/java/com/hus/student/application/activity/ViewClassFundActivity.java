package com.hus.student.application.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.hus.student.application.adapter.CollectionAdapter;
import com.hus.student.application.adapter.PayAdapter;
import com.hus.student.application.module.Const;
import com.hus.student.application.module.OnClickCollection;
import com.hus.student.application.module.Transaction;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Class;
import com.hus.student.application.object.Collection;
import com.hus.student.application.object.Pay;

import java.util.ArrayList;
import java.util.List;

public class ViewClassFundActivity extends AppCompatActivity implements OnClickCollection {


    private String user, codeClass;
    private Class cl;

    private Toolbar toolbar;
    private RecyclerView rv_collection, rv_pay;

    private Button bt_add_collection, bt_add_pay;
    private TextView tv_total;

    private CollectionAdapter collectionAdapter;
    private PayAdapter payAdapter;



    private DatabaseReference refDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_fund);

        user = getIntent().getStringExtra("user");
        codeClass = getIntent().getStringExtra("codeClass");
        if (user == null || codeClass == null) {
            finish();
        }

        Init();

        toolbar.setNavigationOnClickListener(v -> finish());

        rv_pay.setLayoutManager(new LinearLayoutManager(ViewClassFundActivity.this, RecyclerView.VERTICAL, false));
        rv_pay.setAdapter(payAdapter);


        rv_collection.setLayoutManager(new LinearLayoutManager(ViewClassFundActivity.this, RecyclerView.VERTICAL, false));
        rv_collection.setAdapter(collectionAdapter);

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ViewClassFundActivity.this);
        View view = LayoutInflater.from(ViewClassFundActivity.this).inflate(R.layout.alert_collection, null);
        aBuilder.setView(view);
        AlertDialog dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        Button bt_confirm = view.findViewById(R.id.bt_confirm);
        EditText edt_title = view.findViewById(R.id.edt_title);
        EditText edt_amount = view.findViewById(R.id.edt_amount);

        bt_cancel.setOnClickListener(v -> dialog.dismiss());

        bt_confirm.setOnClickListener(v -> {
            try {
                if (edt_title.getText().toString().trim().length() > 0 && edt_amount.getText().toString().trim().length() > 0 && (Double.parseDouble(edt_amount.getText().toString()) > 0)) {
                    addCollection(edt_title.getText().toString(), Double.parseDouble(edt_amount.getText().toString()));
                }
                dialog.dismiss();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        });


        bt_add_collection.setOnClickListener(v -> dialog.show());

        loadClass();
        loadUser();
    }


    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        tv_total = findViewById(R.id.tv_total);
        bt_add_collection = findViewById(R.id.bt_add_collection);
        bt_add_pay = findViewById(R.id.bt_add_pay);

        rv_collection = findViewById(R.id.rv_collection);

        rv_pay = findViewById(R.id.rv_pay);


        collectionAdapter = new CollectionAdapter();
        collectionAdapter.setOnClickCollection(this);

        payAdapter = new PayAdapter();
        payAdapter.setOnClickCollection(this);

        refDb = FirebaseDatabase.getInstance().getReference();

    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        if (account.getRoot() != null) {
                            if (account.getRoot().equals(Const.TREASURER)) {
                                bt_add_collection.setVisibility(View.VISIBLE);
                                bt_add_pay.setVisibility(View.VISIBLE);
                            } else {
                                bt_add_collection.setVisibility(View.GONE);
                                bt_add_pay.setVisibility(View.GONE);
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

    private void loadClass() {

        refDb.child(Const.CLASS).child(codeClass).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    cl = snapshot.getValue(Class.class);
                    if (cl != null) {
                        collectionAdapter.setCollections(cl.getCollections());
                        payAdapter.setPays(cl.getPays());
                        showTotal(cl.getPays(), cl.getCollections());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void OnClick(View view, String type, int position) {
        if(type.equals(Const.COLLECTION)){
            Intent intent = new Intent(ViewClassFundActivity.this,ToDoListActivity.class);
            intent.putExtra("Api",Const.CLASS+"/"+codeClass+"/"+"collections"+"/"+position);
            intent.putExtra("codeClass",codeClass);
            intent.putExtra("position",""+position);
            intent.putExtra("user",user);
            startActivity(intent);
        }

    }


    private void addCollection(String title, double price) {
        Collection collection = new Collection(title, price);

        List<Collection> transactions = cl.getCollections();

        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(collection);
        cl.setCollections(transactions);
        refDb.child(Const.CLASS).child(codeClass).setValue(cl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ViewClassFundActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showTotal(List<Pay> pays, List<Collection> collections) {
        double sum = 0;
        if (pays != null) {
            for (Pay pay :pays) {
                sum -=pay.getAmountOfMoney();
            }
        }
        if (collections != null) {
            for (Collection collection :collections) {
               if(collection.getStudent()!=null){
                   sum +=(collection.getCollection()*collection.getStudent().size());
               }
            }
        }

        tv_total.setText("Total: " + sum);
    }

}