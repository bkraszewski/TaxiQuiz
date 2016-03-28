package com.bartek.taxi.taxiquiz.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bartek.taxi.taxiquiz.R;
import com.bartek.taxi.taxiquiz.entity.QuestionData;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuestionFragment extends Fragment {

    private static final String QUESTION = "question";
    public static final String TAG = "questionTag";

    @InjectView(R.id.tvStreet)
    protected TextView tvStreet;

    @InjectView(R.id.btnCheck)
    protected Button btnCheck;

    @InjectView(R.id.btnMap)
    protected Button btnMap;

    @InjectView(R.id.questionContainer)
    protected ViewGroup questionContainer;

    private OnQuestionSubmitedListener listener;
    private SelectionAdapter[] adapters;
    private QuestionData question;

    public void setListener(OnQuestionSubmitedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        question = (QuestionData) args.getSerializable(QUESTION);

        if (question != null) {
            initQuestions(question);
        }

        checkButtonEnabled();
        btnCheck.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionSubmited(question);
            }
        });
    }

    public void checkButtonEnabled() {
        boolean enabled = true;

        for (QuestionData.Question questionData : question.questions) {
            enabled = enabled && questionData.userAnswer > -1;
        }

        btnCheck.setEnabled(enabled);
    }

    private void initQuestions(final QuestionData question) {
        tvStreet.setText(question.place);
        adapters = new SelectionAdapter[question.questions.size()];


        int index = 0;
        for (QuestionData.Question questionData : question.questions) {
            View questionView = getActivity().getLayoutInflater().inflate( R.layout.view_question, questionContainer, false);
            TextView text = (TextView) questionView.findViewById(R.id.question);
            ListView lvAnswers = (ListView) questionView.findViewById(R.id.lvAnswer);
            adapters[index] = new SelectionAdapter(questionData.answers) {

                @Override
                int getSelection() {
                    return questionData.userAnswer;
                }

                @Override
                void setSelection(int selection) {
                    questionData.userAnswer = selection;
                    checkButtonEnabled();
                }
            };


            text.setText(questionData.question);
            lvAnswers.setAdapter(adapters[index]);
            questionContainer.addView(questionView);
            index++;
        }

    }

    public static QuestionFragment newInstance(QuestionData question) {

        Bundle args = new Bundle();
        args.putSerializable(QUESTION, question);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void showScore(QuestionData score) {

        for (int i = 0; i < score.questions.size(); i++) {
            QuestionData.Question data = score.questions.get(i);
            adapters[i].markAnswers(data.okAnswer, data.wrongAnswer);
        }
    }

    public void enableMap() {
        btnMap.setVisibility(View.VISIBLE);
        btnMap.setOnClickListener(v -> listener.showMap(question));
        btnCheck.setEnabled(false);
    }

    static class AnswerHolder {

        @InjectView(R.id.rbSelection)
        protected AppCompatRadioButton rbSeletion;

        @InjectView(R.id.tvAnswer)
        protected TextView tvAnswer;

        @InjectView(R.id.clickZone)
        protected View clickZone;

        public AnswerHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    abstract class SelectionAdapter extends BaseAdapter {
        private final List<?> answers;
        private int correctIndex = -1;
        private int wrongIndex = -1;

        public SelectionAdapter(List<?> answers) {
            this.answers = answers;
        }

        public void markAnswers(int correctIndex, int wrongIndex) {
            this.correctIndex = correctIndex;
            this.wrongIndex = wrongIndex;
            notifyDataSetChanged();
        }

        abstract int getSelection();

        abstract void setSelection(int selection);

        @Override
        public int getCount() {
            return answers.size();
        }

        @Override
        public Object getItem(int position) {
            return answers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.itm_answer, parent, false);
                AnswerHolder holder = new AnswerHolder(convertView);
                convertView.setTag(holder);
            }

            AnswerHolder holder = (AnswerHolder) convertView.getTag();
            holder.tvAnswer.setText(getItem(position).toString());

            holder.rbSeletion.setChecked(getSelection() == position);
            convertView.setOnClickListener(v -> {
                setSelection(getSelection() == position ? -1 : position);
                notifyDataSetChanged();
            });

            if (correctIndex != -1) {
                convertView.setClickable(false);
            }

            if (correctIndex == position) {
                holder.clickZone.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
            } else if (wrongIndex == position) {
                holder.clickZone.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
            } else {
                holder.clickZone.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            }
            return convertView;
        }
    }

    public interface OnQuestionSubmitedListener {
        void onQuestionSubmited(QuestionData question);

        void showMap(QuestionData question);
    }

}