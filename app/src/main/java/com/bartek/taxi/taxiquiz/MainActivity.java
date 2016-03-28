package com.bartek.taxi.taxiquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bartek.taxi.taxiquiz.model.QuestionFactory;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.learnStreets).setOnClickListener(v ->
                QuizActivity.start(QuestionFactory.Type.STREETS, this)
        );

        findViewById(R.id.quizStreets).setOnClickListener(v ->
                QuizActivity.start(this, 12, 2, true, QuestionFactory.Type.STREETS)
        );

        findViewById(R.id.learnPlaces).setOnClickListener(v ->
                QuizActivity.start(QuestionFactory.Type.PLACES, this)
        );

        findViewById(R.id.quizPlaces).setOnClickListener(v ->
                QuizActivity.start(this, 12, 2, true, QuestionFactory.Type.PLACES)
        );
    }


}
