package com.bartek.taxi.taxiquiz.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MockDao implements DAO {
    @Override
    public List<String> createQuestionDistricts(InputLine line) {
        List<String> questions = new ArrayList<>();
        questions.add(line.district);

        //add random districts
        questions.add("Dolidy gorne");
        questions.add("Srodmiescie");
        questions.add("Wysoki Stoczek");
        Collections.shuffle(questions);
        return questions;
    }

    @Override
    public List<List<String>> createQuestionConnections(InputLine line) {
        List<List<String>> questions = new ArrayList<>();
        questions.add(line.connectedStreets);
        questions.add(Arrays.asList("Jurowiecka", "Fabryczna", "Poleska"));
        questions.add(Arrays.asList("Poleska", "Jurowiecka"));
        questions.add(Arrays.asList("Starobojarska", "Brak lacznika"));
        Collections.shuffle(questions);
        return questions;
    }

    @Override
    public List<InputLine> getAllQuestions() {

        InputLine line1 = new InputLine();
        line1.street = "Złota";
        line1.district = "Centrum";
        line1.connectedStreets = Arrays.asList("Sienkiewicza", "Sobieskiego");

        InputLine line2 = new InputLine();
        line2.street = "Przejazd";
        line2.district = "Srodmiescie";
        line2.connectedStreets = Arrays.asList("Lipowa", "Aleja");
        return Arrays.asList(line1, line2);
    }
}
