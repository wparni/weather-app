//package com.example.arni.weatherapp;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//
///**
// * Created by Arni on 2017-08-22.
// */
//
//public class SettingsAdapter extends ArrayAdapter {
//    private Context context;
//    private SharedPreferences sharedPreferences;
//    private TextView optionsName;
//    private TextView optionsValue;
//    private Object[] listOfKeys;
//
//
//    public SettingsAdapter(Context context) {
//        super(context, 0);
//        this.context = context;
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        listOfKeys = sharedPreferences.getAll().keySet().toArray();
//    }
//
//
//    @Override
//    public int getCount() {
//        return listOfKeys.length;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View singleLine = layoutInflater.inflate(R.layout.settings_list_view_layout, parent, false);
//
//        String singleKey = (String) listOfKeys[position];
//        Object value = sharedPreferences.getAll().get(singleKey);
//
//        optionsName = (TextView) singleLine.findViewById(R.id.option_name_text_view);
//        optionsValue = (TextView) singleLine.findViewById(R.id.option_chosen_value);
//
//        optionsName.setText(singleKey);
//        optionsValue.setText(value.toString());
//
//
//
//
//
//
//        return singleLine;
//    }
//}
