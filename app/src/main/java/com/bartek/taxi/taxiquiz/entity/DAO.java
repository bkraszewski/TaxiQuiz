package com.bartek.taxi.taxiquiz.entity;

import java.util.List;

public interface DAO {
    List<String> createQuestionDistricts(InputLine line);

    List<List<String>> createQuestionConnections(InputLine line);

    List<InputLine> getAllQuestions();
}
