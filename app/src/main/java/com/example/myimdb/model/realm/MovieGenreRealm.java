package com.example.myimdb.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MovieGenreRealm extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;


    public MovieGenreRealm(){}

    public MovieGenreRealm (int id, String name) {
        this.id = id;
        this.name = name;
    }



    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
