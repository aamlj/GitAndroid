
package com.mjones.flag_quiz55;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends Activity {
    // keys for reading data from SharedPreferences
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";
    public static String PLAYERS = "pref_numberOfPlayers";
    private boolean phoneDevice = true; // used to force portrait mode
    private boolean preferencesChanged = true; // did preferences change?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        // determine screen size
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is a tablet, set phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false; // not a phone-sized device

        // if running on phone-sized device, allow only portrait orientation
        if (phoneDevice)
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } // end method onCreate

    // called after onCreate completes execution
    @Override


    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
            // now that the default preferences have been set,
            // initialize QuizFragment and start the quiz
            com.mjones.flag_quiz55.QuizFragment quizFragment = (com.mjones.flag_quiz55.QuizFragment)
                    getFragmentManager().findFragmentById(R.id.quizFragment);
            quizFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(
                    PreferenceManager.getDefaultSharedPreferences(this));
            //populateListView();
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }


    } // end method onStart


    // show menu if app is running on a phone or a portrait-oriented tablet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the default Display object representing the screen
        Display display = ((WindowManager)
                getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize

        // display the app's menu only in portrait orientation
        if (screenSize.x < screenSize.y) // x is width, y is height
        {
            getMenuInflater().inflate(R.menu.main, menu); // inflate the menu
            return true;
        } else
            return false;
    } // end method onCreateOptionsMenu

    // displays SettingsActivity when running on a phone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, com.mjones.flag_quiz55.SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }


    private EditText answerCapital;
    private View.OnClickListener buttonListener;

    {
        buttonListener = new View.OnClickListener() {
    public void onClick(View v) {
        Button button = (Button) v;
        answerCapital = (EditText) findViewById(R.id.continueButton);
        String[] capitals = getResources().getStringArray(R.array.capitals);
        String[] countries = getResources().getStringArray(R.array.Countries);
        switch (v.getId()) {

            case R.id.continueButton://configure the button
                for (int i = 0; i < capitals.length; i++)//set the indices equal to each other
                    for (int j = 0; j < countries.length; j++)
                        i = j;
                for (int i = 0; i < capitals.length; i++)
                    if (answerCapital.equals(capitals)) {//see if the answer matches any of the capitals

                        Toast toast = new Toast(getApplicationContext());//let user see the correct capital
                        toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                        toast.makeText(MainActivity.this, answerCapital.getText(), toast.LENGTH_LONG).show();
                    } else {
                        Toast toast = new Toast(getApplicationContext());//tell the user incorrect
                        toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                        toast.makeText(MainActivity.this, R.string.wrongAnswer, toast.LENGTH_LONG).show();
                        break;
           }
            case R.id.learn://button that connects to the internet
              String urlString = getString(R.string.searchURL) +
                    Uri.encode(countries.toString(), "");

                // create an Intent to launch a web browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW,//connect to the web
                        Uri.parse(urlString));

                startActivity(webIntent); // launches web browser to vie
                AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
                {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }; // end itemClickListener declaration
        }

    }

            // listener for changes to the app's SharedPreferences
            private OnSharedPreferenceChangeListener preferenceChangeListener =
                    new OnSharedPreferenceChangeListener() {
                        // called when the user changes the app's preferences
                        @Override
                        public void onSharedPreferenceChanged(
                                SharedPreferences sharedPreferences, String key) {
                            preferencesChanged = true; // user changed app settings

                            QuizFragment quizFragment = (QuizFragment)
                                    getFragmentManager().findFragmentById(R.id.quizFragment);

                            if (key.equals(CHOICES)) // # of choices to display changed
                            {
                                quizFragment.updateGuessRows(sharedPreferences);
                                quizFragment.resetQuiz();
                            } else {
                                if (key.equals(PLAYERS)) // regions to include changed
                                {
                                    Set<String> players =
                                            sharedPreferences.getStringSet(PLAYERS, null);
                                    if (players != null && players.size() > 0) {
                                        
                                        quizFragment.resetQuiz();

                                    } else if (key.equals(REGIONS)) // regions to include changed
                                    {
                                        Set<String> regions =
                                                sharedPreferences.getStringSet(REGIONS, null);


                                        if (regions != null && regions.size() > 0) {
                                            quizFragment.updateRegions(sharedPreferences);
                                            quizFragment.resetQuiz();
                                        } else // must select one region--set North America as default
                                        {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            assert regions != null;
                                            regions.add(
                                                    getResources().getString(R.string.default_region));
                                            editor.putStringSet(REGIONS, regions);
                                            editor.commit();
                                            Toast.makeText(MainActivity.this,
                                                    R.string.default_region_message,
                                                    Toast.LENGTH_SHORT).show();
                                        }


                                        Toast.makeText(MainActivity.this,
                                                R.string.restarting_quiz, Toast.LENGTH_SHORT).show();

                                    } // end method onSharedPreferenceChanged
                                }
                            }
                            // end anonymous inner class

                        }

                        ;// end class MainActivity


                    };
        };


    }
}