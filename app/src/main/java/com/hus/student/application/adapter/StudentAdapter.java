package com.hus.student.application.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.module.OnClickItemRecyclerView;
import com.hus.student.application.object.Account;
import com.hus.student.application.object.Person;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.Student> {


    private List<String> students;


    private final DatabaseReference refDb;
    private final StorageReference refStg;

    private OnClickItemRecyclerView onClickItemRecyclerView;


    public void setOnClickItemRecyclerView(OnClickItemRecyclerView onClickItemRecyclerView) {
        this.onClickItemRecyclerView = onClickItemRecyclerView;
    }

    public StudentAdapter() {

        refStg = FirebaseStorage.getInstance().getReference();

        refDb = FirebaseDatabase.getInstance().getReference();

        students = new ArrayList<>();
    }


    public void setStudents(List<String> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Student onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_student, parent, false);
        return new Student(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Student holder, int position) {

        holder.tv_id.setText(students.get(position));
        refDb.child(Const.ACCOUNT).child(students.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Account account = snapshot.getValue(Account.class);
                    if (account != null) {
                        holder.tv_root.setText(account.getRoot());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refDb.child(Const.PERSON).child(students.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (person.getName() != null) {
                            holder.tv_name.setText(person.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refStg.child(Const.PERSON).child(students.get(position) + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                    holder.iv_user.setImageBitmap(bitmap);
                }
            }
        });


        holder.itemView.setOnClickListener(v -> onClickItemRecyclerView.onClickItem(v, position));

    }

    @Override
    public int getItemCount() {

        if (students != null) {
            return students.size();
        }
        return 0;
    }

    public static class Student extends RecyclerView.ViewHolder {


        public TextView tv_name, tv_id, tv_root;
        public ImageView iv_user;

        public Student(@NonNull View itemView) {
            super(itemView);
            setIsRecyclable(false);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_root = itemView.findViewById(R.id.tv_root);


            iv_user = itemView.findViewById(R.id.iv_user);

        }
    }
}
