package com.bartek.taxi.taxiquiz.model;

import android.content.Context;
import android.text.TextUtils;

import com.bartek.taxi.taxiquiz.entity.InputPlacesLine;
import com.bartek.taxi.taxiquiz.entity.InputStreetLine;
import com.bartek.taxi.taxiquiz.functions.Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataReader {

    public static List<InputStreetLine> readAllStreetsData(Context context) throws IOException {
        return readFile(context, "streets.tsv", (line ->
                readStreetDataFromLine(line)
        ));
    }

    public static List<InputPlacesLine> readAllPlacesData(Context context) throws IOException {
        return readFile(context, "places.tsv", (line ->
                readPlacesDataFromLine(line)
        ));
    }

    private static <T> List<T> readFile(Context context, String file, Function<String, T> lineReader) {
        List<T> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(lineReader.apply(line));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return data;
    }

    private static InputPlacesLine readPlacesDataFromLine(String line) throws IOException {
        String[] fields = line.trim().split("\\t");
        if (fields.length != 3) {
            throw new IOException("Every line should have 3 fields!");
        }

        InputPlacesLine record = new InputPlacesLine();
        record.place = fields[0].trim();
        record.street = fields[1].trim();
        record.district = fields[2].trim();
        return record;
    }

    private static InputStreetLine readStreetDataFromLine(String line) throws IOException {
        String[] fields = line.trim().split("\\t");
        if (fields.length != 4) {
            throw new IOException("Every line should have 3 fields!");
        }

        InputStreetLine record = new InputStreetLine();
        record.id = Long.parseLong(fields[0]);
        record.street = fields[1].trim();
        record.district = fields[2].trim();

        List<String> streets = Arrays.asList(fields[3].trim().split(","));
        Collections.sort(streets);
        record.connectedStreets = TextUtils.join(", ", streets);

        return record;
    }
}
