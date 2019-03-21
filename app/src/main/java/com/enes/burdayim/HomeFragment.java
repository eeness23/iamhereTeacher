package com.enes.burdayim;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private TextView tw_count;
    private Spinner spinner;
    private Spinner spinnerMunites;
    private Spinner spinnerEducationType;
    private Spinner spinnerLessonTimes;
    private ImageView imageButton;
    private Button btn_start_lesson;
    private Button btn_finish_lesson;
    private String qrCode;
    private Teacher teacher;
    private ArrayList<String> courseNameList;
    private static String username;
    private DatabaseReference ref;
    private static long munite = 1;
    private static long START_TIME_IN_MILLIS = 60000 * munite;
    private static long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private int lessonCount;
    private boolean checkStartCount;
    private boolean checkFirstHalfLesson;
    private boolean checkAccessDatabase;
    private boolean lockClass;
    private boolean lockCount;
    private String lessonName;
    private String educationType;
    private String lessonTime;
    private TextView textView_lesson;
    private TextView textView_munite;
    private TextView textView_education;
    private TextView textView_lessonTime;
    private Typeface custom_font;
    private Typeface custom_font_button;
    private Drawable drawable;
    public static SharedPreferences sharedPreferences;
    private long lastClickTime = 0;
    private boolean changeImageView;
    private int startButtonCount;
    private int finishButtonCount;
    private String timeLeftFormatted;
    private boolean firstOpenApp;
    private TextView warning_list;

    Functions.fillTable fillTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        teacher = new Teacher();
        sharedPreferences = getActivity().getSharedPreferences("com.enes.burdayim", Context.MODE_PRIVATE);
        imageButton = view.findViewById(R.id.tw_qr);
        imageButton.setOnClickListener(this);
        spinnerMunites = view.findViewById(R.id.spinner2);
        spinnerEducationType = view.findViewById(R.id.spinner_education);
        spinnerLessonTimes = view.findViewById(R.id.spinner_lessonTime);
        fillTable=new Functions.fillTable();
        ref = FirebaseDatabase.getInstance().getReference();
        tw_count = view.findViewById(R.id.tw_count);
        btn_finish_lesson = view.findViewById(R.id.btn_finish_lesson);
        btn_finish_lesson.setOnClickListener(this);
        btn_finish_lesson.setVisibility(View.INVISIBLE);
        btn_start_lesson = view.findViewById(R.id.btn_start_lesson);
        btn_start_lesson.setOnClickListener(this);
        textView_education=view.findViewById(R.id.textView_education);
        textView_lesson=view.findViewById(R.id.textView_lesson);
        textView_munite=view.findViewById(R.id.textView_munite);
        textView_lessonTime=view.findViewById(R.id.textView_lessonTime);
        warning_list=view.findViewById(R.id.warning_list);
        System.out.println("deneme ss:"+sharedPreferences.getInt("warning_list",View.INVISIBLE));
        fillTable.setStart(false);
        firstOpenApp=true;
        startButtonCount=0;
        finishButtonCount=0;
        changeImageView=false;
        lessonName=null;
        lockClass=false;
        lockCount=false;
        qrCode = null;
        lessonCount = 0;
        checkStartCount = false;
        checkFirstHalfLesson = false;
        checkAccessDatabase = false;
        custom_font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LatoLight.ttf");
        custom_font_button = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LatoRegular.ttf");
        textView_education.setTypeface(custom_font);
        textView_lesson.setTypeface(custom_font);
        textView_munite.setTypeface(custom_font);
        textView_lessonTime.setTypeface(custom_font);
        warning_list.setVisibility(View.INVISIBLE);
        warning_list.setTypeface(custom_font);

      /*  if(savedInstanceState!=null){
            qrCode=savedInstanceState.getString("qrCode");
          lessonName=savedInstanceState.getString("lessonName");
            educationType=savedInstanceState.getString("educationType");
            lessonTime=savedInstanceState.getString("lessonTime");
            username=savedInstanceState.getString("username");
            checkStartCount=savedInstanceState.getBoolean("checkStartCount");
            checkFirstHalfLesson=savedInstanceState.getBoolean("checkFirstHalfLesson");
            checkAccessDatabase=savedInstanceState.getBoolean("checkAccessDatabase");
            munite=savedInstanceState.getLong("munite");
            lessonCount=savedInstanceState.getInt("lessonCount");
            lockClass=savedInstanceState.getBoolean("lockClass");
            lockCount=savedInstanceState.getBoolean("lockCount");
            courseNameList=savedInstanceState.getStringArrayList("courseNameList");
            btn_start_lesson.setVisibility(savedInstanceState.getInt("btn_start_lesson"));
            btn_finish_lesson.setVisibility(savedInstanceState.getInt("btn_finish_lesson"));
        }
*/

      if(sharedPreferences==null){
          System.out.println("dazzzaaaa");
      }
        if(sharedPreferences!=null){
            this.qrCode=sharedPreferences.getString("qrCode",null);
            checkStartCount=sharedPreferences.getBoolean("checkStartCount",false);
            checkFirstHalfLesson=sharedPreferences.getBoolean("checkFirstHalfLesson",false);
            checkAccessDatabase=sharedPreferences.getBoolean("checkAccessDatabase",false);
            lockClass=sharedPreferences.getBoolean("lockClass",false);
            lockCount=sharedPreferences.getBoolean("lockCount",false);
            lessonName=sharedPreferences.getString("lessonName",null);
            educationType=sharedPreferences.getString("educationType",null);
            lessonTime=sharedPreferences.getString("lessonTime",null);
            username=sharedPreferences.getString("username",username);
            lessonCount=sharedPreferences.getInt("lessonCount",0);

            checkFirstHalfLesson=sharedPreferences.getBoolean("checkFirstHalfLesson",false);
            checkAccessDatabase=sharedPreferences.getBoolean("checkAccessDatabase",false);
            lockClass=sharedPreferences.getBoolean("lockClass",false);
            lockCount=sharedPreferences.getBoolean("lockCount",false);
            btn_start_lesson.setVisibility(sharedPreferences.getInt("btn_start_lesson",View.VISIBLE));
            btn_finish_lesson.setVisibility(sharedPreferences.getInt("btn_finish_lesson",View.INVISIBLE));

            startButtonCount=sharedPreferences.getInt("startButtonCount",0);
            finishButtonCount=sharedPreferences.getInt("finishButtonCount",0);


            spinnerMunites.setEnabled(sharedPreferences.getBoolean("spinnerMunites",true));
            spinnerEducationType.setEnabled(sharedPreferences.getBoolean("spinnerEducationType",true));
            spinnerLessonTimes.setEnabled(sharedPreferences.getBoolean("spinnerLessonTimes",true));
            imageButton.setEnabled(sharedPreferences.getBoolean("imageButton",true));

            if(sharedPreferences.getBoolean("changeImageView",false)){
                imageButton.setImageResource(R.drawable.qr_okey);
            }else {
                imageButton.setImageResource(R.drawable.qr_click_me);
            }
            munite=sharedPreferences.getLong("munite",1);
            START_TIME_IN_MILLIS=sharedPreferences.getLong("START_TIME_IN_MILLIS",60000 * munite);

            mTimeLeftInMillis=sharedPreferences.getLong("mTimeLeftInMillis",START_TIME_IN_MILLIS);
            tw_count.setText(sharedPreferences.getString("twCountShow","malesef"));

              if(startButtonCount!=0 && startButtonCount%2!=0){
                  btn_start_lesson.performClick();
              }

              if(finishButtonCount!=0 && finishButtonCount%2!=0){
                  btn_finish_lesson.performClick();
              }

            fillTable.setStart(sharedPreferences.getBoolean("fillTable",false));

            if(fillTable.isStart()) {
                EventBus.getDefault().postSticky(fillTable);
            }

            warning_list.setVisibility(sharedPreferences.getInt("warning_list",View.INVISIBLE));

        }



        ArrayAdapter<String> educationTypeArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.typeOfEducation)){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_button);
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_button);
            //    v.setBackgroundColor(Color.GREEN);
                return v;
            }
        };
        educationTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducationType.setAdapter(educationTypeArrayAdapter);
        if(educationType!=null){
            int spinnerPosition = educationTypeArrayAdapter.getPosition(educationType);
            spinnerEducationType.setSelection(spinnerPosition);
        }
        spinnerEducationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    educationType = parent.getItemAtPosition(position).toString();

                    ArrayAdapter<String> lessonTimeArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.lessonTimesDayTimes));
                    lessonTimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLessonTimes.setAdapter(lessonTimeArrayAdapter);
                    if(lessonTime!=null){
                        int spinnerPosition = lessonTimeArrayAdapter.getPosition(lessonTime);
                        spinnerLessonTimes.setSelection(spinnerPosition);

                    }
                    spinnerLessonTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            lessonTime = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else {
                    educationType = parent.getItemAtPosition(position).toString();

                    ArrayAdapter<String> lessonTimeArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.lessonTimesEveningTimes)){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            ((TextView) v).setTypeface(custom_font_button);
                            return v;
                        }
                        public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                            View v =super.getDropDownView(position, convertView, parent);
                            ((TextView) v).setTypeface(custom_font_button);
                          //  v.setBackgroundColor(Color.GREEN);
                            return v;
                        }
                    };
                    lessonTimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLessonTimes.setAdapter(lessonTimeArrayAdapter);
                    if(lessonTime!=null) {
                        int spinnerPosition = lessonTimeArrayAdapter.getPosition(lessonTime);
                        spinnerLessonTimes.setSelection(spinnerPosition);

                    }
                    spinnerLessonTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            lessonTime = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> muniteArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.munites)){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_button);
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(custom_font_button);
              //  v.setBackgroundColor(Color.GREEN);
                return v;
            }
        };
        muniteArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunites.setAdapter(muniteArrayAdapter);
        if(!checkFirstHalfLesson){
            int spinnerPosition = muniteArrayAdapter.getPosition(String.valueOf(munite));
            spinnerMunites.setSelection(spinnerPosition);
        }
       if(spinnerMunites.isEnabled()) {
           spinnerMunites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   munite = Long.parseLong(parent.getItemAtPosition(position).toString());
                   START_TIME_IN_MILLIS = 60000 * munite;
                   mTimeLeftInMillis = START_TIME_IN_MILLIS;
                   updateCountDownText();
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });
       }

        ref.child("Teachers").child(username).child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(courseNameList==null) {
                    courseNameList = new ArrayList<String>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        courseNameList.add(ds.getValue().toString());
                    }

                }
                teacher.setCourse(courseNameList);
                teacher.getCourse();
                spinner = view.findViewById(R.id.spinner);
                if(sharedPreferences!=null){
                    spinner.setEnabled(sharedPreferences.getBoolean("spinner",true));
                }

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        teacher.getCourse()){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        ((TextView) v).setTypeface(custom_font_button);
                        return v;
                    }
                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        ((TextView) v).setTypeface(custom_font_button);

                    //    v.setBackgroundColor(Color.GREEN);
                        return v;
                    }
                };
                spinner.setAdapter(stringArrayAdapter);
                stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if(lessonName!=null){
                    int spinnerPosition = stringArrayAdapter.getPosition(lessonName);
                    spinner.setSelection(spinnerPosition);

                }
                if(spinner.isEnabled()) {
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            lessonName = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateCountDownText();
        return view;
    }

    private void startTimer() {
        changeImageView=true;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;

                if (!checkFirstHalfLesson) {
                    btn_start_lesson.setText("Start Lesson");
                    btn_start_lesson.setVisibility(View.INVISIBLE);
                    btn_finish_lesson.setVisibility(View.VISIBLE);
                    spinnerMunites.setEnabled(true);
                    ref.child("Classes").child(qrCode).child("control").setValue(0);
                    checkFirstHalfLesson = true;
                    checkAccessDatabase = false;
                    changeImageView=true;
                    startButtonCount=0;
                    START_TIME_IN_MILLIS = 60000 * munite;
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    updateCountDownText();
                } else {
                    btn_finish_lesson.setText("Finish Lesson");
                    btn_finish_lesson.setVisibility(View.INVISIBLE);
                    btn_start_lesson.setVisibility(View.INVISIBLE);
                  //  btn_start_lesson.setVisibility(View.VISIBLE);
                    finishButtonCount=0;
                  /*  ref.child("Classes").child(qrCode).child("control").setValue(0);
                    ref.child("Classes").child(qrCode).child("busy").setValue(false);
                    ref.child("Classes").child(qrCode).child("lessonName").setValue(" ");
                    ref.child("Classes").child(qrCode).child("educationType").setValue(" ");
                    ref.child("Classes").child(qrCode).child("lessonTime").setValue(" ");
                    */
                  // finish buttonu bittiğinde resimi değiştir. Veritabanından silme listeyi gönderdikten sonra sil.
                    changeImageView=false;
                    lockClass=false;
                    lockCount=false;
                    checkFirstHalfLesson = false;
                    checkAccessDatabase = false;
                    checkStartCount=false;
                    START_TIME_IN_MILLIS = 60000 * munite;
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    updateCountDownText();
                    warning_list.setVisibility(View.VISIBLE);
                    EventBus.getDefault().postSticky(new Functions.sendList(true,lessonName,lessonTime));
                }

            }
        }.start();

        if (!checkFirstHalfLesson) {
            mTimerRunning = true;
            btn_start_lesson.setText("pause");
        } else {
            mTimerRunning = true;
            btn_finish_lesson.setText("pause");
        }

    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        if (!checkFirstHalfLesson) {
            btn_start_lesson.setText("Start Lesson");
        } else {
            btn_finish_lesson.setText("Finish Lesson");
        }

    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tw_count.setText(timeLeftFormatted);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tw_qr:
                IntentIntegrator ıntentIntegrator = new IntentIntegrator(getActivity());
                ıntentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                ıntentIntegrator.setPrompt("Scan");
                ıntentIntegrator.setCameraId(0);
                ıntentIntegrator.setBeepEnabled(false);
                ıntentIntegrator.setBarcodeImageEnabled(false);
                ıntentIntegrator.initiateScan();
                break;
            case R.id.btn_start_lesson:
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    break;
                }
                        if(qrCode!=null) {
                            changeImageView=true;
                            startLesson();
                            startCount();
                            lastClickTime = SystemClock.elapsedRealtime();
                            if(btn_start_lesson.isPressed()){
                                startButtonCount++;
                            }
                            break;
                        }else
                        {
                            Toast.makeText(getActivity(), "QR TARAMASINI YAPMALISINIZ", Toast.LENGTH_LONG).show();
                            lastClickTime = SystemClock.elapsedRealtime();
                            break;
                        }
            case R.id.btn_finish_lesson:
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    break;
                }
                        startLesson();
                        startCount();
                if(btn_finish_lesson.isPressed()){
                    finishButtonCount++;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                         break;
        }
    }

    private void startCount() {
        if(lockCount) {
            if (!checkStartCount) {
                spinner.setEnabled(false);
                spinnerMunites.setEnabled(false);
                spinnerLessonTimes.setEnabled(false);
                spinnerEducationType.setEnabled(false);
                imageButton.setEnabled(false);
                checkStartCount = true;
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            } else {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }

            }
        }
    }

    private void startLesson() {
        spinnerMunites.setEnabled(false);
        if(!lockClass){
            ref.child("Classes").child(qrCode).child("busy").setValue(true);
            ref.child("Classes").child(qrCode).child("lessonName").setValue(lessonName);
            ref.child("Classes").child(qrCode).child("control").setValue(0);
            lockClass=true;
            lockCount=true;
            fillTable.setStart(true);
            EventBus.getDefault().postSticky(fillTable);
        }else {
            if(!lockCount) {
                Toast.makeText(getActivity(), "BU SINIFTA DERS İŞLENMEKTEDİR.", Toast.LENGTH_SHORT).show();
                checkAccessDatabase = true;
            }
        }

        if (!checkAccessDatabase) {
           /* ref.child("Classes").child(qrCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals("control")) {
                            ref.child("Classes").child(qrCode).child("control").setValue(lessonCount);

                            lessonCount++;
                            if (lessonCount == 3) {
                                lessonCount = 1;
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

           */
            lessonCount++;
            if(lessonCount!=1){
                ref.child("Classes").child(qrCode).child("clear").setValue(1);
            }
            ref.child("Classes").child(qrCode).child("control").setValue(lessonCount);
            ref.child("Classes").child(qrCode).child("educationType").setValue(educationType);
            ref.child("Classes").child(qrCode).child("lessonTime").setValue(lessonTime);

            if(ref.child("Classes").child(qrCode).child("Students").getDatabase()==null){
                System.out.println("DENEME enes boş");
            }else{
                System.out.println("DENEME enes boş değil");
            }

            if (lessonCount == 2) {
                lessonCount = 0;
            }
            checkAccessDatabase = true;
        }
    }

    @Subscribe(sticky = true)
    public void takeQrCode(Functions.sendQrCode sendQrCode) {
        this.qrCode = sendQrCode.getQrCode();

        ref.child("Classes").child(qrCode).child("busy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true")){
                    lockClass=true;
                    changeImageView=false;
                    imageButton.setImageResource(R.drawable.qr_click_me);
                }else {
                    lockClass=false;
                    changeImageView=true;
                    imageButton.setImageResource(R.drawable.qr_okey);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Subscribe(sticky = true)
    public void takeUsername(Functions.sendUsername sendUsername) {
        username = sendUsername.getUsername();
    }

    @Subscribe(sticky = true)
    public void clearCache(Functions.clearCache clearCache) {
       if(clearCache.isClear()){
           spinner.setEnabled(true);
           spinnerMunites.setEnabled(true);
           spinnerEducationType.setEnabled(true);
           spinnerLessonTimes.setEnabled(true);
           imageButton.setEnabled(true);
           warning_list.setVisibility(View.INVISIBLE);
           btn_finish_lesson.setVisibility(View.INVISIBLE);
           btn_start_lesson.setVisibility(View.VISIBLE);
           qrCode=null;
           imageButton.setImageResource(R.drawable.qr_click_me);
       }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        if(sharedPreferences==null){
            System.out.println("dazzzaaa");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);

    }

  /*  @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("qrCode",qrCode);
        outState.putString("lessonName",lessonName);
        outState.putString("educationType",educationType);
        outState.putLong("munite",munite);
        outState.putString("lessonTime",lessonTime);
        outState.putStringArrayList("courseNameList",courseNameList);
        outState.putString("username",username);
        outState.putBoolean("lockClass",lockClass);
        outState.putBoolean("lockCount",lockCount);
        outState.putInt("lessonCount",lessonCount);
        outState.putBoolean("checkStartCount",checkStartCount);
        outState.putBoolean("checkFirstHalfLesson",checkFirstHalfLesson);
        outState.putBoolean("checkAccessDatabase",checkAccessDatabase);


        outState.putInt("btn_finish_lesson",btn_finish_lesson.getVisibility());
        outState.putInt("btn_start_lesson",btn_start_lesson.getVisibility());
        super.onSaveInstanceState(outState);
    }
    */

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putString("lessonName",lessonName).apply();
        sharedPreferences.edit().putString("educationType",educationType).apply();
        sharedPreferences.edit().putString("lessonTime",lessonTime).apply();
        sharedPreferences.edit().putBoolean("checkFirstHalfLesson",checkFirstHalfLesson).apply();
        sharedPreferences.edit().putBoolean("checkAccessDatabase",checkAccessDatabase).apply();
        sharedPreferences.edit().putBoolean("checkStartCount",checkStartCount).apply();
        sharedPreferences.edit().putBoolean("lockClass",lockClass).apply();
        sharedPreferences.edit().putBoolean("lockCount",lockCount).apply();
        sharedPreferences.edit().putInt("lessonCount",lessonCount).apply();
        sharedPreferences.edit().putString("username",username).apply();
        sharedPreferences.edit().putBoolean("spinner",spinner.isEnabled()).apply();
        sharedPreferences.edit().putBoolean("spinnerMunites",spinnerMunites.isEnabled()).apply();
        sharedPreferences.edit().putBoolean("spinnerEducationType",spinnerEducationType.isEnabled()).apply();
        sharedPreferences.edit().putBoolean("spinnerLessonTimes",spinnerLessonTimes.isEnabled()).apply();
        sharedPreferences.edit().putBoolean("imageButton",imageButton.isEnabled()).apply();
        sharedPreferences.edit().putBoolean("spinner",spinner.isEnabled()).apply();
        sharedPreferences.edit().putString("qrCode",qrCode).apply();
        sharedPreferences.edit().putBoolean("changeImageView", changeImageView).apply();
        sharedPreferences.edit().putInt("btn_finish_lesson",btn_finish_lesson.getVisibility()).apply();
        sharedPreferences.edit().putInt("btn_start_lesson",btn_start_lesson.getVisibility()).apply();
        sharedPreferences.edit().putInt("startButtonCount",startButtonCount).apply();
        sharedPreferences.edit().putInt("finishButtonCount",finishButtonCount).apply();

        sharedPreferences.edit().putLong("mTimeLeftInMillis",mTimeLeftInMillis).apply();
        sharedPreferences.edit().putLong("START_TIME_IN_MILLIS",START_TIME_IN_MILLIS).apply();
        sharedPreferences.edit().putLong("munite",munite).apply();

        sharedPreferences.edit().putBoolean("fillTable",fillTable.isStart()).apply();
        sharedPreferences.edit().putInt("warning_list",warning_list.getVisibility()).apply();
    }

}
