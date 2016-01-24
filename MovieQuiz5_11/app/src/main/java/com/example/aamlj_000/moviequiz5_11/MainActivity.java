package com.example.aamlj_000.moviequiz5_11;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    private String[] questions;//array for the questions
    private String[] answers;//array for the answers
    private int currentQuestion;//counter for the questions
    private Button getAnswerButton;//button for the answer
    private Button nextQuestionButton;//button to continue
    private TextView questionTextView;//too see the question
    private TextView answerTextView;//to see the answer
    private EditText answerText;//input prompt for user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     questionsAndAnswers();//call methods
        showQuestion();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    public void questionsAndAnswers() {//the questions array
        questions = new String[]{"The fast and the ?", "Fifty Shades of ?", "Bruce ?", "? Phsyco", "Dumb and ?", "Top ?", "American ?", "Menace to ?",
                "Lethal ?", "Die ?", "Taledega ?", "Hustle and ?", "Pulp ?", "Lord of the ?", "Harry ?"};
//the answers array
        answers = new String[]{"Furious", "Grey", "Almighty", "American", "Dumber", "Gun", "Pie", "Society", "Weapon", "Hard", "Nights", "Flow",
                "Fiction", "Rings", "Potter"};
        currentQuestion = -1;//set to -1 so we can set to 0 after each question
        getAnswerButton = (Button) findViewById(R.id.answerButton);
        nextQuestionButton = (Button) findViewById(R.id.button);
        questionTextView = (TextView) findViewById(R.id.QuestionTextView);//pair varibles with the gui
        answerTextView = (TextView) findViewById(R.id.answerText);

        answerText = (EditText) findViewById(R.id.editText);


        getAnswerButton.setOnClickListener(new View.OnClickListener() {//listener for answer button
            @Override
            public void onClick(View v) {
                checkAnswer();

            }
        });


        nextQuestionButton.setOnClickListener(new View.OnClickListener() {//listener for the question button
            @Override
            public void onClick(View v) {
                showQuestion();            }
        });
    }

    private void showQuestion() {//method to assign new question each time
       currentQuestion++;
        if(currentQuestion==questions.length)
            currentQuestion = 0;
        questionTextView.setText(questions[currentQuestion]);

        answerTextView.setText("");//clear the text
        answerText.setText("");
    }

    private void checkAnswer() {//check if the answer is correct and hide keyboard
      isCorrect();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(answerText.getWindowToken(), 0);

    }

    public boolean ifAnswerCorrect(String answer) {//if the answer is correct then set it equal to array

           return (answer.equalsIgnoreCase(answers[currentQuestion]));

    }


    public void isCorrect(){//test if answer correct and display accordingly

String answer = answerText.getText().toString();//if correct
    if(ifAnswerCorrect(answer))
        updateCorrectDisplay();


    else
updateWrongDisplay();//if incorrect


}

    public void updateWrongDisplay () {//call when answer is wrong
       answerTextView.setTextColor(Color.RED);
        answerTextView.setText("WRONG  the correct answer was " + (answers[currentQuestion]));
    }
    public void updateCorrectDisplay () {//call when answer is right
        answerTextView.setTextColor(Color.GREEN);
        answerTextView.setText("CORRECT!");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
