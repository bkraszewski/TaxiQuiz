package com.bartek.taxi.taxiquiz.entity;

import java.util.ArrayList;
import java.util.List;

public class InputLine {
    public long id;
    public String street;
    public String district;
    public List<String> connectedStreets = new ArrayList<>();
}
