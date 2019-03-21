package com.enes.burdayim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class ProfilFragment extends Fragment implements View.OnClickListener {
    Button btn_logout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_profil, container, false);
        btn_logout= view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                SharedPreferences settings = getActivity().getSharedPreferences("com.enes.burdayim", Context.MODE_PRIVATE);
                settings.edit().putBoolean("controlLogin",false).apply();
                EventBus.getDefault().postSticky(new Functions.clearCache(true));
                Intent ıntent = new Intent(getActivity(),Login.class);
                startActivity(ıntent);

                break;

        }
    }
}
