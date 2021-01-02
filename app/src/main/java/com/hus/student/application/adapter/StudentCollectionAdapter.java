package com.hus.student.application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hus.student.application.R;
import com.hus.student.application.module.Const;
import com.hus.student.application.object.Person;

import java.util.ArrayList;
import java.util.List;

public class StudentCollectionAdapter extends RecyclerView.Adapter<StudentCollectionAdapter.StudentCollectionHolder> {

    private List<String> student;

    private List<String> collection;

    private final DatabaseReference refDb;


    private String Api,root;

    public void setRoot(String root) {
        this.root = root;
        notifyDataSetChanged();
    }

    public void setApi(String api) {
        Api = api;
    }

    public StudentCollectionAdapter() {
        refDb = FirebaseDatabase.getInstance().getReference();
    }

    private void updateCollection(){
        refDb.child(Api).child("student").setValue(collection);
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
        notifyDataSetChanged();
    }

    public void setStudent(List<String> student) {
        this.student = student;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public StudentCollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_student_collection,parent,false);
        return new StudentCollectionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentCollectionHolder holder, int position) {
        holder.tv_id.setText(student.get(position));
        if(collection!=null){
            for (String stg: collection) {
                if(stg.equals(student.get(position))){
                    holder.rb_collection.setChecked(true);
                }
            }
        }
        refDb.child(Const.PERSON).child(student.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Person person = snapshot.getValue(Person.class);
                    if(person!=null){
                        if(person.getName()!=null){
                            holder.tv_name.setText(person.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.rb_collection.setEnabled(root.equals(Const.TREASURER));

        holder.rb_collection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                if(collection==null){
                    collection = new ArrayList<>();
                }
                collection.add(student.get(position));
                updateCollection();
            }else {
                if(collection!=null){
                    for (int i = 0; i < collection.size() ; i++) {
                        if(collection.get(i).equals(student.get(position))){
                            collection.remove(i);
                            updateCollection();
                            break;
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(student==null){
            return 0;
        }
        return student.size();
    }

    public static class StudentCollectionHolder extends RecyclerView.ViewHolder{
        public TextView tv_name,tv_id;
        public CheckBox rb_collection;
        public StudentCollectionHolder(@NonNull View itemView) {
            super(itemView);

            setIsRecyclable(false);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_id = itemView.findViewById(R.id.tv_id);
            rb_collection = itemView.findViewById(R.id.cb_collection);
        }
    }
}
