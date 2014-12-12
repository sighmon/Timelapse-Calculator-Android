package com.sighmon.timelapsehelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /*** App Preferences Fragment ***/
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

        }
        @Override
        public void onResume() {
            super.onResume();

            // Set the summary to the current values
            Map<String, ?> sharedPreferencesMap = getPreferenceScreen().getSharedPreferences().getAll();
            for (Map.Entry<String, ?> entry : sharedPreferencesMap.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                Preference pref = findPreference(entry.getKey());
                if (pref instanceof EditTextPreference) {
                    pref.setSummary(entry.getValue().toString());
                }
            }

            // Register for changes in settings
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            Preference pref = findPreference(key);
            // Set summary to be the user-description for the selected value
            if (pref instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                pref.setSummary(editTextPref.getText());
            }
        }
    }
}