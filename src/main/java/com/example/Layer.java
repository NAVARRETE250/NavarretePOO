package com.example;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.List;

public class Layer {
    private String name;
    private String tilesSheetFile;
    private int x, y, depth, tilesWidth, tilesHeight;
    private List<List<Integer>> tileMap;

    public Layer(String name, String tilesSheetFile, int x, int y, int depth, int tilesWidth, int tilesHeight, List<List<Integer>> tileMap) {
        this.name = name;
        this.tilesSheetFile = tilesSheetFile;
        this.x = x;
        this.y = y;
        this.depth = depth;
        this.tilesWidth = tilesWidth;
        this.tilesHeight = tilesHeight;
        this.tileMap = tileMap;
    }



    // Métodos getter y setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTilesSheetFile() {
        return tilesSheetFile;
    }

    public void setTilesSheetFile(String tilesSheetFile) {
        this.tilesSheetFile = tilesSheetFile;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getTilesWidth() {
        return tilesWidth;
    }

    public void setTilesWidth(int tilesWidth) {
        this.tilesWidth = tilesWidth;
    }

    public int getTilesHeight() {
        return tilesHeight;
    }

    public void setTilesHeight(int tilesHeight) {
        this.tilesHeight = tilesHeight;
    }

    public List<List<Integer>> getTileMap() {
        return tileMap;
    }

    public void setTileMap(List<List<Integer>> tileMap) {
        this.tileMap = tileMap;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("tilesSheetFile", this.tilesSheetFile);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("depth", this.depth);
        json.put("tilesWidth", this.tilesWidth);
        json.put("tilesHeight", this.tilesHeight);
        
        // TileMap
        JSONArray tileMapArray = new JSONArray();
        for (List<Integer> row : this.tileMap) {
            tileMapArray.put(new JSONArray(row));
        }
        json.put("tileMap", tileMapArray);
        
        return json;
    }
}