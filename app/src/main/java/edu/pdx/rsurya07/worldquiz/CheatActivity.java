package edu.pdx.rsurya07.worldquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @Author:  Surya Ravikumar
 * @Data:  4/30/2019
 *
 * @Description: This is the cheat activity. It asks the user if they want to really look at the answer, by asking 'Yes' or 'No'.
 *               If the user wants to cheat, it prints out the answer.
 *               If the user presses 'No' after saying 'Yes', it still counts as the user cheated.
 *               If the user presses 'No' the first time, asks the user to go back.
 *               If the user presses 'Yes' after saying 'No', the answer is displayed and counted as the user cheated.
 */
public class CheatActivity extends AppCompatActivity {
    private static final String ANSWER_CHEAT = "edu.pdx.rsurya07.project2.answer_cheat";
    private static final String USER_CHEAT = "edu.pdx.rsurya07.project2.user_cheat";
    private static final String ANS_TEXT = "edu.pdx.rsurya07.project2.ans_text";
    private static final String DISP_MSG = "edu.pdx.rsurya07.project2.disp_msg";

    private Intent data;
    private Button yesButton;
    private Button noButton;
    private TextView displaytext;
    private TextView answerText;
    private boolean cheat;
    private String textDisplay;
    private String textAns;

    /**
     * Function that saves certain variables to be obtained when onCreate is called again after being destroyed
     * Saves:
     *          1)The displayed answer - if it was displayed
     *          2)Display message to the user
     *          3)Save if user cheated
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ANS_TEXT, textAns);
        savedInstanceState.putString(DISP_MSG, textDisplay);
        savedInstanceState.putBoolean(USER_CHEAT, cheat);
    }

    /**
     * Function that is first called when activity is created
     * Gets saved variables from onSaveInstanceState if saved
     * if not, then initializes variables
     * Gets IDs of TextView and Button variables
     * Sets onClickListeneres for buttons
     * Save data to return to parent activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //get saved variables
        if(savedInstanceState != null)
        {
            textDisplay = savedInstanceState.getString(DISP_MSG);
            textAns = savedInstanceState.getString(ANS_TEXT);
            cheat = savedInstanceState.getBoolean(USER_CHEAT, false);
        }

        //if not saved, then initialize
        else
        {
            cheat = false;
            textDisplay = "Are you sure you want to go to the dark side...?";
            textAns = "";
        }

        //get IDs of TextView and Button instances
        displaytext = (TextView) findViewById(R.id.cheatText);
        displaytext.setText(textDisplay);

        answerText = (TextView) findViewById(R.id.cheatAnswerText);
        answerText.setText(textAns);

        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);

        //set return variables for parent activity
        //done here since, if the device was rotated, the state saved is still returned if user did
        //not interact after rotating
        data = new Intent();
        data.putExtra(USER_CHEAT, cheat);
        setResult(RESULT_OK, data);

        /**
         * Yes Button - onCliclListener
         * If yes button is pressed, display answer and set variable as user cheated
         * Set return variables to parent as user cheated
         */
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display message
                textDisplay = "...Well then you are really lost\n\nPress back to go back";
                displaytext.setText(textDisplay);

                //get answer passed in by parent and display
                textAns = getIntent().getStringExtra(ANSWER_CHEAT);
                answerText.setText(textAns);

                //user cheated
                cheat = true;

                //set return variables
                data = new Intent();
                data.putExtra(USER_CHEAT, cheat);
                setResult(RESULT_OK, data);
            }
        });

        /**
         * No button - onClickListener
         * If no button is pressed, check if user already cheated
         * IF user already cheated, then cheat variable doesnt change - stays as cheated
         * If user did not cheat, print a message (user can still cheat after pressing no, but will be counted as cheated if user proceeds to do so)
         */
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if user did not cheat
                if(!cheat)
                {
                    //display text
                    textDisplay = "You are a true member of the council!\n\nPress back to go back";
                    displaytext.setText(textDisplay);

                    //set answer test as empty string (useful when saved)
                    textAns = "";

                    //return user did not cheat
                    data.putExtra(USER_CHEAT, cheat);
                    setResult(RESULT_OK, data);
                }

                //if user already cheated
                else
                {
                    //just display saying they already cheated and do nothing
                    textDisplay = "You were the chosen one...but you already failed";
                    displaytext.setText(textDisplay);
                }
            }
        });
    }
}
