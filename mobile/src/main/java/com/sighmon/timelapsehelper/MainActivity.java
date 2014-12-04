package com.sighmon.timelapsehelper;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.sighmon.timelapsehelper.MESSAGE";
    public final static Integer PLAYBACK_FPS = 30;

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Set the min/max for the pickers.
            NumberPicker shootingDays = (NumberPicker) rootView.findViewById(R.id.shooting_days);
            shootingDays.setMaxValue(99);
            NumberPicker shootingHours = (NumberPicker) rootView.findViewById(R.id.shooting_hours);
            shootingHours.setMaxValue(23);
            NumberPicker shootingMinutes = (NumberPicker) rootView.findViewById(R.id.shooting_minutes);
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
            playbackFrames.setMaxValue(PLAYBACK_FPS);

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
}

