package com.enes.burdayim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class Main extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private ArrayList<String> courseNameList;
    private String username;
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment studentFragment = new StudentFragment();
    private final Fragment profilFragment = new ProfilFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;
    SecretKeyGenerator secretKeyGenerator;
    BottomNavigationView bottomNavigationView;
    String backStateName ;
    Fragment temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            active = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
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

        Intent i = getIntent();
        username = i.getStringExtra("teacherUsername");
        secretKeyGenerator = new SecretKeyGenerator();

        fm.addOnBackStackChangedListener(this);

        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.fragment_container, profilFragment, "3").hide(profilFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, studentFragment, "2").hide(studentFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").hide(profilFragment).commit();
        fm.beginTransaction().hide(profilFragment).hide(studentFragment).hide(homeFragment).show(active).commit();
        backStateName = getSupportFragmentManager().getClass().getName();
        String backStateName = getSupportFragmentManager().getClass().getName();
        EventBus.getDefault().postSticky(new Functions.sendUsername(username));


    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            if(active!=homeFragment) {
                                fm.beginTransaction().hide(active).show(homeFragment).commit();
                                active = homeFragment;
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                return true;
                            }
                            return false;
                        case R.id.nav_student:
                            if(active!=studentFragment) {
                                backStateName = fm.getClass().getName();
                                fm.beginTransaction().hide(active).show(studentFragment).commit();
                                active = studentFragment;
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                return true;
                            }
                            return false;
                        case R.id.nav_profil:
                            if(active!=profilFragment) {
                                backStateName = fm.getClass().getName();
                                fm.beginTransaction().hide(active).show(profilFragment).commit();
                                active = profilFragment;
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                return true;
                            }
                            return false;
                    }

                    return false;
                }
            };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You called the scan", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    EventBus.getDefault().postSticky(new Functions.sendQrCode(secretKeyGenerator.decrypt(result.getContents())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackStackChanged() {
            int eleman = fm.getBackStackEntryCount();
            StringBuilder stringBuilder= new StringBuilder("");
            for (int i=eleman-1;i>=0;i--){
                stringBuilder.append(" \n");
                stringBuilder.append("Index ").append(i).append(" : ");
                stringBuilder.append(fm.getBackStackEntryAt(i).getName());

            }

        Log.e("enes", stringBuilder.toString());
    }


   public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
            super.onBackPressed();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

  /*
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount()-1;

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        bottomNavigationView.setSelectedItemId(getSupportFragmentManager().getBackStackEntryAt(count-1));
        }
    }
*/
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        fm.putFragment(outState, "myFragmentName", active);
    }

}
