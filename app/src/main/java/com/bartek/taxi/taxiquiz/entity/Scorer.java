package com.bartek.taxi.taxiquiz.entity;

public class Scorer {
    public static Score calculateScore(Question question, int firstAnswer, int secondAnswer) {

        Score score = new Score();
        score.firstOkAnswer = question.districts.indexOf(question.correctDistrict);
        score.firstBadAnswer = score.firstOkAnswer == firstAnswer ? -1 : firstAnswer;


        score.secondOkAnswer = question.connections.indexOf(question.correctConnections);
        score.secondBadAnswer = score.secondOkAnswer == secondAnswer ? -1 : secondAnswer;
        return score;

    }
}
