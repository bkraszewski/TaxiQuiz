package com.bartek.taxi.taxiquiz.entity;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    public String street;
    public String correctDistrict;
    public List<String> correctConnections;

    public List<String>districts;
    public List<List<String>>connections;
    public Object id;

    public Question(InputLine line) {
        id = line.id;
        street = line.street;
        correctDistrict = line.district;
        correctConnections = line.connectedStreets;
    }
}
