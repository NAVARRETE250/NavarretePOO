package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.List;

public class Level {
    private String name;
    private String description;
    private List<Layer> layers;
    private List<Zone> zones;
    private List<Sprite> sprites;

    public Level(String name, String description, List<Layer> layers, List<Zone> zones, List<Sprite> sprites) {
        this.name = name;
        this.description = description;
        this.layers = layers;
        this.zones = zones;
        this.sprites = sprites;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("description", this.description);
    
        JSONArray layersArray = new JSONArray();
        for (Layer layer : this.layers) {
            layersArray.add(layer.toJson());
        }
        json.put("layers", layersArray);
    
        JSONArray zonesArray = new JSONArray();
        for (Zone zone : this.zones) {
            zonesArray.add(zone.toJson());
        }
        json.put("zones", zonesArray);
    
        JSONArray spritesArray = new JSONArray();
        for (Sprite sprite : this.sprites) {
            spritesArray.add(sprite.toJson());
        }
        json.put("sprites", spritesArray);
    
        return json;
    }


    public String getDescription() {
        return description;
    }
    
    public List<Sprite> getSprites() {
        return sprites;
    }
    

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public void removeLayer(Layer layer) {
        layers.remove(layer);
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
    }

    public String getName() {
        return name;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public List<Zone> getZones() {
        return zones;
    }
}