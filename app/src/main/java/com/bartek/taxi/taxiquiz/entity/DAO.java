package com.bartek.taxi.taxiquiz.entity;

import android.content.Context;

import java.util.List;

public interface DAO {
    List<String> createQuestionDistricts(InputLine line);

    List<List<String>> createQuestionConnections(InputLine line);

    List<InputLine> getAllQuestions(boolean isRandom);

    List<InputLine> getQuestions(int size, boolean isRandom);


    class Factory {
        public static DAO create(Context context) {
            return TsvDao.getInstance(context);
        }
    }
}
