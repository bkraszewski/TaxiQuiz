package com.bartek.taxi.taxiquiz.entity;

public class QuestionFactory {

    private static DAO dao = new MockDao();

    public static Question createQuestion(InputLine line) {
        Question question = new Question(line);
        question.districts = dao.createQuestionDistricts(line);
        question.connections = dao.createQuestionConnections(line);
        return question;
    }
}
