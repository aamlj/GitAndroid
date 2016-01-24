package com.mjones.flag_quiz55;
//TODO
//MAKE A STRINGS FOLDER FOR STRINGS                             COMPLETE
// MAKE A COLOR FOLDER RED AND GREEN FOR CORRECT AND INCORRECT  COMPLETE
//MAKE A ARRAY FOLDER FOR REGION, GUESSES,AND FLAGS             COMPLETE
//MAKE A DIMENSION FOLDER FOR MARGIN, PADDING, SPACING, AND ANSWER SIZE    COMPLETE
//CREATE A FRAGMENT LAYOUT WITH 3,6,9 BUTTONS DEPENDING ON DEVICE AND DIFFICULTY    COMPLETE
//INCLUDE FLAG, QUESTION NUMBER, GUESS COUNTRY, BUTTONS, AND ANSWER IN FRAGMENT LAYOUT   COMPLETE
//CREATE A PREFERENCES FOLDER WHERE USER CAN CHOOSE REGIONS, KEYS, AND BUTTONS        COMPLETE
//CREATE ANIMATION WHEN THE USER GETS A INCORRECT GUESS (MAYBE SHAKING)         COMPLETE
//FIND DRAWABLES FOR FLAGS TO REPRESENT IMAGES                                     COMPLETE
//GIVE POINTS FOR ANSWERING CORRECTLY ON FIRST GUESS AND FEWER POINTS FOR OTHER GUESSES  COMPLETE
// CREATE A SHARED PREFERENCES FILE TO SAVE THE TOP FIVE SCORES   COMPLETE
//MAKE THE GAME MULTI PLAYER              COMPLETE
//MAKE A BONUS QUESTION MAYBE THE CAPITOL OF THE COUNTRY. IF CORRECT ADD 10 POINTS TO SCORE     COMPLETE
//AFTER A QUESTION IS ANSWERED CORRECTLY INCLUDE A LINK TO WIKIPEDIA WHERE USER CAN LEARN MORE ABOUT THE COUNTRY     COMPLETE
//ALLOW USER TO DECIDE WHEN TO MOVE TO NEXT FLAG     COMPLETE
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.mjones.flag_quiz55.R.string.question;

public class QuizFragment extends Fragment 
{

   private static final String TAG = "FlagQuiz Activity";//name of QuizFragment

   private static final int FLAGS_IN_QUIZ = 10; //number of flags in quiz

   private static final int POINTS_FOR_FIRST_CORRECT_ANSWERS = 5;//FIVE POINTS FOR FIRST BUTTON CLICK
   private static final int POINTS_FOR_SECOND_CORRECT_ANSWERS = 3;//THREE POINTS FOR SECOND BUTTON CLICK
   private static final int POINTS_FOR_THIRD_CORRECT_ANSWERS = 1;//ONE POINTS FOR THIRD BUTTON CLICK

   private static final String SCORES = "scores";//holds the scores
    
   private List<String> fileNameList; // flag names
   private List<String> quizCountriesList; // countries in quiz
   private Set<String> regionsSet; // regions in quiz
   private String correctAnswer; // correct country for the current flag
   private int totalGuesses; // number of guesses
   private int correctAnswers; // number of correct guesses
   private int firstCorrectAnswers = 0;//number of correct guesses on first try
   private int secondCorrectAnswers = 0;
   private int thirdCorrectAnswers = 0;
   private int guessRows; // number of rows displaying guess Buttons
   private int numberOfPlayers;//number of players in game
   private SecureRandom random; // randomize the quiz
   private Handler handler; // delay loading next flag
   private Animation shakeAnimation; // animation for incorrect guess
   private TextView questionNumberTextView; // shows current question #
   private ImageView flagImageView; // displays a flag
   private LinearLayout[] guessLinearLayouts; // rows of answer Buttons
   private TextView answerTextView; // displays Correct! or Incorrect!
   private int ButtonClickCount = 0; //Keep track of how many times a button is clicked in making a correct answer
   private int totalPoints = 0;
   private SharedPreferences topFiveHighScoresPreference;

  // configures the QuizFragment
    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);
      View view =
         inflater.inflate(R.layout.fragment_quiz, container, false);

       // get the SharedPreferences containing the high scores of the game
       getActivity();
       topFiveHighScoresPreference = getActivity().getSharedPreferences(SCORES, MODE_PRIVATE);

       fileNameList = new ArrayList<String>();//array list for flags
      quizCountriesList = new ArrayList<String>();//array list for countries
       String[] capitals = getResources().getStringArray(R.array.capitals);
       String[] countries = getResources().getStringArray(R.array.Countries);
       random = new SecureRandom();//to randomize
      handler = new Handler();

      // shake animation used for incorrect answers
      shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
         R.anim.incorrect_shake);
      shakeAnimation.setRepeatCount(3); // animation repeats 3 times for three wrong answers

      // get references to GUI
      questionNumberTextView =
         (TextView) view.findViewById(R.id.questionNumberTextView);//question number
      flagImageView = (ImageView) view.findViewById(R.id.flagImageView);//flag
      guessLinearLayouts = new LinearLayout[3];//guess buttons
      guessLinearLayouts[0] =
         (LinearLayout) view.findViewById(R.id.row1LinearLayout);
      guessLinearLayouts[1] =
         (LinearLayout) view.findViewById(R.id.row2LinearLayout);
      guessLinearLayouts[2] =
         (LinearLayout) view.findViewById(R.id.row3LinearLayout);
      answerTextView = (TextView) view.findViewById(R.id.answerTextView);

      // configure listeners for the guess Buttons


       for (LinearLayout row : guessLinearLayouts)
      {
         for (int column = 0; column < row.getChildCount(); column++)
         {
            Button button = (Button) row.getChildAt(column);
            button.setOnClickListener(guessButtonListener);
         }
      }

      // set questionNumberTextView's text
      questionNumberTextView.setText(
         getResources().getString(question, 1, FLAGS_IN_QUIZ));
      return view; // returns the fragment's view for display
   }

     public void updateGuessRows(SharedPreferences sharedPreferences)//preferences of user
   {
      // get the number of guess buttons that should be displayed
      String choices =
         sharedPreferences.getString(MainActivity.CHOICES, null);
      guessRows = Integer.parseInt(choices) / 3;

      // hide all guess button LinearLayouts
      for (LinearLayout layout : guessLinearLayouts)
         layout.setVisibility(View.INVISIBLE);

      // display appropriate guess button LinearLayouts
      for (int row = 0; row < guessRows; row++)
         guessLinearLayouts[row].setVisibility(View.VISIBLE);
   }
 // update based on values in SharedPreferences
   public void updateRegions(SharedPreferences sharedPreferences)
   {
      regionsSet = 
         sharedPreferences.getStringSet(MainActivity.REGIONS, null);
   }
// set up and start the next quiz
   public void resetQuiz()
   {      
      // get image file names for regions
      AssetManager assets = getActivity().getAssets(); 
      fileNameList.clear(); // clear list of image names
      
      try
      {
         // loop through each region
         for (String region : regionsSet)
         {
            // get a list of all flag image files in this region
            String[] paths = assets.list(region);

            for (String path : paths)
               fileNameList.add(path.replace(".png", ""));
         }
      }
      catch (IOException exception)
      {
         Log.e(TAG, "Error loading image file names", exception);
      }
      firstCorrectAnswers = 0;//reset number of correct guesses on first try
      secondCorrectAnswers = 0;//reset number of correct guesses on second try
      thirdCorrectAnswers = 0;//reset number of correct guesses on third try
      totalPoints = 0;//reset number of total points
      correctAnswers = 0; // reset the number of correct answers made
      totalGuesses = 0; // reset the total number of guesses
      quizCountriesList.clear(); // clear prior list of quiz countries
      
      int flagCounter = 1; //initialize counter
      int numberOfFlags = fileNameList.size(); 

      // add FLAGS_IN_QUIZ random file names to the quizCountriesList
      while (flagCounter <= FLAGS_IN_QUIZ) 
      {
         int randomIndex = random.nextInt(numberOfFlags); 


         // get the random file name
         String fileName = fileNameList.get(randomIndex);
         
         // if the region is enabled and it hasn't already been chosen
         if (!quizCountriesList.contains(fileName)) 
         {
            quizCountriesList.add(fileName); // add the file to the list
            ++flagCounter;

         }
      } 

      loadNextFlag(); // start the quiz
   } // end method

   // after a correct flag, load the next flag
   private void loadNextFlag() 
   {
      // get name of the next flag and remove it from the list
      String nextImage = quizCountriesList.remove(0);
      correctAnswer = nextImage; // update the correct answer
      answerTextView.setText(""); // clear answerTextView

      // display current question number
      questionNumberTextView.setText(
         getResources().getString(question,
            (correctAnswers + 1), FLAGS_IN_QUIZ));

      // region from the next image's name
      String region = nextImage.substring(0, nextImage.indexOf('-'));
       String capital = nextImage.substring(1, nextImage.indexOf('-'));
      // load next image from assets folder
      AssetManager assets = getActivity().getAssets();

      try
      {
         // get an InputStream to the asset representing the next flag
         InputStream stream =
            assets.open(region + "/" + nextImage + ".png");

         // load the asset and display on the flagImageView
         Drawable flag = Drawable.createFromStream(stream, nextImage);
         flagImageView.setImageDrawable(flag);
      }
      catch (IOException exception)
      {
         Log.e(TAG, "Error loading " + nextImage, exception);
      }

      Collections.shuffle(fileNameList); // shuffle file names

      // put the correct answer at the end of fileNameList
      int correct = fileNameList.indexOf(correctAnswer);
      fileNameList.add(fileNameList.remove(correct));

      // add guess Buttons based on the value of guessRows
      for (int row = 0; row < guessRows; row++) 
      {
         // place Buttons
         for (int column = 0; 
            column < guessLinearLayouts[row].getChildCount(); column++) 
         { 
            // get reference to Button
            Button newGuessButton = 
               (Button) guessLinearLayouts[row].getChildAt(column);
            newGuessButton.setEnabled(true);

            // get country name and set it as newGuessButton's text
            String fileName = fileNameList.get((row * 3) + column);
            newGuessButton.setText(getCountryName(fileName));
         } 
      } 
      
      // randomly replace one Button with the correct answer
      int row = random.nextInt(guessRows); // pick random row
      int column = random.nextInt(3); // pick random column
      LinearLayout randomRow = guessLinearLayouts[row]; // get the row
      String countryName = getCountryName(correctAnswer);
      ((Button) randomRow.getChildAt(column)).setText(countryName);    
   } // end method

   // parses the country flag file name and returns the country name
   private String getCountryName(String name)
   {
      return name.substring(name.indexOf('-') + 1).replace('_', ' ');
   }
    private String getCapitalName(String name)
    {
        return name.substring(name.indexOf('-') + 2).replace('_', ' ');
    }


    // called when a guess Button is touched
    private OnClickListener guessButtonListener; // end answerButtonListener

    {
        guessButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button guessButton = ((Button) v);
                String guess = guessButton.getText().toString();
                String answer = getCountryName(correctAnswer);
                ++totalGuesses; // increment number of guesses
                ButtonClickCount++;

                if (guess.equals(answer)) // if the guess is correct
                {

                    ++correctAnswers; // increment the number of correct answers

                    if (ButtonClickCount == 1)
                        firstCorrectAnswers++;
                   //
                    if (ButtonClickCount == 2)
                        secondCorrectAnswers++;
                    if (ButtonClickCount == 3)
                        thirdCorrectAnswers++;

                    ButtonClickCount = 0;

                    // display correct answer in green text
                    answerTextView.setText(answer + "!");
                    answerTextView.setTextColor(
                            getResources().getColor(R.color.correct_answer));

                    disableButtons(); // disable all buttons
                    // if the user has correctly identified flags
                    if (correctAnswers == FLAGS_IN_QUIZ) {
                        // Calculate the total points.
                        totalPoints = (firstCorrectAnswers * POINTS_FOR_FIRST_CORRECT_ANSWERS) +
                                (secondCorrectAnswers * POINTS_FOR_SECOND_CORRECT_ANSWERS) +
                                (thirdCorrectAnswers * POINTS_FOR_THIRD_CORRECT_ANSWERS);

                        saveHighScores(totalPoints);

                        // DialogFragment to display quiz stats and start new quiz

                             DialogFragment quizResults =
                                new DialogFragment() {
                                    // create an AlertDialog and return it
                                    @Override
                                    public Dialog onCreateDialog(Bundle bundle) {
                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(getActivity());
                                        builder.setCancelable(false);

                                        builder.setMessage(

                                                getResources().getString(R.string.results,
                                                        totalGuesses, firstCorrectAnswers, (1000 / (double) totalGuesses), totalPoints));



                                        // "Reset Quiz" Button
                                        builder.setPositiveButton(R.string.reset_quiz,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int id) {


                                                        resetQuiz();


                                                    }
                                                } // end anonymous inner class
                                        ); // end call to setPositiveButton

                                        return builder.create(); // return the AlertDialog
                                    } // end method onCreateDialog
                                }; // end DialogFragment anonymous inner class


                        // use FragmentManager to display the DialogFragment
                        quizResults.show(getFragmentManager(), "quiz results");
                    } else // answer is correct but quiz is not over
                    {

                        // load the next flag after a 1-second delay
                        handler.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        loadNextFlag();
                                    }
                                }, 2000); // 2000 milliseconds for 2-second delay
                    }
                } else // guess was incorrect
                {

                    flagImageView.startAnimation(shakeAnimation); // play shake

                    // display "Incorrect!" in red
                    answerTextView.setText(R.string.incorrect_answer);
                    answerTextView.setTextColor(
                            getResources().getColor(R.color.incorrect_answer));
                    guessButton.setEnabled(false); // disable incorrect answer
                }
            } // end method onClick
        };
    }

    // method that disables all answer Buttons
   private void disableButtons()
   {
      for (int row = 0; row < guessRows; row++)
      {
         LinearLayout guessRow = guessLinearLayouts[row];
         for (int i = 0; i < guessRow.getChildCount(); i++)
            guessRow.getChildAt(i).setEnabled(false);
      }
   }

   public ArrayList<String> saveHighScores(int score) {//method for saving the scores
       ArrayList<String> topFiveHighScoresList;

       // store the saved scores in an ArrayList
       topFiveHighScoresList = new ArrayList<String>(topFiveHighScoresPreference.getAll().keySet());

       // add the latest score to the list
       topFiveHighScoresList.add(Integer.toString(score));

       // sort the scores
       Collections.sort(topFiveHighScoresList, String.CASE_INSENSITIVE_ORDER);
            // remove the last score from the list if the list exceeds 5 items.
       if (topFiveHighScoresList.size() > 5)
           topFiveHighScoresList.remove(5);

       SharedPreferences.Editor preferencesEditor;
       preferencesEditor = topFiveHighScoresPreference.edit();

       preferencesEditor.clear();
       preferencesEditor.apply();
return topFiveHighScoresList;
   }


} // end class


