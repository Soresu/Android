package com.fohadiszallas.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void StartQuiz(View view) {
        //Start new Activity
        Intent inent = new Intent(this, Quiz.class);
        startActivity(inent);
    }
}
