package com.bartek.taxi.taxiquiz.model;

import android.content.Context;

import com.bartek.taxi.taxiquiz.App;
import com.bartek.taxi.taxiquiz.entity.InputPlacesLine;
import com.bartek.taxi.taxiquiz.entity.InputStreetLine;
import com.bartek.taxi.taxiquiz.entity.QuestionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionFactory {

    private static QuestionFactory instance;
    private final DataComposer composer;

    public static QuestionFactory getInstance() {
        if (instance == null) {
            instance = new QuestionFactory(App.getContext());
        }
        return instance;
    }

    private QuestionFactory(Context context) {
        composer = new DataComposer(context);
        composer.init();
    }


    public List<QuestionData> getQuestions(Type type, boolean shuffle) {
        return getQuestions(type, shuffle, -1);
    }

    private List<QuestionData> fillStreetData(List<InputStreetLine> lines) {
        List<QuestionData> questions = new ArrayList<>();
        for (InputStreetLine line : lines) {
            QuestionData data = new QuestionData(line);
            data.questions.get(0).answers = composer.createQuestionDistricts(line.district);
            data.questions.get(1).answers = composer.createQuestionConnections(line);
            questions.add(data);
        }

        return questions;
    }

    private List<QuestionData> fillPlacesData(List<InputPlacesLine> inputPlacesLines) {
        List<QuestionData> questions = new ArrayList<>();
        for (InputPlacesLine line : inputPlacesLines) {
            QuestionData data = new QuestionData(line);
            data.questions.get(0).answers = composer.createQuestionStreets(line.street);
            questions.add(data);
        }

        return questions;
    }

    public List<QuestionData> getQuestions(Type type, boolean isRandom, int size) {


        switch (type) {
            case STREETS:
                if (size > composer.getStreetData().size()) {
                    throw new IllegalArgumentException("Not enough questions!");
                }

                List<InputStreetLine> inputStreetLines = new ArrayList<>(composer.getStreetData());
                if (isRandom) {
                    Collections.shuffle(inputStreetLines);
                }
                return fillStreetData(inputStreetLines.subList(0, size > 0 ? size : inputStreetLines.size()));

            case PLACES:
                if (size > composer.getPlacesData().size()) {
                    throw new IllegalArgumentException("Not enough questions!");
                }

                List<InputPlacesLine> lines = new ArrayList<>(composer.getPlacesData());
                if (isRandom) {
                    Collections.shuffle(lines);
                }
                return fillPlacesData(lines.subList(0, size > 0 ? size : lines.size()));
        }

        return null;
    }

    public enum Type {
        STREETS, PLACES
    }
}
