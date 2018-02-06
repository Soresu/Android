package com.fohadiszallas.quizapp.Classes;

import android.widget.RadioButton;

import com.fohadiszallas.quizapp.Enums.QuestionTypes;
import com.fohadiszallas.quizapp.Quiz;


/**
 * Created by Hunter on 2018.02.06..
 */

public class Question {
    public String Title;
    public String[] Answers;
    public String[] GoodAnswer;
    public QuestionTypes Type;

    public Question(String title, String[] answers, String[] goodAnswer, QuestionTypes type){
        Title = title;
        Answers = answers;
        GoodAnswer = goodAnswer;
        Type = type;

    }
}
