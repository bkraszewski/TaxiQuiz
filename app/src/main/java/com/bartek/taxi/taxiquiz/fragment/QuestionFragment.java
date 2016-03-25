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
import com.bartek.taxi.taxiquiz.entity.Question;
import com.bartek.taxi.taxiquiz.entity.Score;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuestionFragment extends Fragment {

    private static final String QUESTION = "question";
    public static final String TAG = "questionTag";

    @InjectView(R.id.tvStreet)
    protected TextView tvStreet;

    @InjectView(R.id.lvDistricts)
    protected ListView lvDistricts;

    @InjectView(R.id.lvStreets)
    protected ListView lvStreets;

    @InjectView(R.id.btnCheck)
    protected Button btnCheck;

    @InjectView(R.id.btnMap)
    protected Button btnMap;

    private int selectedDistrict = -1;
    private int selectedStreet = -1;

    private OnQuestionSubmitedListener listener;
    private SelectionAdapter[] adapters;
    private Question question;

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
        question = (Question) args.getSerializable(QUESTION);

        if (question != null) {
            initQuestions(question);
        }

        checkButtonEnabled();
        btnCheck.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionSubmited(question, selectedDistrict, selectedStreet);
            }
        });
    }

    public void checkButtonEnabled() {
        btnCheck.setEnabled(selectedDistrict > -1 && selectedStreet > -1);
    }

    private void initQuestions(final Question question) {
        tvStreet.setText(question.street);
        adapters = new SelectionAdapter[2];
        adapters[0] = new SelectionAdapter(question.districts) {

            @Override
            int getSelection() {
                return selectedDistrict;
            }

            @Override
            void setSelection(int selection) {
                selectedDistrict = selection;
                checkButtonEnabled();
            }
        };

        lvDistricts.setAdapter(adapters[0]);
        adapters[1] = (new SelectionAdapter(question.connections) {
            @Override
            int getSelection() {
                return selectedStreet;
            }

            @Override
            void setSelection(int selection) {
                selectedStreet = selection;
                checkButtonEnabled();
            }
        });

        lvStreets.setAdapter(adapters[1]);

    }

    public static QuestionFragment newInstance(Question question) {

        Bundle args = new Bundle();
        args.putSerializable(QUESTION, question);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void showScore(Score score) {
        adapters[0].markAnswers(score.firstOkAnswer, score.firstBadAnswer);
        adapters[1].markAnswers(score.secondOkAnswer, score.secondBadAnswer);
    }

    public void enableMap() {
        btnMap.setVisibility(View.VISIBLE);
        btnMap.setOnClickListener(v -> {
            MapFragmentDialog dialog = MapFragmentDialog.newInstance(question.street);
            dialog.show(getActivity().getFragmentManager(), TAG);
        });

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
        void onQuestionSubmited(Question question, int firstAnswer, int secondAnswer);
    }

}
