package edu.pdx.rsurya07.worldquiz;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: Surya Ravikumar
 * @Date: 4/30/2019
 *
 * @Description: QuizActivity that loads in the type of quiz user mentioned in the MainActivity
 *               Displays the questions and radio button for answers, buttons to submit, cheat &
 *               go to the next question.
 *               It also records the number of questions user answered correctly, to pass back to the parent
 *               Options are disabled once user submits the answer
 *
 *               Working:
 *                  1)Display question:
 *                      i)User cheat - calls cheatActivity
 *                      ii)User enters answers
 *                          a)Submit - displays if user was right, and radio buttons are disabled to not reenter answer
 *                          b)Next - moves on to next question after checking if user entered answers was correct - doesn't display if answer was right or wrong
 *                          c)Cheat after pressing submit - does let user cheat, but prints the answer
 *                          d)Submit or next - without choosing an option, ask user to enter answer first
 */
public class QuizActivity extends AppCompatActivity {

    private static final String QUIZ_TYPE = "edu.pdx.rsurya07.project2.quiztype";
    private static final String CORRECT_ANSWERS = "edu.pdx.rsurya07.project2.correct_answers";
    private static final String TOTAL_QS = "edu.pdx.rsurya07.project2.total_qs";
    private static final String ANSWER_CHEAT = "edu.pdx.rsurya07.project2.answer_cheat";
    private static final String USER_CHEAT = "edu.pdx.rsurya07.project2.user_cheat";
    private static final String RESULT_ACTIVITY = "edu.pdx.rsurya07.project2.result_activity";
    private static final String QUESTION_INDEX = "edu.pdx.rsurya07.project2.question_index";
    private static final String OPTION_LOCK = "edu.pdx.rsurya07.project2.option_lock";
    private static final String RESULT_TEXT = "edu.pdx.rsurya07.project2.result_text";

    private RadioGroup optionsGroup;
    private RadioButton options;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private Button submitButton;
    private Button nextButton;
    private Button cheatButton;
    private TextView questionText;
    private int quizChoice;
    private String[] qsArray;
    private String[] ansArray;
    private String[][] ansChoices;
    private int numOfQs;
    private String resultText;
    private String quizType;
    private TextView result;
    private TextView quiz;
    private boolean enableOptions;
    private boolean answerShown;

    private int correctAnswers;

    private int questionIndex;

    /**
     * Save instance variables when activity is destroyed
     * Saves:
     *      1) If user cheated
     *      2)Number of correct answers
     *      3)Question number
     *      4)If radio button have been disabled
     *      5)Result message
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(USER_CHEAT, answerShown);
        savedInstanceState.putInt(CORRECT_ANSWERS, correctAnswers);
        savedInstanceState.putInt(QUESTION_INDEX, questionIndex);
        savedInstanceState.putBoolean(OPTION_LOCK, enableOptions);
        savedInstanceState.putString(RESULT_TEXT, resultText);
    }

    /**
     * Method that reads a JSON file to load the quiz
     * Method parses a JSON file based on the user input
     * Reads in number of question in the quiz
     * Using number of question, declare question, answer and choices arrays
     * Reads in the questions and stores it in the question array
     * Reads in the correct answer and stores it in the answers array
     * Reads in the choices and stores it in a 2D choices array
     * @param choice is the user entered choice for quiz type. Each choice number based on the order in the dropdown menu represent a quiz.
     */
    private void loadQuiz(int choice)
    {
        //declare JSON variables
        JSONParser parser = new JSONParser();
        Object obj;
        String fileName;

        //set filename with respect to user input
        switch(choice)
        {
            case 1: fileName = "SportsQuiz.json"; break;
            case 2: fileName = "HistoryQuiz.json"; break;
            case 3: fileName = "GeographyQuiz.json"; break;
            case 4: fileName = "HumanBodyQuiz.json"; break;
            case 5: fileName = "MusicQuiz.json"; break;
            case 6: fileName = "ScienceQuiz.json"; break;
            case 7: fileName = "GeneralKnowledgeQuiz.json"; break;

            default: fileName = "GeneralKnowledgeQuiz.json"; break;
        }

        //try to parse the file
        try
        {
            //open file
            InputStream is = getAssets().open(fileName);
            obj = parser.parse(new InputStreamReader(is));
            JSONArray array = new JSONArray();
            array.add(obj);

            JSONObject jsnObj = (JSONObject) array.get(0); //get the JSON file at index 0 - theres only 1 in this case

            long noq = (long) jsnObj.get("numOfQs");    //read number of questions
            numOfQs = (int) noq;

            quizType = (String) jsnObj.get("title");    //get the quiz title
            quiz.setText(quizType);

            //declare arrays for questions, answers, choices using number of questions
            qsArray = new String [numOfQs];
            ansArray = new String [numOfQs];
            ansChoices = new String [numOfQs][4];

            //get the quiz items which has question, answer and choices
            JSONArray quizitems = (JSONArray) jsnObj.get("quizitems");
            JSONObject items;
            JSONArray ansItems;

            //loop over quiz items and read question, answer and choices
            for(int i = 0; i < numOfQs; i++)
            {
                items = (JSONObject) quizitems.get(i);
                ansItems = (JSONArray) items.get("choices");
                qsArray[i] = (String) items.get("questiontext");
                ansArray[i] = (String) items.get("answertext");

                for(int j = 0; j < 4; j++)
                    ansChoices[i][j] = (String) ansItems.get(j);
            }
        }

        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to display question and option using the current index
     */
    private void putQuestion()
    {
        //read question array and choices array to dipsplay
        questionText.setText(qsArray[questionIndex]);
        option1.setText(ansChoices[questionIndex][0]);
        option2.setText(ansChoices[questionIndex][1]);
        option3.setText(ansChoices[questionIndex][2]);
        option4.setText(ansChoices[questionIndex][3]);

        result.setText(resultText);
    }

    /**
     * Method to check if user entered answer is correct
     * If correct, then increment correct answers counter
     * @param userAns the option user chose as a String variable
     * @param correctAns  the correct answer as a String variable
     * @param next  variable used to update correct answer counter when "Next" button is pressed. To not overcount when "Submit" button is pressed.
     */
    private void checkAnswer(String userAns, String correctAns, boolean next)
    {
        //if correct answer
        if(userAns.equals(correctAns))
        {
            //if next button pressed then increment correct answers
            if(next)
            {
                correctAnswers++;
            }

            //display correct message
            resultText = "Right decision you've made";
            result.setText(resultText);
        }

        //if answer wrong, then display wrong message
        else
        {
            resultText = "Wrong you are";
            result.setText(resultText);
        }
    }


    /**
     * method to enable or disable radio buttons so user cannot re neter after submitting
     * @param option true - enable radio buttons, false - disable radio buttons
     */
    private void enableUserInput(boolean option)
    {
        option1.setEnabled(option);
        option2.setEnabled(option);
        option3.setEnabled(option);
        option4.setEnabled(option);
        enableOptions = option;
    }

    /**
     * After getting result from child activity - CheatActivity in this case
     * Get parameters if user cheated
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data == null)
            return;

        else
        {
            if(answerShown == false)
            {
                answerShown = data.getBooleanExtra(USER_CHEAT, false);
            }
        }
    }

    /**
     * After creating activity
     * Get TextView, Button IDs
     * Set onClickListeners for buttons
     * read saved variables if saved, else initialize them
     * LoadQuiz, DisplayQuestion and enable or disable user input
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        optionsGroup = (RadioGroup) findViewById(R.id.radioGroup);
        option1 = (RadioButton) findViewById(R.id.choice1);
        option2 = (RadioButton) findViewById(R.id.choice2);
        option3 = (RadioButton) findViewById(R.id.choice3);
        option4 = (RadioButton) findViewById(R.id.choice4);
        submitButton = (Button) findViewById(R.id.submitButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        cheatButton = (Button) findViewById(R.id.cheatButton);
        questionText = (TextView) findViewById(R.id.questionText);
        result = (TextView) findViewById(R.id.resultMessage);
        quiz = (TextView) findViewById(R.id.quizType);

        //get saved variables
        if(savedInstanceState != null)
        {
            questionIndex = savedInstanceState.getInt(QUESTION_INDEX, 0);
            correctAnswers = savedInstanceState.getInt(CORRECT_ANSWERS, 0);
            resultText = savedInstanceState.getString(RESULT_TEXT);
            answerShown = savedInstanceState.getBoolean(USER_CHEAT);
            enableOptions = savedInstanceState.getBoolean(OPTION_LOCK);
        }

        //if not saved, then initialize them
        else
        {
            questionIndex = 0;
            correctAnswers = 0;
            enableOptions = true;
            resultText = "Click on submit to check";
            answerShown = false;
        }

        //get user selected quiz from parent
        quizChoice = getIntent().getIntExtra(QUIZ_TYPE, 1);

        loadQuiz(quizChoice);   //read JSON file and initialize question, answer, choices arrays
        enableUserInput(enableOptions); //enable or disable user input - disabled only when saved state is disabled
        putQuestion();  //display question and choices

        /**
         * @CheatButton - onCLick
         *      if user entered cheat button, then allow user to view answer
         *      Calls CheatActivity if user has not submitted answer
         *      IF user has already submitted, just display answer - not counted as cheating since answer already recorded
         */
        cheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if user has not submitted
                if(enableOptions)
                {
                    //call CheatActivity
                    Intent intent = new Intent(QuizActivity.this,
                            CheatActivity.class);

                    intent.putExtra(ANSWER_CHEAT, ansArray[questionIndex]);
                    startActivityForResult(intent, 0);
                }

                //If submitted, the display
                else
                {
                    resultText = "Chosen a path, you have...\nLet you cheat, I won't\nKnowledge is power, is the saying...\n\"" + ansArray[questionIndex] + "\" is the choice you had to make...";
                    result.setText(resultText);
                }
            }
        });

        /**
         * @SubmitButtom - onClick
         * Calls check button method if user clicked on submitted
         */
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkButton(v);
            }
        });

        /**
         * @NextButton - onClick
         * If user pressed submit button, check if user selected an option, update correct answer counter, enable inputs, clear radio buttons and move onto next question
         */
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //get radio buttton status
                int radioId = optionsGroup.getCheckedRadioButtonId();
                options = findViewById(radioId);
                optionsGroup.clearCheck();

                //if no option selected then ask for user input
                if(radioId == -1)
                {
                    resultText = "You must choose a path before you proceed...";
                    result.setText(resultText);
                }

                //if user entered something
                else
                {
                    //check answer, and increment correct answer counter if correct
                    checkAnswer(String.format("%s",options.getText()), ansArray[questionIndex], true);

                    questionIndex++; //increment question index to move onto next question

                    //clear radio buttons
                    option1.setChecked(false);
                    option2.setChecked(false);
                    option3.setChecked(false);
                    option4.setChecked(false);

                    //if not the last question then update question and print defualt string, and enable user inputs again
                    if(questionIndex < numOfQs)
                    {
                        resultText = "Click on submit to check";
                        putQuestion();
                        enableUserInput(true);
                    }

                    //if that was the last question, then return
                    else
                    {
                        //put correct answers, total question, and if user cheated in intent
                        Intent data = new Intent();
                        data.putExtra(CORRECT_ANSWERS, correctAnswers);
                        data.putExtra(TOTAL_QS, numOfQs);
                        data.putExtra(USER_CHEAT, answerShown);

                        //used by MainActivity to check if returned from this activity or result activity
                        data.putExtra(RESULT_ACTIVITY, false);
                        setResult(RESULT_OK, data);

                        //close activity
                        finish();
                    }
                }
            }
        });
    }

    /**
     * Method called when submit button pressed
     * Checks for user input
     *      if user entered something, check if answer correct
     *      if no user input, then ask for user input
     * @param v
     */
    public void checkButton(View v)
    {
        //get radio button status
        int radioId = optionsGroup.getCheckedRadioButtonId();
        options = findViewById(radioId);

        //if no user input, ask for input
        if(radioId == -1)
        {
            resultText = "You must choose a path before you proceed...";
            result.setText(resultText);
        }

        //if user entered something, then check answer but do not increment correct answer counter, also disable radio buttons so user cant change answer
        else
        {
            enableUserInput(false);
            checkAnswer(String.format("%s",options.getText()), ansArray[questionIndex], false);
        }
    }
}
