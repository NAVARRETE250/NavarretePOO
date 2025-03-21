package com.example;

import org.json.simple.JSONObject;

public class Zone {
    private String type;
    private String color;
    private int x, y, width, height;

    public Zone(String type, String color, int x, int y, int width, int height) {
        this.type = type;
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Métodos getter y setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", this.type);
        json.put("color", this.color);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("width", this.width);
        json.put("height", this.height);
    
        return json;
    }
}