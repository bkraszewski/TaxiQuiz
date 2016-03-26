package com.bartek.taxi.taxiquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bartek.taxi.taxiquiz.entity.DAO;
import com.bartek.taxi.taxiquiz.entity.InputLine;
import com.bartek.taxi.taxiquiz.entity.Question;
import com.bartek.taxi.taxiquiz.entity.QuestionFactory;
import com.bartek.taxi.taxiquiz.entity.Score;
import com.bartek.taxi.taxiquiz.entity.Scorer;
import com.bartek.taxi.taxiquiz.fragment.QuestionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuizActivity extends Activity {

    private static final String SIZE = "size";
    private static final String RANDOM = "random";
    private static final String ERRORS_LIMIT = "errorLimit";

    private boolean quizALive = true;

    public static void start(Context context, int size, int errorLimit, boolean random) {
        Intent starter = new Intent(context, QuizActivity.class);
        starter.putExtra(SIZE, size);
        starter.putExtra(RANDOM, random);
        starter.putExtra(ERRORS_LIMIT, errorLimit);
        context.startActivity(starter);
    }

    public static void start(Context context) {
        start(context, -1, Integer.MAX_VALUE, false);
    }

    private List<InputLine> questions = new ArrayList<>();

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

        DAO dao = DAO.Factory.create(this);

        boolean isRandom = getIntent().getBooleanExtra(RANDOM, false);
        errorsLimit = getIntent().getIntExtra(ERRORS_LIMIT, 0);
        int size = getIntent().getIntExtra(SIZE, -1);

        if (size == -1) {
            questions = dao.getAllQuestions(isRandom);
        } else {
            questions = dao.getQuestions(size, isRandom);
        }

        btnNext.setOnClickListener(v -> onNextQuestion());
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

    private void updateProgress() {
        tvProgress.setText(String.format("Pytanie: %d/%d", currentQuestion + 1, questions.size()));
    }

    private void updateScore() {
        tvScore.setText(String.format("Poprawne: %d Błędne: %d", correctAnswers, wrongAnswers));
    }

    private void createQuestionForLine(int index) {
        Question question = QuestionFactory.createQuestion(questions.get(index), this);
        QuestionFragment fragment = QuestionFragment.newInstance(question);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.questionContainer, fragment)
                .commit();

        fragment.setListener(new QuestionFragment.OnQuestionSubmitedListener() {

            @Override
            public void onQuestionSubmited(Question question, int firstAnswer, int secondAnswer) {
                Score score = Scorer.calculateScore(question, firstAnswer, secondAnswer);
                fragment.showScore(score);
                fragment.enableMap();
                btnNext.setEnabled(true);
                recalculateScore(score);
                updateScore();
            }

            @Override
            public void showMap(Question question) {
                MapActivity.start(QuizActivity.this, question.street);
            }
        });
    }

    private void recalculateScore(Score score) {
        if (score.firstBadAnswer == -1 && score.secondBadAnswer == -1) {
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