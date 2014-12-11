package com.sighmon.timelapsehelper;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.EventListener;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.sighmon.timelapsehelper.MESSAGE";
    public final static Integer INTERVAL_FIELD = 8;
    public final static Integer SHOTS_FIELD = 3600;
    public final static Integer FPS_FIELD = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            case R.id.menu_search:
                Log.i("Search", "Search pressed.");
                openSearch();
                return true;
            case R.id.action_settings:
//                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.i("Search", "Clicked OK.");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.i("Search", "Clicked cancel.");
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        int storedScrollState;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Set the min/max for the pickers.
            NumberPicker shootingDays = (NumberPicker) rootView.findViewById(R.id.shooting_days);
            shootingDays.setMaxValue(99);
            NumberPicker shootingHours = (NumberPicker) rootView.findViewById(R.id.shooting_hours);
            shootingHours.setMaxValue(23);
            final NumberPicker shootingMinutes = (NumberPicker) rootView.findViewById(R.id.shooting_minutes);
            shootingMinutes.setMaxValue(59);
            NumberPicker shootingSeconds = (NumberPicker) rootView.findViewById(R.id.shooting_seconds);
            shootingSeconds.setMaxValue(59);

            NumberPicker playbackHours = (NumberPicker) rootView.findViewById(R.id.playback_hours);
            playbackHours.setMaxValue(23);
            NumberPicker playbackMinutes = (NumberPicker) rootView.findViewById(R.id.playback_minutes);
            playbackMinutes.setMaxValue(60);
            NumberPicker playbackSeconds = (NumberPicker) rootView.findViewById(R.id.playback_seconds);
            playbackSeconds.setMaxValue(60);
            NumberPicker playbackFrames = (NumberPicker) rootView.findViewById(R.id.playback_frames);
            playbackFrames.setMaxValue(FPS_FIELD);

            // Set the Interval, Shots & FPS fields to defaults
            // TODO: Work out how to set app defaults in Android
            EditText intervalField = (EditText) rootView.findViewById(R.id.interval);
            intervalField.setText(INTERVAL_FIELD.toString(), TextView.BufferType.EDITABLE);
            EditText shotsField = (EditText) rootView.findViewById(R.id.shots);
            shotsField.setText(SHOTS_FIELD.toString(), TextView.BufferType.EDITABLE);
            EditText fpsField = (EditText) rootView.findViewById(R.id.fps);
            fpsField.setText(FPS_FIELD.toString(), TextView.BufferType.EDITABLE);

            // Calculate the shooting time
            calculateShootingTime(rootView);

            shootingSeconds.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker v, int scrollState) {
                    storedScrollState = scrollState;
                    Log.i("picker","scroll state: "+scrollState);
                }
            });

            shootingSeconds.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    int delta = newVal - oldVal;
                    Log.i("picker", "valuechanged from " + oldVal + " to " + newVal + " (delta " + delta + ")");
                    int maxDelta = picker.getMaxValue() - picker.getMinValue();
                    if (storedScrollState!=NumberPicker.OnScrollListener.SCROLL_STATE_IDLE && Math.abs(delta) > (maxDelta / 2)) {
                        shootingMinutes.setValue(shootingMinutes.getValue()-Integer.signum(delta));
                        Log.i("picker", "rollover detected");
                    }

                }
            });

            return rootView;
        }
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*** TIME CALCULATION MATH ***/

    public static void calculateShootingTime(View view) {

        EditText intervalEditText = (EditText) view.findViewById(R.id.interval);
        EditText shotsEditText = (EditText) view.findViewById(R.id.shots);

        String intervalString = intervalEditText.getText().toString();
        int interval = Integer.parseInt(intervalString);
        String shotsString = shotsEditText.getText().toString();
        int shots = Integer.parseInt(shotsString);

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
}

