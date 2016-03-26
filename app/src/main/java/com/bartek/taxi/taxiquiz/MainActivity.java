package com.bartek.taxi.taxiquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.learn).setOnClickListener(v ->
                QuizActivity.start(this)
        );

        findViewById(R.id.quiz).setOnClickListener(v ->
                QuizActivity.start(this, 12, 2, true)
        );
    }


}
