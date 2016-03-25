package com.bartek.taxi.taxiquiz;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bartek.taxi.taxiquiz.entity.DAO;
import com.bartek.taxi.taxiquiz.entity.InputLine;
import com.bartek.taxi.taxiquiz.entity.MockDao;
import com.bartek.taxi.taxiquiz.entity.Question;
import com.bartek.taxi.taxiquiz.entity.QuestionFactory;
import com.bartek.taxi.taxiquiz.entity.Score;
import com.bartek.taxi.taxiquiz.entity.Scorer;
import com.bartek.taxi.taxiquiz.entity.TsvDao;
import com.bartek.taxi.taxiquiz.fragment.QuestionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuizActivity extends Activity {


    private static final int ERRORS_LIMIT = 99;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.inject(this);

        DAO dao = new TsvDao(this);
        questions = dao.getAllQuestions();

        btnNext.setOnClickListener(v -> onNextQuestion());
        onNextQuestion();
    }

    private void onNextQuestion() {
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

    }

    private void onFinish() {

    }

    private void updateProgress() {
        tvProgress.setText(String.format("Pytanie: %d/%d", currentQuestion + 1, questions.size()));
    }

    private void updateScore() {
        tvScore.setText(String.format("Poprawne: %d Błędne: %d", correctAnswers, wrongAnswers));
    }

    private void createQuestionForLine(int index) {
        Question question = QuestionFactory.createQuestion(questions.get(index));
        QuestionFragment fragment = QuestionFragment.newInstance(question);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        fragment.setListener((question1, firstAnswer, secondAnswer) -> {
            Score score = Scorer.calculateScore(question, firstAnswer, secondAnswer);
            fragment.showScore(score);
            fragment.enableMap();
            btnNext.setEnabled(true);
            recalculateScore(score);
            updateScore();
        });
    }

    private void recalculateScore(Score score) {
        if (score.firstBadAnswer == -1 && score.secondBadAnswer == -1) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }

        if (wrongAnswers >= ERRORS_LIMIT) {
            onGameOver();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag(QuestionFragment.TAG);
        if (fragment != null) {
            try {
                ((DialogFragment) fragment).dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }
}