package edu.pdx.rsurya07.worldquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Author: Surya Ravikumar
 * @Date: 4/30/2019
 *
 * @Description: MainActivity that asks the user what type of quiz they want to take and displays options in a dropdown menu
 *               Activity also stores how many quizzed were completed by the user, and how many quiz they've cheated on using
 *               SharedPreferences
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner quizChose;
    private static final String QUIZ_TYPE = "edu.pdx.rsurya07.project2.quiztype";
    private static final String CORRECT_ANSWERS = "edu.pdx.rsurya07.project2.correct_answers";
    private static final String TOTAL_QS = "edu.pdx.rsurya07.project2.total_qs";
    private static final String USER_CHEAT = "edu.pdx.rsurya07.project2.user_cheat";
    private static final String RESULT_ACTIVITY = "edu.pdx.rsurya07.project2.result_activity";
    private boolean userCheated;

    private TextView quizesTakenText;
    private TextView quizesCheatedText;

    public static final String SHARED_PREFS = "edu.pdx.rsurya07.project2.shared_prefs";
    public static final String QUIZES_TAKEN = "edu.pdx.rsurya07.project2.quizes_taken";
    public static final String QUIZES_CHEATED_ON = "edu.pdx.rsurya07.project2.quizes_cheated_on";

    private int quizesTaken;
    private int quizesCheatedOn;

    /**
     * Method to display choices in dropdown menu
     */
    private void setChoices()
    {
        //code adopted from "Coding in Flow" YouTube channel
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.quizes,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quizChose.setAdapter(adapter);
    }

    /**
     * Method to display Quizes completed and quizes cheated on by user
     */
    private void updateTextView()
    {
        quizesTakenText.setText("Quizes taken: " + String.valueOf(quizesTaken));
        quizesCheatedText.setText("Quizes Cheated on: " + String.valueOf(quizesCheatedOn));
    }

    /**
     * @MainActivity - onCreate
     *  Get SharedPreferences Data
     *  Set drop down menu
     *  @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get IDs of variables
        quizChose = (Spinner) findViewById(R.id.spinner);
        quizesTakenText = (TextView) findViewById(R.id.quizes_taken);
        quizesCheatedText = (TextView) findViewById(R.id.quizes_cheated_on);

        //display spinned and choices and user history
        setChoices();
        loadData();
        updateTextView();

        quizChose.setOnItemSelectedListener(this);
    }

    /**
     * Method that responds to one of the option in dropdown menu selected
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        String text = adapterView.getItemAtPosition(i).toString() + String.format("%d", i);

        //Start quiz activity based on input
        //0th option is blank or prompt telling user to select choices here
        if(i > 0)
        {
            Intent intent = new Intent(MainActivity.this,
                    QuizActivity.class);

           intent.putExtra(QUIZ_TYPE, i);
           startActivityForResult(intent, 0);
        }
    }

    //Had to have this here to shutup warnings and errors
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * onActivityResult method checks which activity it returned from.
     * IF returned from QuizActivity, increment quizes taken and quized cheated on if cheated, and store it (SharedPreferences)
     * then call result activity to display result
     *
     * IF returning from resultActivity, do nothing - nothing needs to be done
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //reset dropdown menu
        quizChose.setAdapter(null);
        setChoices();

        if(data == null)
            return;

        else
        {
            //if returned from QuizActivity
            if(!data.getBooleanExtra(RESULT_ACTIVITY, false))
            {
                //store variables returned
                int correctAnswers = data.getIntExtra(CORRECT_ANSWERS, 0);
                int totalQs = data.getIntExtra(TOTAL_QS, 5);
                userCheated = data.getBooleanExtra(USER_CHEAT, false);

                //increment SharedPreferences variable counters
                quizesTaken++;

                if(userCheated)
                    quizesCheatedOn++;

                //...and store them and update display counter
                saveData();
                updateTextView();

                //call result activity with correct answers, total answers, and if user cheated
                Intent intent = new Intent(MainActivity.this,
                        ResultActivity.class);

                intent.putExtra(CORRECT_ANSWERS, correctAnswers);
                intent.putExtra(TOTAL_QS, totalQs);
                intent.putExtra(USER_CHEAT, userCheated);

                startActivityForResult(intent, 0);
            }
        }
    }

    /**
     * Method to save SharedPreferences data
     */
    private void saveData()
    {
        //code adopted from "Coding in Flow" YouTube channel

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //save quizes taken and quizes cheated on
        editor.putInt(QUIZES_TAKEN, quizesTaken);
        editor.putInt(QUIZES_CHEATED_ON, quizesCheatedOn);

        //save
        editor.apply();
    }

    /**
     * Method to load SharedPreferences data
     */
    private void loadData()
    {
        //code adopted from "Coding in Flow" YouTube channel

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        quizesTaken = sharedPreferences.getInt(QUIZES_TAKEN, 0);
        quizesCheatedOn = sharedPreferences.getInt(QUIZES_CHEATED_ON, 0);
    }
}
