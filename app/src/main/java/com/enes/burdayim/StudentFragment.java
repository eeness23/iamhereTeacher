package com.enes.burdayim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StudentFragment extends Fragment implements View.OnClickListener     {
    private String qrCode;
    private ListView listView_student;
    private TextView textView_null;
    private TextView table_number;
    private TextView table_name;
    private TextView table_level;
    private TextView tw_count_name;
    private TextView tw_count;
    private UserCustomAdapter userAdapter;
    private ArrayList<Student> studentArrayList;
    private DatabaseReference ref;
    private Button btnSend;
    private Bundle savedState;
    private boolean saved;
    private static final String _FRAGMENT_STATE = "FRAGMENT_STATE";
    private SharedPreferences sharedPreferences;
    private long lastClickTime = 0;
    private String lessonName;
    private String lessonTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View view=inflater.inflate(R.layout.fragment_student, container, false);

        sharedPreferences = getActivity().getSharedPreferences("com.enes.burdayim", Context.MODE_PRIVATE);

        listView_student = view.findViewById(R.id.listView_student);
        textView_null=view.findViewById(R.id.textView_null);
        table_level=view.findViewById(R.id.table_level);
        table_name=view.findViewById(R.id.table_name);
        table_number=view.findViewById(R.id.table_number);
        tw_count=view.findViewById(R.id.tw_count);
        tw_count_name=view.findViewById(R.id.tw_count_name);
        textView_null=view.findViewById(R.id.textView_null);
        btnSend = view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        table_number.setVisibility(View.INVISIBLE);
        table_level.setVisibility(View.INVISIBLE);
        table_name.setVisibility(View.INVISIBLE);
        listView_student.setVisibility(View.INVISIBLE);
        tw_count.setVisibility(View.INVISIBLE);
        btnSend.setVisibility(View.INVISIBLE);
        tw_count_name.setVisibility(View.INVISIBLE);
        textView_null.setVisibility(View.VISIBLE);
        textView_null.setText("Yoklama Sistemi Aktif Değildir.");
        studentArrayList = new ArrayList<Student>();

        if(sharedPreferences!=null){
            table_number.setVisibility(sharedPreferences.getInt("table_number",View.INVISIBLE));
            table_level.setVisibility(sharedPreferences.getInt("table_level",View.INVISIBLE));
            table_name.setVisibility(sharedPreferences.getInt("table_name",View.INVISIBLE));
            listView_student.setVisibility(sharedPreferences.getInt("listView_student",View.INVISIBLE));
            tw_count.setVisibility(sharedPreferences.getInt("tw_count",View.INVISIBLE));
            tw_count_name.setVisibility(sharedPreferences.getInt("tw_count_name",View.INVISIBLE));
            textView_null.setVisibility(sharedPreferences.getInt("textView_null",View.VISIBLE));
            btnSend.setVisibility(sharedPreferences.getInt("btnSend",View.INVISIBLE));
            qrCode=sharedPreferences.getString("qrCode",null);
            if(qrCode==null){
                qrCode="";
            }
            Gson gson = new Gson();
            String response=sharedPreferences.getString("studentListJson" ,"");
            studentArrayList = gson.fromJson(response, new TypeToken<List<Student>>(){}.getType());

            if(studentArrayList==null){
                studentArrayList=new ArrayList<Student>();
                tw_count.setText("0");
            }else {
                tw_count.setText(String.valueOf(studentArrayList.size()));
            }

            lessonName=sharedPreferences.getString("lessonName","");
            lessonTime=sharedPreferences.getString("lessonTime","");
        }


        userAdapter = new UserCustomAdapter(getContext(), R.layout.student_list_item, studentArrayList);
        listView_student = view.findViewById(R.id.listView_student);
        listView_student.setItemsCanFocus(false);
        listView_student.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();


        return view;
    }

    @Subscribe(sticky = true)
    public void takeQrCode(Functions.sendQrCode sendQrCode) {
        this.qrCode = sendQrCode.getQrCode();
    }

    @Subscribe(sticky = true)
    public void fillTable(Functions.fillTable fillTable) {
        if(fillTable.isStart()) {
            fillTable();
        }
    }

    @Subscribe(sticky = true)
    public void sendList(Functions.sendList sendList) {
       if(sendList.isSend()){
           btnSend.setVisibility(View.VISIBLE);
           lessonName=sendList.getLessonName();
           lessonTime=sendList.getLessonTime();
        }
    }

    public void fillTable() {
        textView_null.setVisibility(View.INVISIBLE);
        table_number.setVisibility(View.VISIBLE);
        table_level.setVisibility(View.VISIBLE);
        table_name.setVisibility(View.VISIBLE);
        listView_student.setVisibility(View.VISIBLE);
        tw_count.setVisibility(View.VISIBLE);
        tw_count_name.setVisibility(View.VISIBLE);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Classes").child(qrCode).addValueEventListener(new ValueEventListener() {
            private int tempControl;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals("control")){
                        tempControl=Integer.parseInt(ds.getValue().toString());
                    }
                    if(ds.getKey().toString().equals("clear") && ds.getValue().toString().equals("1")){
                        studentArrayList.clear();
                        userAdapter.notifyDataSetChanged();
                        ref.child("Classes").child(qrCode).child("clear").setValue(0);
                        ref.child("Classes").child(qrCode).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Student student = ds.getValue(Student.class);
                                    ref.child("ogrenci").child(student.getNumber()).child("justOneScan").setValue(true);
                                    if(tempControl==2){
                                        ref.child("ogrenci").child(student.getNumber()).child("takenLesson").setValue(true);
                                    }else {
                                        ref.child("ogrenci").child(student.getNumber()).child("takenLesson").setValue(false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        ref.child("Classes").child(qrCode).child("Students").removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("Classes").child(qrCode).child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentArrayList.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    userAdapter.notifyDataSetChanged();
                    tw_count.setText("0");
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Student student = ds.getValue(Student.class);
                    student.setKey(ds.getKey());
                    studentArrayList.add(student);
                    Collections.sort(studentArrayList, new CustomComparator());
                    userAdapter.notifyDataSetChanged();
                    tw_count.setText(String.valueOf(studentArrayList.size()));
                  /*  if(ds.getPgetKey().equals("clear") && ds.getValue().toString().equals("1")){
                        studentArrayList.clear();
                        userAdapter.notifyDataSetChanged();
                        ref.child("clear").setValue(0);
                    }
                    */
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putString("qrCode",qrCode).apply();
        sharedPreferences.edit().putInt("table_number",table_number.getVisibility()).apply();
        sharedPreferences.edit().putInt("table_level",table_level.getVisibility()).apply();
        sharedPreferences.edit().putInt("table_name",table_name.getVisibility()).apply();
        sharedPreferences.edit().putInt("listView_student",listView_student.getVisibility()).apply();
        sharedPreferences.edit().putInt("tw_count",tw_count.getVisibility()).apply();
        sharedPreferences.edit().putInt("tw_count_name",tw_count_name.getVisibility()).apply();
        sharedPreferences.edit().putInt("textView_null",textView_null.getVisibility()).apply();
        sharedPreferences.edit().putInt("btnSend",btnSend.getVisibility()).apply();
        sharedPreferences.edit().putString("lessonName",lessonName).apply();
        sharedPreferences.edit().putString("lessonTime",lessonTime).apply();
        Gson gson = new Gson();
        String studentListJson = gson.toJson(studentArrayList);
        sharedPreferences.edit().putString("studentListJson",studentListJson).apply();
    }

    public void send (View view){
        Toast.makeText(getActivity(), "deneme", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    break;
                }
                table_number.setVisibility(View.INVISIBLE);
                table_level.setVisibility(View.INVISIBLE);
                table_name.setVisibility(View.INVISIBLE);
                listView_student.setVisibility(View.INVISIBLE);
                tw_count.setVisibility(View.INVISIBLE);
                btnSend.setVisibility(View.INVISIBLE);
                tw_count_name.setVisibility(View.INVISIBLE);
                textView_null.setText("Yoklama Sistemi Aktif Değildir.");
                textView_null.setVisibility(View.VISIBLE);
                tw_count.setText("0");
                clearAndSaveList();

                EventBus.getDefault().postSticky(new Functions.clearCache(true));
                break;
        }
    }
    private void clearAndSaveList() {
        String date=getDate();

        for(int i=0 ; i<studentArrayList.size();i++){
           String tempKey=ref.child("Results").child(date).child(lessonName).child(lessonTime).push().getKey();

            ref.child("Results").child(date).child(lessonName).child(lessonTime).child(tempKey).
                    child("name").setValue(studentArrayList.get(i).getName());
            ref.child("Results").child(date).child(lessonName).child(lessonTime).child(tempKey).
                    child("number").setValue(studentArrayList.get(i).getNumber());
            ref.child("Results").child(date).child(lessonName).child(lessonTime).child(tempKey).
                    child("level").setValue(studentArrayList.get(i).getLevel());
        }

        ref.child("Classes").child(qrCode).child("control").setValue(0);
        ref.child("Classes").child(qrCode).child("clear").setValue(1);
        ref.child("Classes").child(qrCode).child("busy").setValue(false);
        ref.child("Classes").child(qrCode).child("lessonName").setValue(" ");
        ref.child("Classes").child(qrCode).child("educationType").setValue(" ");
        ref.child("Classes").child(qrCode).child("lessonTime").setValue(" ");
    }

    private String getDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }


}