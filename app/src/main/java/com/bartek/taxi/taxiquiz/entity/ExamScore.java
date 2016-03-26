package com.bartek.taxi.taxiquiz.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "exam_score")
public class ExamScore {

    @DatabaseField
    public int questions;

    @DatabaseField
    public int correct;

    @DatabaseField
    public long startDate;

    @DatabaseField
    public long endDate;

    public ExamScore() {
    }

    public ExamScore(int questions, int correct, long startDate, long endDate) {
        this.questions = questions;
        this.correct = correct;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
