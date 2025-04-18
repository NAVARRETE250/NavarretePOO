package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private static final String RESOURCES_PATH = "NavarretePOO/src/main/resources/";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String resourcesPath = RESOURCES_PATH;

        // Paso 1: Listar archivos JSON en la carpeta
        List<String> jsonFiles = listJsonFilesInFolder(resourcesPath);
        if (jsonFiles.isEmpty()) {
            System.out.println("No se encontraron archivos JSON en la carpeta: " + resourcesPath);
            return;
        }

        // Paso 2: Seleccionar un archivo JSON
        String selectedFile = selectJsonFile(jsonFiles);
        if (selectedFile == null) {
            System.out.println("No se seleccionó ningún archivo. Saliendo del programa...");
            return;
        }

        // Paso 3: Cargar el juego desde el archivo JSON seleccionado
        Game game = loadGameFromJson(resourcesPath, selectedFile);
        if (game == null) {
            System.out.println("No se pudo cargar el juego. Saliendo del programa...");
            return;
        }

        // Paso 4: Mostrar el menú principal
        mainMenu(game, resourcesPath);
    }

    private static List<String> listJsonFilesInFolder(String folderPath) {
        List<String> jsonFiles = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    jsonFiles.add(file.getName());
                }
            }
        } else {
            System.out.println("La carpeta no existe o no es un directorio: " + folderPath);
        }
        return jsonFiles;
    }

    private static Game loadGameFromJson(String folderPath, String filename) {
        JSONParser parser = new JSONParser();
        File file = new File(folderPath + filename);
        if (!file.exists()) {
            System.out.println("El archivo no existe: " + file.getAbsolutePath());
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            return parseGame(jsonObject);
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo JSON: " + e.getMessage());
            return null;
        }
    }

    private static Game parseGame(JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        JSONArray levelsArray = (JSONArray) jsonObject.get("levels");
        List<Level> levels = new ArrayList<>();
        for (Object levelObj : levelsArray) {
            levels.add(parseLevel((JSONObject) levelObj));
        }
        return new Game(name, levels);
    }

    private static Level parseLevel(JSONObject levelJson) {
        String levelName = (String) levelJson.get("name");
        String description = (String) levelJson.get("description");
        List<Layer> layers = parseLayers((JSONArray) levelJson.get("layers"));
        List<Zone> zones = parseZones((JSONArray) levelJson.get("zones"));
        List<Sprite> sprites = parseSprites((JSONArray) levelJson.get("sprites"));
        return new Level(levelName, description, layers, zones, sprites);
    }

    private static List<Layer> parseLayers(JSONArray layersArray) {
        List<Layer> layers = new ArrayList<>();
        for (Object layerObj : layersArray) {
            layers.add(parseLayer((JSONObject) layerObj));
        }
        return layers;
    }

    private static Layer parseLayer(JSONObject layerJson) {
        String layerName = (String) layerJson.get("name");
        String tilesSheetFile = (String) layerJson.get("tilesSheetFile");
        int x = ((Long) layerJson.get("x")).intValue();
        int y = ((Long) layerJson.get("y")).intValue();
        int depth = ((Long) layerJson.get("depth")).intValue();
        int tilesWidth = ((Long) layerJson.get("tilesWidth")).intValue();
        int tilesHeight = ((Long) layerJson.get("tilesHeight")).intValue();
        JSONArray tileMapArray = (JSONArray) layerJson.get("tileMap");
        List<List<Integer>> tileMap = new ArrayList<>();
        for (Object rowObj : tileMapArray) {
            JSONArray rowArray = (JSONArray) rowObj;
            List<Integer> row = new ArrayList<>();
            for (Object cellObj : rowArray) {
                row.add(((Long) cellObj).intValue());
            }
            tileMap.add(row);
        }
        return new Layer(layerName, tilesSheetFile, x, y, depth, tilesWidth, tilesHeight, tileMap);
    }

    private static List<Zone> parseZones(JSONArray zonesArray) {
        List<Zone> zones = new ArrayList<>();
        for (Object zoneObj : zonesArray) {
            zones.add(parseZone((JSONObject) zoneObj));
        }
        return zones;
    }

    private static Zone parseZone(JSONObject zoneJson) {
        String type = (String) zoneJson.get("type");
        String color = (String) zoneJson.get("color");
        int x = ((Long) zoneJson.get("x")).intValue();
        int y = ((Long) zoneJson.get("y")).intValue();
        int width = ((Long) zoneJson.get("width")).intValue();
        int height = ((Long) zoneJson.get("height")).intValue();
        return new Zone(type, color, x, y, width, height);
    }

    private static List<Sprite> parseSprites(JSONArray spritesArray) {
        List<Sprite> sprites = new ArrayList<>();
        for (Object spriteObj : spritesArray) {
            sprites.add(parseSprite((JSONObject) spriteObj));
        }
        return sprites;
    }

    private static Sprite parseSprite(JSONObject spriteJson) {
        String type = (String) spriteJson.get("type");
        String imageFile = (String) spriteJson.get("imageFile");
        int x = ((Long) spriteJson.get("x")).intValue();
        int y = ((Long) spriteJson.get("y")).intValue();
        int width = ((Long) spriteJson.get("width")).intValue();
        int height = ((Long) spriteJson.get("height")).intValue();
        return new Sprite(type, imageFile, x, y, width, height);
    }

        private static void saveGameToJson(Game game, String folderPath, String filename) {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created) {
                    System.out.println("No se pudo crear la carpeta: " + folderPath);
                    return;
                }
            }
    
            if (!filename.endsWith(".json")) {
                filename += ".json";
            }
    
            String filePath = folderPath + File.separator + filename;
    
            try (FileWriter file = new FileWriter(filePath)) {
                JSONObject gameJson = game.toJson();
                String jsonString = gameJson.toJSONString();
                String prettyJson = formatJsonSimple(jsonString);
                file.write(prettyJson);
                System.out.println("Juego guardado en: " + filePath);
            } catch (Exception e) {
                System.out.println("Error al guardar el archivo JSON: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        private static String formatJsonSimple(String jsonString) {
            StringBuilder prettyJson = new StringBuilder();
            int indentLevel = 0;
            boolean inQuote = false;
            boolean inTileMap = false;
            int arrayDepth = 0;
    
            for (int i = 0; i < jsonString.length(); i++) {
                char c = jsonString.charAt(i);
                
                if (c == '"' && (i == 0 || jsonString.charAt(i - 1) != '\\')) {
                    inQuote = !inQuote;
                }
    
                if (!inQuote) {
                    if (c == '[' && isTileMapKey(jsonString, i)) {
                        inTileMap = true;
                        arrayDepth = 1;
                        prettyJson.append("[\n").append(getIndent(indentLevel + 1));
                        continue;
                    }
    
                    if (inTileMap) {
                        if (c == '[') arrayDepth++;
                        if (c == ']') arrayDepth--;
    
                        prettyJson.append(c);
                        
                        if (c == ',' && arrayDepth == 1) {
                            prettyJson.append("\n").append(getIndent(indentLevel + 1));
                        } else if (c == ',' && arrayDepth > 1) {
                            prettyJson.append(" ");
                        }
    
                        if (arrayDepth == 0) inTileMap = false;
                        continue;
                    }
    
                    switch (c) {
                        case '{':
                        case '[':
                            prettyJson.append(c).append("\n").append(getIndent(++indentLevel));
                            break;
                        case '}':
                        case ']':
                            prettyJson.append("\n").append(getIndent(--indentLevel)).append(c);
                            break;
                        case ',':
                            prettyJson.append(c).append("\n").append(getIndent(indentLevel));
                            break;
                        case ':':
                            prettyJson.append(": ");
                            break;
                        default:
                            prettyJson.append(c);
                    }
                } else {
                    prettyJson.append(c);
                }
            }
            return prettyJson.toString();
        }
    
        private static boolean isTileMapKey(String jsonString, int pos) {
            if (pos < 10) return false; // Evitar IndexOutOfBounds
            String preceding = jsonString.substring(Math.max(0, pos - 10), pos);
            return preceding.matches(".*\"tileMap\"\\s*:\\s*$");
        }

    private static String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("    "); // 4 espacios por nivel
        }
        return indent.toString();
    }

    private static String selectJsonFile(List<String> jsonFiles) {
        if (jsonFiles.isEmpty()) {
            System.out.println("No se encontraron archivos JSON en la carpeta.");
            return null;
        }

        System.out.println("\n--- Selecciona un archivo JSON ---");
        for (int i = 0; i < jsonFiles.size(); i++) {
            System.out.println((i + 1) + ". " + jsonFiles.get(i));
        }
        System.out.println((jsonFiles.size() + 1) + ". Salir");

        String choice = scanner.nextLine();
        try {
            int choiceInt = Integer.parseInt(choice);
            if (choiceInt >= 1 && choiceInt <= jsonFiles.size()) {
                return jsonFiles.get(choiceInt - 1);
            } else if (choiceInt == jsonFiles.size() + 1) {
                return null; // Salir
            } else {
                System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, introduce un número válido.");
        }
        return null;
    }

    private static void mainMenu(Game game, String resourcesPath) {
        while (true) {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Gestionar Niveles");
            System.out.println("2. Guardar y Salir");
            System.out.println("3. Jugar en Terminal");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                manageLevels(game);
            } else if (choice.equals("2")) {
                System.out.println("Introduce el nombre del archivo para guardar (por ejemplo, 'modified_game.json'):");
                String fileName = scanner.nextLine();
                saveGameToJson(game, resourcesPath, fileName);
                System.out.println("Juego guardado en: " + resourcesPath + fileName);
                break;
            } else if (choice.equals("3")) {
                playInTerminal(game);
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
            }
        }
    }

private static void clearScreen() {
    try {
        if (System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    } catch (Exception e) {
        // Fallback: imprime 50 líneas vacías
        for (int i = 0; i < 50; i++) System.out.println();
    }
}

private static void playInTerminal(Game game) {
    List<Level> levels = game.getLevels();
    while (true) {
        try {
            System.out.println("\n--- Selecciona un Nivel para Jugar ---");
            for (int i = 0; i < levels.size(); i++) {
                System.out.println((i + 1) + ". " + levels.get(i).getName());
            }
            System.out.println((levels.size() + 1) + ". Volver al Menú Principal");

            String choice = scanner.nextLine();
            int choiceInt = Integer.parseInt(choice);

            if (choiceInt >= 1 && choiceInt <= levels.size()) {
                Level selectedLevel = levels.get(choiceInt - 1);
                playLevelInTerminal(selectedLevel);
            } else if (choiceInt == levels.size() + 1) {
                break;
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
                TimeUnit.MILLISECONDS.sleep(1000); // Pausa mejorada
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, introduce un número válido.");
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}

private static void playLevelInTerminal(Level level) {
    if (level.getLayers().isEmpty()) {
        System.out.println("Este nivel no tiene capas. No se puede jugar.");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return;
    }

    Layer layer = level.getLayers().get(0);
    List<List<Integer>> tileMap = layer.getTileMap();
    int playerX = 1;
    int playerY = 1;

    try {
        clearScreen();
        System.out.println("Controles: W (Arriba), A (Izquierda), S (Abajo), D (Derecha), Q (Salir)");
        System.out.println("Presiona Enter para comenzar...");
        scanner.nextLine();

        while (true) {
            clearScreen();
            renderLevel(tileMap, playerX, playerY);
            System.out.print("Dirección (W/A/S/D/Q): ");

            String input = scanner.nextLine().toUpperCase();
            if (input.equals("Q")) break;

            int newX = playerX;
            int newY = playerY;

            switch (input) {
                case "W": newY--; break;
                case "A": newX--; break;
                case "S": newY++; break;
                case "D": newX++; break;
                default:
                    System.out.println("Tecla inválida. Usa W/A/S/D.");
                    TimeUnit.MILLISECONDS.sleep(1000);
                    continue;
            }

            if (newX >= 0 && newY >= 0 && newY < tileMap.size() && newX < tileMap.get(newY).size()) {
                int tile = tileMap.get(newY).get(newX);
                if (tile != 319 && tile != 320) {
                    playerX = newX;
                    playerY = newY;
                }
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

    private static void renderLevel(List<List<Integer>> tileMap, int playerX, int playerY) {
        for (int y = 0; y < tileMap.size(); y++) {
            for (int x = 0; x < tileMap.get(y).size(); x++) {
                if (x == playerX && y == playerY) {
                    System.out.print('@');
                } else {
                    char tileChar = getTileChar(tileMap.get(y).get(x));
                    System.out.print(tileChar);
                }
            }
            System.out.println();
        }
    }

    private static char getTileChar(int tile) {
        switch (tile) {
            case 21: return '.';
            case 319: return '#';
            case 320: return '#';
            case 340: return '~';
            case 341: return '~';
            default: return ' ';
        }
    }

    private static void manageLevels(Game game) {
        List<Level> levels = game.getLevels();
        while (true) {
            System.out.println("\n--- Gestionar Niveles ---");
            for (int i = 0; i < levels.size(); i++) {
                System.out.println((i + 1) + ". " + levels.get(i).getName());
            }
            System.out.println((levels.size() + 1) + ". Añadir Nivel");
            System.out.println((levels.size() + 2) + ". Volver al Menú Principal");

            String choice = scanner.nextLine();
            try {
                int choiceInt = Integer.parseInt(choice);

                if (choiceInt >= 1 && choiceInt <= levels.size()) {
                    manageLevel(levels.get(choiceInt - 1));
                } else if (choiceInt == levels.size() + 1) {
                    System.out.println("Introduce el nombre del nivel:");
                    String name = scanner.nextLine();
                    System.out.println("Introduce la descripción del nivel:");
                    String description = scanner.nextLine();
                    Level newLevel = new Level(name, description, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    levels.add(newLevel);
                    System.out.println("Nivel añadido con éxito.");
                } else if (choiceInt == levels.size() + 2) {
                    break;
                } else {
                    System.out.println("Opción no válida, por favor inténtelo de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido.");
            }
        }
    }

    private static void manageLevel(Level level) {
        while (true) {
            System.out.println("\n--- Gestionar Nivel: " + level.getName() + " ---");
            System.out.println("Descripción: " + level.getDescription());

            System.out.println("\n--- Capas ---");
            List<Layer> layers = level.getLayers();
            if (layers.isEmpty()) {
                System.out.println("No hay capas en este nivel.");
            } else {
                for (int i = 0; i < layers.size(); i++) {
                    Layer layer = layers.get(i);
                    System.out.println((i + 1) + ". " + layer.getName());
                }
            }

            System.out.println("\n--- Zonas ---");
            List<Zone> zones = level.getZones();
            if (zones.isEmpty()) {
                System.out.println("No hay zonas en este nivel.");
            } else {
                for (int i = 0; i < zones.size(); i++) {
                    Zone zone = zones.get(i);
                    System.out.println((i + 1) + ". " + zone.getType());
                }
            }

            System.out.println("\n--- Sprites ---");
            List<Sprite> sprites = level.getSprites();
            if (sprites.isEmpty()) {
                System.out.println("No hay sprites en este nivel.");
            } else {
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.get(i);
                    System.out.println((i + 1) + ". " + sprite.getType());
                }
            }

            System.out.println("\n--- Opciones ---");
            System.out.println("1. Añadir Capa");
            System.out.println("2. Añadir Zona");
            System.out.println("3. Añadir Sprite");
            System.out.println("4. Volver a Gestionar Niveles");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addLayer(level);
            } else if (choice.equals("2")) {
                addZone(level);
            } else if (choice.equals("3")) {
                addSprite(level);
            } else if (choice.equals("4")) {
                break;
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
            }
        }
    }

    private static void addLayer(Level level) {
        System.out.println("\n--- Añadir Nueva Capa ---");

        System.out.println("Introduce el nombre de la capa:");
        String name = scanner.nextLine();

        System.out.println("Introduce el archivo de la hoja de tiles (por ejemplo, 'tileset_0.png'):");
        String tilesSheetFile = scanner.nextLine();

        System.out.println("Introduce la posición X:");
        int x = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce la posición Y:");
        int y = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce la profundidad:");
        int depth = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el ancho de los tiles:");
        int tilesWidth = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el alto de los tiles:");
        int tilesHeight = Integer.parseInt(scanner.nextLine());

        List<List<Integer>> tileMap = new ArrayList<>();
        level.addLayer(new Layer(name, tilesSheetFile, x, y, depth, tilesWidth, tilesHeight, tileMap));

        System.out.println("Capa añadida con éxito.");
    }

    private static void addZone(Level level) {
        System.out.println("\n--- Añadir Nueva Zona ---");

        System.out.println("Introduce el tipo de zona (por ejemplo, 'stone' o 'water'):");
        String type = scanner.nextLine();

        System.out.println("Introduce el color de la zona (por ejemplo, 'grey' o 'blue'):");
        String color = scanner.nextLine();

        System.out.println("Introduce la posición X:");
        int x = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce la posición Y:");
        int y = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el ancho:");
        int width = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el alto:");
        int height = Integer.parseInt(scanner.nextLine());

        level.addZone(new Zone(type, color, x, y, width, height));

        System.out.println("Zona añadida con éxito.");
    }

    private static void addSprite(Level level) {
        System.out.println("\n--- Añadir Nuevo Sprite ---");

        System.out.println("Introduce el tipo de sprite (por ejemplo, 'pirate_walk'):");
        String type = scanner.nextLine();

        System.out.println("Introduce el archivo de imagen (por ejemplo, 'pirate_walk.png'):");
        String imageFile = scanner.nextLine();

        System.out.println("Introduce la posición X:");
        int x = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce la posición Y:");
        int y = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el ancho:");
        int width = Integer.parseInt(scanner.nextLine());

        System.out.println("Introduce el alto:");
        int height = Integer.parseInt(scanner.nextLine());

        level.getSprites().add(new Sprite(type, imageFile, x, y, width, height));

        System.out.println("Sprite añadido con éxito.");
    }
}