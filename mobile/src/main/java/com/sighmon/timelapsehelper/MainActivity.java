package com.sighmon.timelapsehelper;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.sighmon.timelapsehelper.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        // Set default preferences, the false on the end means it's only set once
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_reset:
                // Log.i("Menu", "Reset pressed.");
                checkResetAlert();
                return true;
            case R.id.action_settings:
                // Log.i("Menu", "Settings pressed.");
                // Settings intent
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                // Log.i("Menu", "About pressed.");
                // About intent
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkResetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // Log.i("Reset", "Clicked reset.");
                resetToDefaults();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // Log.i("Reset", "Clicked cancel.");
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void resetToDefaults() {

        // Get shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the user preferences
        String intervalFromPreferences = sharedPreferences.getString("interval_field", null);
        String shotsFromPreferences = sharedPreferences.getString("shots_field", null);
        String fpsFromPreferences = sharedPreferences.getString("fps_field", null);
        Boolean intervalCentricFromPreferences = sharedPreferences.getBoolean("interval_centric", false);

        // Get the fields
        EditText intervalField = (EditText) this.findViewById(R.id.interval);
        EditText shotsField = (EditText) this.findViewById(R.id.shots);
        EditText fpsField = (EditText) this.findViewById(R.id.fps);
        final Switch intervalCentricSwitch = (Switch) this.findViewById(R.id.intervalCentricSwitch);

        // Set the fields to user preferences
        intervalField.setText(intervalFromPreferences, TextView.BufferType.EDITABLE);
        shotsField.setText(shotsFromPreferences, TextView.BufferType.EDITABLE);
        fpsField.setText(fpsFromPreferences, TextView.BufferType.EDITABLE);
        intervalCentricSwitch.setChecked(intervalCentricFromPreferences);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainFragment extends Fragment {

        public MainFragment() {
        }

        HashMap<NumberPicker,Integer> scrollStates = new HashMap<NumberPicker,Integer>();

        List shootingGroup;
        List playbackGroup;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Get shared preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());

            // Get the user preferences
            String intervalFromPreferences = sharedPreferences.getString("interval_field", null);
            String shotsFromPreferences = sharedPreferences.getString("shots_field", null);
            String fpsFromPreferences = sharedPreferences.getString("fps_field", null);
            Boolean intervalCentricFromPreferences = sharedPreferences.getBoolean("interval_centric", false);

            // Set the min/max for the pickers.
            NumberPicker shootingDays = (NumberPicker) rootView.findViewById(R.id.shooting_days);
            shootingDays.setMaxValue(99);
            shootingDays.setWrapSelectorWheel(false);
            NumberPicker shootingHours = (NumberPicker) rootView.findViewById(R.id.shooting_hours);
            shootingHours.setMaxValue(23);
            NumberPicker shootingMinutes = (NumberPicker) rootView.findViewById(R.id.shooting_minutes);
            shootingMinutes.setMaxValue(59);
            NumberPicker shootingSeconds = (NumberPicker) rootView.findViewById(R.id.shooting_seconds);
            shootingSeconds.setMaxValue(59);

            shootingGroup = Arrays.asList(
                    shootingDays,
                    shootingHours,
                    shootingMinutes,
                    shootingSeconds);

            NumberPicker playbackHours = (NumberPicker) rootView.findViewById(R.id.playback_hours);
            playbackHours.setMaxValue(23);
            playbackHours.setWrapSelectorWheel(false);
            NumberPicker playbackMinutes = (NumberPicker) rootView.findViewById(R.id.playback_minutes);
            playbackMinutes.setMaxValue(59);
            NumberPicker playbackSeconds = (NumberPicker) rootView.findViewById(R.id.playback_seconds);
            playbackSeconds.setMaxValue(59);
            NumberPicker playbackFrames = (NumberPicker) rootView.findViewById(R.id.playback_frames);
            playbackFrames.setMaxValue(getIntegerFromStringIgnoringNumberFormatException(fpsFromPreferences));

            playbackGroup = Arrays.asList(
                    playbackHours,
                    playbackMinutes,
                    playbackSeconds,
                    playbackFrames);

            // Get the fields
            EditText intervalField = (EditText) rootView.findViewById(R.id.interval);
            EditText shotsField = (EditText) rootView.findViewById(R.id.shots);
            EditText fpsField = (EditText) rootView.findViewById(R.id.fps);
            final Switch intervalCentricSwitch = (Switch) rootView.findViewById(R.id.intervalCentricSwitch);

            // Set the fields to user preferences
            intervalField.setText(intervalFromPreferences, TextView.BufferType.EDITABLE);
            shotsField.setText(shotsFromPreferences, TextView.BufferType.EDITABLE);
            fpsField.setText(fpsFromPreferences, TextView.BufferType.EDITABLE);
            intervalCentricSwitch.setChecked(intervalCentricFromPreferences);

            for (EditText field : new EditText[]{intervalField, shotsField, fpsField}) {
                // When the user changes a field...
                field.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        calculateShootingTime(rootView);
                        calculatePlaybackTime(rootView);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            // Calculate the shooting time
            calculateShootingTime(rootView);
            calculatePlaybackTime(rootView);

            for (final List group : new List[]{shootingGroup,playbackGroup}) {

                for (Object po : group) {
                    NumberPicker picker = (NumberPicker) po;
                    picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
                        @Override
                        public void onScrollStateChange(NumberPicker v, int scrollState) {
                            if (scrollState == SCROLL_STATE_IDLE) {
                                if (group == shootingGroup) {
                                    if (intervalCentricSwitch.isChecked()) {
                                        intervalCentricShooting(rootView);
                                    } else {
                                        shootingCentric(rootView);
                                    }
                                } else if (group == playbackGroup) {
                                    if (intervalCentricSwitch.isChecked()) {
                                        intervalCentricPlayback(rootView);
                                    } else {
                                        playbackCentric(rootView);
                                    }
                                }
                            }
                            scrollStates.put(v, scrollState);
                            //Log.i("picker", "scroll state: " + scrollState);
                        }
                    });
                }

                for (final Object po : group) {
                    NumberPicker picker = (NumberPicker) po;
                    picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker thisPicker, int oldVal, int newVal) {
                            int delta = newVal - oldVal;
                            //Log.i("picker", "valuechanged from " + oldVal + " to " + newVal + " (delta " + delta + ")");
                            int maxDelta = thisPicker.getMaxValue() - thisPicker.getMinValue();
                            // if this picker is scrolling and the value has jumped, we have probably rolled over
                            if (scrollStates.get(thisPicker).intValue() != NumberPicker.OnScrollListener.SCROLL_STATE_IDLE && Math.abs(delta) > (maxDelta / 2)) {
                                int thisIndex = group.indexOf(thisPicker);
                                if (thisIndex>0) {
                                    NumberPicker nextMostSignificantPicker = (NumberPicker) group.get(thisIndex - 1);
                                    nextMostSignificantPicker.setValue(nextMostSignificantPicker.getValue() - Integer.signum(delta));
                                    //Log.i("picker", "rollover detected");
                                }
                            }
                        }
                    });
                }
            }

            return rootView;
        }
    }

    /** Called when the user clicks the Send button */
    /* public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AboutActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    } */

    /*** TIME CALCULATION MATH ***/

    public static void calculateShootingTime(View view) {

        int interval = getIntegerFromEditText(view, R.id.interval);
        int shots = getIntegerFromEditText(view, R.id.shots);

        // Calculate the shooting time
        if (interval > 0) {
            int totalRealSeconds = shots * interval;
            int totalRealMinutes = totalRealSeconds / 60;
            int totalRealHours = totalRealMinutes / 60;
            int totalRealDays = totalRealHours / 24;
            int totalRealHoursRemainder = totalRealHours % 24;
            int totalRealMinutesRemainder = totalRealMinutes % 60;
            int totalRealSecondsRemainder = totalRealSeconds % 60;

            // Update the shooting picker
            NumberPicker shootingDays = (NumberPicker) view.findViewById(R.id.shooting_days);
            shootingDays.setValue(totalRealDays);
            NumberPicker shootingHours = (NumberPicker) view.findViewById(R.id.shooting_hours);
            shootingHours.setValue(totalRealHoursRemainder);
            NumberPicker shootingMinutes = (NumberPicker) view.findViewById(R.id.shooting_minutes);
            shootingMinutes.setValue(totalRealMinutesRemainder);
            NumberPicker shootingSeconds = (NumberPicker) view.findViewById(R.id.shooting_seconds);
            shootingSeconds.setValue(totalRealSecondsRemainder);
        }
    }

    public static void calculatePlaybackTime(View view) {

        int playbackFPS = getIntegerFromEditText(view, R.id.fps);
        int shots = getIntegerFromEditText(view, R.id.shots);

        // Calculate the playback time
        if (playbackFPS > 0) {
            int totalPlaybackSeconds = shots / playbackFPS;
            int totalPlaybackMinutes = totalPlaybackSeconds / 60;
            int totalPlaybackHours = totalPlaybackMinutes / 60;
            int totalPlaybackMinutesRemainder = totalPlaybackMinutes % 60;
            int totalPlaybackSecondsRemainder = totalPlaybackSeconds % 60;
            int totalPlaybackFrames = shots % playbackFPS;

            // Update the playback picker
            NumberPicker playbackHours = (NumberPicker) view.findViewById(R.id.playback_hours);
            playbackHours.setValue(totalPlaybackHours);
            NumberPicker playbackMinutes = (NumberPicker) view.findViewById(R.id.playback_minutes);
            playbackMinutes.setValue(totalPlaybackMinutesRemainder);
            NumberPicker playbackSeconds = (NumberPicker) view.findViewById(R.id.playback_seconds);
            playbackSeconds.setValue(totalPlaybackSecondsRemainder);
            NumberPicker playbackFrames = (NumberPicker) view.findViewById(R.id.playback_frames);
            playbackFrames.setValue(totalPlaybackFrames);
        }
    }

    public static void playbackCentric(View view) {

        int fps = getIntegerFromEditText(view, R.id.fps);
        int hrs = ((NumberPicker) view.findViewById(R.id.playback_hours)).getValue();
        int mins = ((NumberPicker) view.findViewById(R.id.playback_minutes)).getValue();
        int secs = ((NumberPicker) view.findViewById(R.id.playback_seconds)).getValue();
        int frames = ((NumberPicker) view.findViewById(R.id.playback_frames)).getValue();

        int shots = (hrs * (60 * 60 * fps)) + (mins * (60*fps)) + (secs * fps) + frames;

        EditText shotsField = (EditText) view.findViewById(R.id.shots);
        shotsField.setText(Integer.toString(shots), TextView.BufferType.EDITABLE);

        calculateShootingTime(view);
    }

    public static void shootingCentric(View view) {

        int interval = getIntegerFromEditText(view, R.id.interval);
        int days = ((NumberPicker) view.findViewById(R.id.shooting_days)).getValue();
        int hrs = ((NumberPicker) view.findViewById(R.id.shooting_hours)).getValue();
        int mins = ((NumberPicker) view.findViewById(R.id.shooting_minutes)).getValue();
        int secs = ((NumberPicker) view.findViewById(R.id.shooting_seconds)).getValue();

        int seconds = (days * 24 * 60 * 60) + (hrs * 60 * 60) + (mins * 60) + secs;
        int shots = seconds / interval;

        EditText shotsField = (EditText) view.findViewById(R.id.shots);
        shotsField.setText(Integer.toString(shots), TextView.BufferType.EDITABLE);

        calculatePlaybackTime(view);
    }

    public static void intervalCentricShooting(View view) {

        int shots = getIntegerFromEditText(view, R.id.shots);
        int days = ((NumberPicker) view.findViewById(R.id.shooting_days)).getValue();
        int hrs = ((NumberPicker) view.findViewById(R.id.shooting_hours)).getValue();
        int mins = ((NumberPicker) view.findViewById(R.id.shooting_minutes)).getValue();
        int secs = ((NumberPicker) view.findViewById(R.id.shooting_seconds)).getValue();
        int seconds = (days * 24 * 60 * 60) + (hrs * 60 * 60) + (mins * 60) + secs;
        int interval = seconds / shots;

        EditText intervalField = (EditText) view.findViewById(R.id.interval);
        intervalField.setText(Integer.toString(interval), TextView.BufferType.EDITABLE);

        calculatePlaybackTime(view);
    }

    public static void intervalCentricPlayback(View view) {

        int fps = getIntegerFromEditText(view, R.id.fps);
        int hrs = ((NumberPicker) view.findViewById(R.id.playback_hours)).getValue();
        int mins = ((NumberPicker) view.findViewById(R.id.playback_minutes)).getValue();
        int secs = ((NumberPicker) view.findViewById(R.id.playback_seconds)).getValue();
        int shootDays = ((NumberPicker) view.findViewById(R.id.shooting_days)).getValue();
        int shootHrs = ((NumberPicker) view.findViewById(R.id.shooting_hours)).getValue();
        int shootMins = ((NumberPicker) view.findViewById(R.id.shooting_minutes)).getValue();
        int shootSecs = ((NumberPicker) view.findViewById(R.id.shooting_seconds)).getValue();
        int totalPlaybackSeconds = (hrs * 60 * 60) + (mins * 60) + secs;
        int shots = totalPlaybackSeconds * fps;
        int totalShootingSeconds = (shootDays * 24 * 60 * 60) + (shootHrs * 60 * 60) + (shootMins * 60) + shootSecs;
        int interval = 0;
        if (shots > 0) {
            interval = totalShootingSeconds / shots;
        }

        EditText intervalField = (EditText) view.findViewById(R.id.interval);
        intervalField.setText(Integer.toString(interval), TextView.BufferType.EDITABLE);

        EditText shotsField = (EditText) view.findViewById(R.id.shots);
        shotsField.setText(Integer.toString(shots), TextView.BufferType.EDITABLE);

        calculateShootingTime(view);
    }

    public static int getIntegerFromEditText(View view, int objectId) {

        EditText et = (EditText) view.findViewById(objectId);
        String etString = et.getText().toString();

        return getIntegerFromStringIgnoringNumberFormatException(etString);
    }

    public static int getIntegerFromStringIgnoringNumberFormatException(String string) {
        int etInteger = 0;
        try {
            etInteger = Integer.parseInt(string);
        }
        catch (NumberFormatException e){/* Ignore the exception */}

        return etInteger;
    }
}

