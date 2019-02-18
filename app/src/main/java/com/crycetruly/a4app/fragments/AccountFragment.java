package com.crycetruly.a4app.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crycetruly.a4app.R;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Elia on 1/27/2018.
 */

public class AccountFragment extends Fragment {
    TextView adminarea,user_name;
    private static final String TAG = "AccountFragment";
    RelativeLayout abt,min,hr,pol;
    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_me,container,false);
          
        abt=v.findViewById(R.id.ab);
        min=v.findViewById(R.id.min);
        hr=v.findViewById(R.id.hr);

        pol=v.findViewById(R.id.pol);

        pol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.upf.go.ug/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.hurinet.or.ug/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.health.go.ug";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        
        abt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailsFromsite();;
            }
        });
        
        
        return v;
    }

    private void showDetailsFromsite() {

        String url = "http://collections.infocollections.org/whocountry/en/d/Jh4325e/15.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


}
