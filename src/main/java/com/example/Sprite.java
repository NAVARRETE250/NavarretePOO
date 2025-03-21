package com.example;

import org.json.JSONObject;

public class Sprite {
    private String type;
    private String imageFile;
    private int x;
    private int y;
    private int width;
    private int height;

    // Constructor
    public Sprite(String type, String imageFile, int x, int y, int width, int height) {
        this.type = type;
        this.imageFile = imageFile;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", this.type);
        json.put("imageFile", this.imageFile);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("width", this.width);
        json.put("height", this.height);
    
        return json;
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getImageFile() {
        return imageFile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Setters (opcional, si necesitas modificar los valores)
    public void setType(String type) {
        this.type = type;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}