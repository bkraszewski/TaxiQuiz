package com.bartek.taxi.taxiquiz.entity;

import android.content.Context;

public class QuestionFactory {


    public static Question createQuestion(InputLine line, Context context) {
        DAO dao = DAO.Factory.create(context);
        Question question = new Question(line);
        question.districts = dao.createQuestionDistricts(line);
        question.connections = dao.createQuestionConnections(line);
        return question;
    }
}
