package com.bartek.taxi.taxiquiz.model;

import com.bartek.taxi.taxiquiz.entity.QuestionData;

public class Scorer {
    public static QuestionData calculateScore(QuestionData data) {
        for (QuestionData.Question question : data.questions) {
            question.okAnswer = question.answers.indexOf(question.correctAnswer);
            question.wrongAnswer = question.okAnswer == question.userAnswer ? -1 : question.userAnswer;
        }

        return data;

    }
}
