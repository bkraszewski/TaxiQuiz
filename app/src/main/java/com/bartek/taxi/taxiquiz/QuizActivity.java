package com.bartek.taxi.taxiquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bartek.taxi.taxiquiz.db.DbHelper;
import com.bartek.taxi.taxiquiz.entity.ExamScore;
import com.bartek.taxi.taxiquiz.entity.QuestionData;
import com.bartek.taxi.taxiquiz.fragment.QuestionFragment;
import com.bartek.taxi.taxiquiz.model.QuestionFactory;
import com.bartek.taxi.taxiquiz.model.Scorer;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuizActivity extends Activity {

    private static final String SIZE = "size";
    private static final String RANDOM = "random";
    private static final String ERRORS_LIMIT = "errorLimit";
    private static final String TYPE = "type";

    private boolean quizALive = true;
    private long startDate;

    public static void start(Context context, int size, int errorLimit, boolean random, QuestionFactory.Type type) {
        Intent starter = new Intent(context, QuizActivity.class);
        starter.putExtra(SIZE, size);
        starter.putExtra(RANDOM, random);
        starter.putExtra(ERRORS_LIMIT, errorLimit);
        starter.putExtra(TYPE, type);
        context.startActivity(starter);
    }

    public static void start(QuestionFactory.Type type, Context context) {
        start(context, -1, Integer.MAX_VALUE, false, type);
    }

    private List<QuestionData> questions = new ArrayList<>();

    @InjectView(R.id.btnNext)
    protected View btnNext;

    @InjectView(R.id.tvProgress)
    protected TextView tvProgress;

    @InjectView(R.id.tvScore)
    protected TextView tvScore;

    private int currentQuestion = -1;

    private int correctAnswers;
    private int wrongAnswers;
    private int errorsLimit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.inject(this);

        boolean isRandom = getIntent().getBooleanExtra(RANDOM, false);
        errorsLimit = getIntent().getIntExtra(ERRORS_LIMIT, 0);
        int size = getIntent().getIntExtra(SIZE, -1);
        QuestionFactory.Type type = (QuestionFactory.Type) getIntent().getSerializableExtra(TYPE);
        QuestionFactory factory = QuestionFactory.getInstance();

        if (size == -1) {
            questions = factory.getQuestions(type, isRandom);
        } else {
            questions = factory.getQuestions(type, isRandom, size);
        }

        btnNext.setOnClickListener(v -> onNextQuestion());
        startDate = System.currentTimeMillis();
        onNextQuestion();
    }

    private void onNextQuestion() {
        if (!quizALive) {
            return;
        }

        currentQuestion++;
        btnNext.setEnabled(false);
        if (currentQuestion < questions.size()) {
            createQuestionForLine(currentQuestion);
            updateProgress();
            updateScore();
        } else {
            onFinish();
        }
    }

    private void onGameOver() {
        quizALive = false;

        new AlertDialog.Builder(this)
                .setTitle("Nie zdałeś!")
                .setMessage("Popelniles za duzo bledow, pocwicz!")
                .create()
                .show();
    }

    private void onFinish() {
        quizALive = false;
        storeScore();

        if (wrongAnswers > errorsLimit) {
            onGameOver();
        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Zdałeś!")
                    .setMessage("Gratuluje! Trenuj dalej!")
                    .create()
                    .show();
        }
    }

    private void storeScore() {
        try {
            Dao<ExamScore, String> store = DbHelper.getInstance(this).getDao(ExamScore.class);
            store.create(new ExamScore(questions.size(), correctAnswers, startDate, System.currentTimeMillis()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProgress() {
        tvProgress.setText(String.format("Pytanie: %d/%d", currentQuestion + 1, questions.size()));
    }

    private void updateScore() {
        tvScore.setText(String.format("Poprawne: %d Błędne: %d", correctAnswers, wrongAnswers));
    }

    private void createQuestionForLine(int index) {
        QuestionData question = questions.get(index);
        QuestionFragment fragment = QuestionFragment.newInstance(question);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.questionContainer, fragment)
                .commit();

        fragment.setListener(new QuestionFragment.OnQuestionSubmitedListener() {

            @Override
            public void onQuestionSubmited(QuestionData question) {
                QuestionData score = Scorer.calculateScore(question);
                fragment.showScore(score);
                fragment.enableMap();
                btnNext.setEnabled(true);
                recalculateScore(score);
                updateScore();
            }

            @Override
            public void showMap(QuestionData question) {
                MapActivity.start(QuizActivity.this, question.place);
            }
        });
    }

    private void recalculateScore(QuestionData score) {
        boolean correct = true;
        for (QuestionData.Question question : score.questions) {
            correct = correct && question.wrongAnswer == -1;
        }
        if (correct) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }

    }

    @Override
    public void onBackPressed() {
        if (quizALive) {
            showQuitWarning(() -> super.onBackPressed());
        } else {
            super.onBackPressed();
        }
    }

    private void showQuitWarning(Runnable positiveAction) {
        new AlertDialog.Builder(this)
                .setTitle("Na pewno chcesz wyjsc?")
                .setMessage("Nie dokonczyles quizu")
                .setPositiveButton("Wyjdz", (dialog1, which) -> positiveAction.run())
                .setNegativeButton("Kontynuuj", null)
                .create().show();
    }
}