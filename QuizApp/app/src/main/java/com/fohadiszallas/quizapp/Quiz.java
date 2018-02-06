package com.fohadiszallas.quizapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fohadiszallas.quizapp.Classes.Question;
import com.fohadiszallas.quizapp.Enums.QuestionTypes;

import java.util.ArrayList;
import java.util.List;



public class Quiz extends AppCompatActivity {

    Integer QuizId = 0;
    String[] Quiz1Answers = {"KitKat", "Lollipop", "Marshmallow", "Nougat", "Oreo"};
    List<Question> Questions = new ArrayList<>();
    String[] AnswerTags = {"textAnswer", "radioAnswers", "checkboxAnswers", "results"};
    List<String> UserAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        LoadQuestions();
        LoadNextQuestion();
    }

    private void LoadQuestions() {
        //Question 1
        List<String> answers = new ArrayList<>();
        String currentVersion = GetCurrentVersion();
        answers.add(currentVersion);
        for (Integer i = 0; i < 3; i++) {
            if (currentVersion != Quiz1Answers[i]) {
                answers.add(Quiz1Answers[i]);
            }
        }
        //Collections.shuffle(answers);
        Questions.add(new Question("What is your android version?", answers.toArray(new String[0]), new String[]{answers.get(0)}, QuestionTypes.RADIO));

        //Question 2
        String[] answers2 = new String[]{"android packages", "Android packaging kit", "Android pack", "None of the above"};
        Questions.add(new Question("What is APK in android?", answers2, new String[]{answers2[1]}, QuestionTypes.RADIO));

        //Question 3
        String[] answers3 = new String[]{"Red", "Green", "Blue", "Yellow"};
        Questions.add(new Question("Which color used in Google logo design?", answers3, new String[]{answers3[0], answers3[1], answers3[2], answers3[3]}, QuestionTypes.CHECKBOX));

        //Question 4
        String[] answers4 = new String[]{"2018.02.01", "2018.01.01", "2018.02.06", "2018.01.17"};
        Questions.add(new Question("When did I publish this project?", answers4, new String[]{answers4[2]}, QuestionTypes.RADIO));

        //Question 5
        Questions.add(new Question("Which class represents character strings?", new String[]{}, new String[]{"String"}, QuestionTypes.TEXT));

        //Question 6
        String[] answers6 = new String[]{"Gábor", "Günter", "Adam", "John"};
        Questions.add(new Question("What is my name?", answers6, new String[]{answers6[0]}, QuestionTypes.RADIO));

    }

    public void NextQuestion(View view) {
        //After reset
        if(QuizId == -1){
            QuizId = 0;
            LoadNextQuestion();
            return;
        }
        //Adding answer from user
        String answer = "";
        Question previousQuestion = Questions.get(QuizId);
        switch (previousQuestion.Type) {
            case RADIO:
                RadioGroup radioButtonGroup = (RadioGroup) findViewById(R.id.radioButtonGroup);
                int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) radioButtonGroup.findViewById(radioButtonID);
                if (radioButton != null) {
                    answer = radioButton.getText().toString();
                }
                radioButtonGroup.clearCheck();
                break;
            case CHECKBOX:
                //Radio should be this too
                List<String> checkedAnswers = new ArrayList<>();
                for (Integer i = 0; i < 4; i++) {
                    int id = getResources().getIdentifier("checkbox" + i, "id", getPackageName());
                    CheckBox possibleAnswer = (CheckBox) findViewById(id);
                    if (possibleAnswer.isChecked()) {
                        checkedAnswers.add(possibleAnswer.getText().toString());
                        possibleAnswer.setChecked(false);
                        possibleAnswer.setSelected(false);
                    }
                }
                //Concat to display
                answer = TextUtils.join(", ", checkedAnswers);
                break;
            case TEXT:
                TextView answerTextView = (TextView) findViewById(R.id.text0);
                answer = answerTextView.getText().toString();
                SetSingleAnswer(null);
                break;
            default:

        }
        //No answer given
        if (answer == "") {
            return;
        }
        UserAnswers.add(answer);

        //There are more questions
        if (QuizId < Questions.size() - 1) {
            QuizId++;
            LoadNextQuestion();
        } else {
            HideViews("results");
            SetQuestionTitle("Results", "");
            LinearLayout resultsView = (LinearLayout) findViewById(R.id.results);
            //Remove views from previous turn
            if(resultsView.getChildCount() > 0){
                resultsView.removeAllViews();
            }
            Integer goodAnsers = 0;
            LinearLayout block = new LinearLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
            block.setLayoutParams(layoutParams);
            block.setOrientation(LinearLayout.VERTICAL);
            for (Integer i = 0; i < Questions.size(); i++) {

                Question q = Questions.get(i);
                //Question
                AddTextToResults(block, q.Title, 15, Typeface.BOLD, "#000000");
                //Good answers
                String goodAnswer = TextUtils.join(", ", q.GoodAnswer);
                AddTextToResults(block, "Correct answer: " + goodAnswer, 12, Typeface.NORMAL, "#000000");
                //User answers
                String userAnswer = UserAnswers.get(i);
                AddTextToResults(block, "Your answer: " + userAnswer, 12, Typeface.NORMAL, "#000000");
                //Is it Correct?
                if (userAnswer.equals(goodAnswer)) {
                    AddTextToResults(block, "Correct :)", 12, Typeface.BOLD, "#34A853");
                    goodAnsers++;
                } else {
                    AddTextToResults(block, "Incorrect :(", 12, Typeface.BOLD, "#EA4335");
                }
            }
            AddTextToResults(block, "Score: " + goodAnsers + "/" + Questions.size(), 15, Typeface.BOLD, "#000000");
            Toast.makeText(getBaseContext(), "Your score is: " + goodAnsers + "/" + Questions.size(),
                    Toast.LENGTH_SHORT).show();
            resultsView.addView(block);
            QuizId = -1;
            UserAnswers.clear();
            SetSingleAnswer("");
        }
    }

    public void AddTextToResults(LinearLayout resView, String text, Integer size, Integer tf, String color) {

        TextView line = new TextView(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        line.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        line.setTypeface(null, tf);
        line.setText(text);
        line.setTextColor(Color.parseColor(color));
        resView.addView(line);
    }

    private void LoadNextQuestion() {
        Question currentQuestion = Questions.get(QuizId);
        SetQuestionTitle("Question " + (QuizId + 1), currentQuestion.Title);

        //Hide unused views that not contains this type
        HideViews(currentQuestion.Type.name());

        switch (currentQuestion.Type) {
            case RADIO:
                SetMultipleAnsers(currentQuestion, "radio");
                break;
            case CHECKBOX:
                SetMultipleAnsers(currentQuestion, "checkbox");
                break;
            case TEXT:
                //Reset
                SetSingleAnswer("");
                break;
            default:

        }
    }

    public void HideViews(String name) {
        //Hide unused views
        for (Integer i = 0; i < AnswerTags.length; i++) {
            int id = getResources().getIdentifier(AnswerTags[i], "id", getPackageName());
            View AnswerView = (View) findViewById(id);
            if (AnswerTags[i].toLowerCase().contains(name.toLowerCase())) {

                AnswerView.setVisibility(View.VISIBLE);
            } else {
                AnswerView.setVisibility(View.GONE);
            }
        }
    }

    public void SetMultipleAnsers(Question current, String type) {
        for (Integer i = 0; i < current.Answers.length; i++) {
            //Get the question
            String answer = current.Answers[i];
            //Get the RadioButton by ID from layout and set the value
            int id = getResources().getIdentifier(type + i, "id", getPackageName());
            CompoundButton answerButton = (CompoundButton) findViewById(id);
            answerButton.setText(answer);
        }
    }

    private void SetSingleAnswer(String answer) {
        TextView answerTextView = (TextView) findViewById(R.id.text0);
        answerTextView.setText(answer);
    }

    private void SetQuestionTitle(String mainTitle, String title) {
        TextView questionTextView = (TextView) findViewById(R.id.question);
        questionTextView.setText(title);
        TextView mainTitleTextView = (TextView) findViewById(R.id.mainTitle);
        mainTitleTextView.setText(mainTitle);
    }

    public static String GetCurrentVersion() {
        double release = Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
        String codeName = "Other";//below Jelly bean OR above Oreo
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if (release < 5) {
            codeName = "Kit Kat";
        } else if (release < 6) {
            codeName = "Lollipop";
        } else if (release < 7) {
            codeName = "Marshmallow";
        } else if (release < 8) {
            codeName = "Nougat";
        } else if (release < 9) {
            codeName = "Oreo";
        }
        return codeName;
    }

}


