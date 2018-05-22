package com.example.bgmitkov.myapplication;

/**
 * Created by bgmitkov on 22.5.2018 г..
 */

public class Item {
    String name;
    String path;

    public Item(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
