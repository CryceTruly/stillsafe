package com.crycetruly.a4app;

import android.app.FragmentTransaction;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
     Settings settings =new Settings();

        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainlay,settings);
        fragmentTransaction.commit();
    }
    public static class Settings extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main);
        }
    }
}
