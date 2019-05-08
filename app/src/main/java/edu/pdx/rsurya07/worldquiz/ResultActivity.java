package edu.pdx.rsurya07.worldquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * @Author: Surya Ravikumar
 * @Date: 4/30/2019
 *
 * @Description: Result activity that displays the user score and some message that corresponds to the
 *               score and if user cheated
 */
public class ResultActivity extends AppCompatActivity {

    private static final String greatScoreNoCheat = "You're a wizard 'arry";
    private static final String greatScoreCheat = "Do not defy the council, Master, not again";
    private static final String mehScoreNoCheat = "You will be a Jedi. I promise";
    private static final String mehScoreCheat = "When you play a game of thrones you win or you die";
    private static final String lowScoreNoCheat = "It aint much work, but it's honest work";
    private static final String lowScoreCheat = "He came, he cheated, he still failed";
    private static final String CORRECT_ANSWERS = "edu.pdx.rsurya07.project2.correct_answers";
    private static final String TOTAL_QS = "edu.pdx.rsurya07.project2.total_qs";
    private static final String USER_CHEAT = "edu.pdx.rsurya07.project2.user_cheat";
    private static final String RESULT_ACTIVITY = "edu.pdx.rsurya07.project2.result_activity";

    private TextView appreciationMessage;
    private TextView scoreDisplay;

    private int correctAnswers;
    private  int totalQs;
    private boolean answerShown;
    private double percent;

    /**
     * When activity is created, get TextView variables and IDs
     * Get the score percent using values given by parent activity
     * Get user cheated variable
     *
     * Score ranges: >80% Great score
     *               >40% Ok Score
     *               > 0% Low score
     *
     *               Each range has 2 messages - 1 if user cheated and other if not
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //get values from parent
        //answers answered correctly
        correctAnswers = getIntent().getIntExtra(CORRECT_ANSWERS, 0);

        //total question in the quiz
        totalQs = getIntent().getIntExtra(TOTAL_QS, 1);

        //if user cheated
        answerShown = getIntent().getBooleanExtra(USER_CHEAT, false);

        //calculate percent
        percent = (correctAnswers * 100)/totalQs;

        //get TextView IDs
        appreciationMessage = findViewById(R.id.resultAppreciation);
        scoreDisplay = findViewById(R.id.displayScore);

        //print messages
        if(percent > 80)
        {
            if(answerShown)
            {
                appreciationMessage.setText(greatScoreCheat);
            }

            else
            {
                appreciationMessage.setText(greatScoreNoCheat);
            }
        }

        else if(percent > 40)
        {
            if(answerShown)
            {
                appreciationMessage.setText(mehScoreCheat);
            }

            else
            {
                appreciationMessage.setText(mehScoreNoCheat);
            }
        }

        else
        {
            if(answerShown)
            {
                appreciationMessage.setText(lowScoreCheat);
            }

            else
            {
                appreciationMessage.setText(lowScoreNoCheat);
            }
        }

        scoreDisplay.setText("Your score is: " + String.format("%.2f", percent) + "%");

        //return to user the activity returned from the "Result activity" - used by the parent activity (MainActivity in this case)
        Intent data = new Intent();
        data.putExtra(RESULT_ACTIVITY, true);
        setResult(RESULT_OK, data);
    }
}
