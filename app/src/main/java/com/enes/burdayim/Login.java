package com.enes.burdayim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity  {
    private EditText username;
    private EditText password;
    private TextView login;
    private DatabaseReference ref;
    private Teacher teacher;
    private String sNumber;
    private String sPassword;
    public static SharedPreferences sharedPreferences;
    private boolean controlLogin;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        ref = FirebaseDatabase.getInstance().getReference().child("Teachers");
        sharedPreferences = this.getSharedPreferences("com.enes.burdayim", Context.MODE_PRIVATE);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font_button = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        username.setTypeface(custom_font);
        password.setTypeface(custom_font);
        login.setTypeface(custom_font_button);
        controlLogin=false;
        if(sharedPreferences!=null){
            controlLogin=sharedPreferences.getBoolean("controlLogin",false);
        }

         if(controlLogin){
            Intent i2 = new Intent(getApplicationContext(), Main.class);
            i2.putExtra("teacherUsername", sharedPreferences.getString("teacherUsername",null));
            finish();
            startActivity(i2);

        }


        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    public void enter(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
                sNumber = username.getText().toString();
                sPassword = password.getText().toString();
                if (TextUtils.isEmpty(sNumber) || TextUtils.isEmpty(sPassword)) {
                    Toast.makeText(getApplicationContext(), " Fill in all field", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child(sNumber).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            teacher = dataSnapshot.getValue(Teacher.class);
                            if (teacher == null) {
                                Toast.makeText(Login.this, "Didnt find user", Toast.LENGTH_LONG).show();
                            } else {
                                if (sPassword.equals(teacher.getPassword())) {
                                    Toast.makeText(Login.this, "Login Succeful", Toast.LENGTH_SHORT).show();
                                    sharedPreferences.edit().putBoolean("controlLogin",true).apply();
                                    sharedPreferences.edit().putString("teacherUsername",sNumber).apply();
                                    sharedPreferences.edit().putString("teacherPassword",sPassword).apply();
                                    Intent i2 = new Intent(getApplicationContext(), Main.class);
                                    i2.putExtra("teacherUsername", sNumber);
                                    finish();
                                    startActivity(i2);


                                } else {
                                    Toast.makeText(Login.this, "Password incorrect", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

        lastClickTime = SystemClock.elapsedRealtime();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
