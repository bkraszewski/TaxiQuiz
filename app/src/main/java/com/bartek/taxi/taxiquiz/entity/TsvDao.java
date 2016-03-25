package com.bartek.taxi.taxiquiz.entity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.renderscript.ScriptGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TsvDao implements DAO {

    private final Context context;

    public TsvDao(Context context) {
        this.context = context;
    }

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
        List<InputLine> data = new ArrayList<>(130);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("data.tsv")));
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(readDataFromLine(line));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }

    private InputLine readDataFromLine(String line) throws IOException {
        String[] fields = line.trim().split("\\t");
        if (fields.length != 4) {
            throw new IOException("Every line should have 3 fields!");
        }

        InputLine record = new InputLine();
        record.id = Long.parseLong(fields[0]);
        record.street = fields[1];
        record.district = fields[2];
        record.connectedStreets = readStreets(fields[3]);

        return record;
    }

    private List<String> readStreets(String field) {
        String[] data = field.trim().split(",");
        List<String> cleanData = new ArrayList<>();
        for (String street : data) {
            cleanData.add(street.trim());
        }

        return cleanData;
    }
}