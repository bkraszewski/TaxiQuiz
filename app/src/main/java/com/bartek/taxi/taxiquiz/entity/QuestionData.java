package com.bartek.taxi.taxiquiz.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionData implements Serializable {
    public String place;
    public List<Question> questions = new ArrayList<>();

    public long id;

    public QuestionData(InputStreetLine line) {
        id = line.id;
        place = line.street;

        Question first = new Question();
        first.question = "Podaj osiedle";
        first.correctAnswer = line.district;
        questions.add(first);

        Question second = new Question();
        second.question = "Podaj sasiednie ulice";
        second.correctAnswer = line.connectedStreets;
        questions.add(second);
    }

    public QuestionData(InputPlacesLine line) {
        id = line.id;
        place = line.place;

        Question first = new Question();
        first.question = "Podaj ulice";
        first.correctAnswer = line.street;
        questions.add(first);
    }

    public static class Question implements Serializable {
        public String question;
        public String correctAnswer;
        public List<String> answers;

        //user answers
        public int userAnswer = -1;
        public int okAnswer = -1;
        public int wrongAnswer = -1;
    }
}
