package com.enes.burdayim;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;

public class UserCustomAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Student> data = new ArrayList<Student>();
    private String className;
    DatabaseReference databaseReference;
    String tempNumber;

    public UserCustomAdapter(Context context, int layoutResourceId,
                             ArrayList<Student> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        databaseReference=FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new UserHolder();
            holder.textName = row.findViewById(R.id.tw_name);
            holder.textNumber = row.findViewById(R.id.tw_number);
            holder.textLevel = row.findViewById(R.id.tw_level);
            holder.btnDelete = row.findViewById(R.id.btn_delete);
            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }
        final Student user = data.get(position);
        holder.textName.setText(user.getName());
        holder.textNumber.setText(user.getNumber());
        holder.textLevel.setText(user.getLevel());

        holder.btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                className = "Amfi 1";

                databaseReference.child("Classes").child(className).child("Students")
                        .child(user.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.getKey().equals("number")){
                                tempNumber=ds.getValue().toString();
                                databaseReference.child("ogrenci").child(tempNumber).child("justOneScan").setValue(true);
                                databaseReference.child("ogrenci").child(tempNumber).child("checkContinuity").setValue(0);
                                databaseReference.child("ogrenci").child(tempNumber).child("takenLesson").setValue(false);
                                databaseReference.child("Classes").child(className).child("Students").child(user.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                data.remove(position);

            }
        });
        return row;

    }

    static class UserHolder {
        TextView textNumber;
        TextView textName;
        TextView textLevel;
        ImageView btnDelete;
    }

    public void setClassName(String className) {
        this.className = className;
    }


}

