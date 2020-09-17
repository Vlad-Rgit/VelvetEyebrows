package com.example.velveteyebrows.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference logOutPreference = findPreference("log_out");

        logOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(getContext(), R.style.Theme_VelvetEyebrows_Calendar));


                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppData.clearUser(getContext());
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

                return true;
            }
        });
    }
}