package com.bartek.taxi.taxiquiz.entity;

import android.content.Context;
import android.renderscript.ScriptGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TsvDao implements DAO {

    private static TsvDao instance;

    private final Context context;
    private Random random = new Random();

    private Set<String> districts = new HashSet<>();
    private Map<String, Set<String>> streetsByDistrict = new HashMap<>();
    private Set<String> allStreets = new HashSet<>();

    private TsvDao(Context context) {
        this.context = context;
    }

    public static DAO getInstance(Context contect) {
        if (instance == null) {
            instance = new TsvDao(contect);
        }

        return instance;
    }

    @Override
    public List<String> createQuestionDistricts(InputLine line) {
        List<String> questions = new ArrayList<>();
        questions.add(line.district);

        List<String> districtList = new ArrayList<>(districts);

        while (questions.size() < 4) {
            int index = random.nextInt(districtList.size());
            if (!questions.contains(districtList.get(index))) {
                questions.add(districtList.get(index));
            }

            districtList.remove(index);
        }

        Collections.shuffle(questions);
        return questions;
    }

    @Override
    public List<List<String>> createQuestionConnections(InputLine line) {
        List<List<String>> questions = new ArrayList<>();
        questions.add(line.connectedStreets);
//        questions.add(Arrays.asList("Jurowiecka", "Fabryczna", "Poleska"));
//        questions.add(Arrays.asList("Poleska", "Jurowiecka"));
//        questions.add(Arrays.asList("Starobojarska", "Brak lacznika"));

        while (questions.size() < 4) {
            List<String> answers = generateStreets(line.district);
            if (!questions.contains(answers)) {

                questions.add(answers);
            }
        }


//        for(List<String> list: questions){
//            list.SH
//        }
        Collections.shuffle(questions);
        return questions;
    }

    private List<String> generateStreets(String district) {
        List<String> streets = new ArrayList<>(streetsByDistrict.get(district));
        List<String> answers = new ArrayList<>();
        int size = random.nextInt(2) == 1 ? 3 : 2;
        while (answers.size() < size) {
            if (streets.isEmpty()) {
                List<String> all = new ArrayList<>(allStreets);
                addIfNotExists(answers, all.get(random.nextInt(all.size())));
            } else {
                int index = random.nextInt(streets.size());
                addIfNotExists(answers, streets.get(index));
                streets.remove(index);
            }
        }

        return answers;
    }

    private void addIfNotExists(List<String> answers, String s) {
        if (!answers.contains(s)) {
            answers.add(s);
        }
    }

    @Override
    public List<InputLine> getAllQuestions(boolean isRandom) {
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


        if (isRandom) {
            Collections.shuffle(data);
        }
        return data;
    }

    @Override
    public List<InputLine> getQuestions(int size, boolean isRandom) {
        List<InputLine> allData = getAllQuestions(isRandom);
        return allData.subList(0, size);
    }

    private void filQuestionsData(InputLine data) {
        districts.add(data.district);
        if (streetsByDistrict.containsKey(data.district)) {
            streetsByDistrict.get(data.district).addAll(data.connectedStreets);
        } else {
            streetsByDistrict.put(data.district, new HashSet<>(data.connectedStreets));
        }

        allStreets.addAll(data.connectedStreets);
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

        filQuestionsData(record);
        return record;
    }

    private List<String> readStreets(String field) {
        String[] data = field.trim().split(",");
        List<String> cleanData = new ArrayList<>();
        for (String street : data) {
            cleanData.add(street.trim());
        }

        Collections.shuffle(cleanData);
        return cleanData;
    }
}