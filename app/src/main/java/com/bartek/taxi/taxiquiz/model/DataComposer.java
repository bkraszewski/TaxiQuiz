package com.bartek.taxi.taxiquiz.model;

import android.content.Context;
import android.text.TextUtils;

import com.bartek.taxi.taxiquiz.entity.InputPlacesLine;
import com.bartek.taxi.taxiquiz.entity.InputStreetLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DataComposer {

    private final Context context;
    private List<InputStreetLine> streetData;
    private List<InputPlacesLine> placesData;
    private Random random = new Random();

    private Set<String> allDistricts = new HashSet<>();
    private Map<String, Set<String>> streetsByDistricts = new HashMap<>();
    private Set<String> allStreets = new HashSet<>();

    public DataComposer(Context context) {
        this.context = context;
    }

    public void init() {
        readData();
        composeData();
    }


    public List<InputStreetLine> getStreetData() {
        return streetData;
    }

    public List<InputPlacesLine> getPlacesData() {
        return placesData;
    }

    private void readData() {

        try {
            this.streetData = DataReader.readAllStreetsData(context);
            this.placesData = DataReader.readAllPlacesData(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void composeData() {
        for (InputStreetLine line : streetData) {
            allDistricts.add(line.district);
            if (!streetsByDistricts.containsKey(line.district)) {
                streetsByDistricts.put(line.district, new HashSet<>());
            }

            streetsByDistricts.get(line.district).addAll(Arrays.asList(line.connectedStreets.split(",")));


        }

        for (InputPlacesLine line : placesData) {
            allDistricts.add(line.district);

            if (!streetsByDistricts.containsKey(line.district)) {
                streetsByDistricts.put(line.district, new HashSet<>());
            }

            streetsByDistricts.get(line.district).addAll(Arrays.asList(line.street));
        }

        for (Map.Entry<String, Set<String>> entry : streetsByDistricts.entrySet()) {
            allStreets.addAll(entry.getValue());
        }
    }

    public List<String> createQuestionDistricts(String district) {
        List<String> questions = new ArrayList<>();
        questions.add(district);

        List<String> districtList = new ArrayList<>(allDistricts);
        districtList.remove(district);

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

    public List<String> createQuestionConnections(InputStreetLine line) {
        List<String> questions = new ArrayList<>();
        questions.add(line.connectedStreets);

        while (questions.size() < 4) {
            String answers = generateStreets(line.district);
            if (!questions.contains(answers)) {

                questions.add(answers);
            }
        }

        Collections.shuffle(questions);
        return questions;
    }

    private String generateStreets(String district) {
        List<String> streets = new ArrayList<>(streetsByDistricts.get(district));
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

        Collections.sort(answers);
        return TextUtils.join(", ", answers);
    }

    private void addIfNotExists(List<String> answers, String s) {
        if (!answers.contains(s)) {
            answers.add(s);
        }
    }

    public List<String> createQuestionStreets(String excludedStreet) {
        List<String> streets = new ArrayList<>(allStreets);
        allStreets.remove(excludedStreet);
        Collections.shuffle(streets);
        streets.add(random.nextInt(3), excludedStreet);
        return streets.subList(0, 4);

    }
}
