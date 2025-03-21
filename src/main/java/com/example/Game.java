package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.List;

public class Game {
    private String name;
    private List<Level> levels;

    // Constructor
    public Game(String name, List<Level> levels) {
        this.name = name;
        this.levels = levels;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
    
        JSONArray levelsArray = new JSONArray();
        for (Level level : this.levels) {
            levelsArray.add(level.toJson());
        }
        json.put("levels", levelsArray);
    
        return json;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Level> getLevels() {
        return levels;
    }
}