package com.example.myimdb.model;

import io.realm.RealmObject;

public class MovieGenre extends RealmObject {

    private int id;
    private String name;


    public MovieGenre(){}

    public MovieGenre (int id, String name) {
        this.id = id;
        this.name = name;
    }



    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
