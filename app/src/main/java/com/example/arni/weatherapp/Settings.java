package com.example.arni.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;


/**
 * Created by Arni on 2017-08-21.
 */

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new TemperatureFragment())
                .commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String titleText = "SETTINGS";

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.test, root, false);
        bar.setTitle(titleText);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }

        });
    }

    void openMainActivity() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openMainActivity();
        finish();
    }

    public static class TemperatureFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_weather_settings);

//            Preference preference = findPreference(MainActivity.TEMPERATURE_KEY);
//            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("metric")) {
//                        preference.setSummary("Unit: ℃");
//                    } else if (MainActivity.sharedPreferences.getString(MainActivity.TEMPERATURE_KEY, "").equals("imperial")) {
//                        preference.setSummary("Unit: °F");
//                    }
//                    return true;
//                }
//            });
        }


        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ListView lv = (ListView) view.findViewById(android.R.id.list);
            ViewGroup parent = (ViewGroup) lv.getParent();
            parent.setPadding(0, 140, 0, 0);
        }
    }
}