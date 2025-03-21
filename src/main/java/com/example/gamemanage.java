package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.InputStream;

public class gamemanage {
    private static Scanner scanner = new Scanner(System.in);
    private static final String RESOURCES_PATH = "uf4/uf4/src/main/resources/";
public static void main(String[] args) {
        String resourcesPath = "uf4/uf4/src/main/resources/"; // Ruta de la carpeta
    
        // Paso 1: Listar archivos JSON en la carpeta
        List<String> jsonFiles = listJsonFilesInFolder(resourcesPath);
        if (jsonFiles.isEmpty()) {
            System.out.println("No se encontraron archivos JSON en la carpeta: " + resourcesPath);
            return;
        }
    
        // Paso 2: Seleccionar un archivo JSON
        String selectedFile = selectJsonFile(jsonFiles); // Pasa la lista de archivos JSON
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

    private static List<String> listJsonFilesInResources() {
    List<String> jsonFiles = new ArrayList<>();
    File resourcesDir = new File("src/main/resources/"); // Ruta a la carpeta resources
    if (resourcesDir.exists() && resourcesDir.isDirectory()) {
        File[] files = resourcesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                jsonFiles.add(file.getName());
            }
        }
    }
    return jsonFiles;
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

private static void saveGameToJson(Game game, String folderPath, String filename) {
    File folder = new File(folderPath);
    if (!folder.exists()) {
        boolean created = folder.mkdirs(); // Crear la carpeta si no existe
        if (!created) {
            System.out.println("No se pudo crear la carpeta: " + folderPath);
            return;
        }
    }

    String filePath = folderPath + filename;
    try (FileWriter file = new FileWriter(filePath)) {
        file.write(game.toJson().toJSONString());
        file.flush();
        System.out.println("Juego guardado en: " + filePath);
    } catch (Exception e) {
        System.out.println("Error al guardar el archivo JSON: " + e.getMessage());
    }
}

private static String selectJsonFile() {
    List<String> jsonFiles = listJsonFilesInResources();
    if (jsonFiles.isEmpty()) {
        System.out.println("No se encontraron archivos JSON en la carpeta resources.");
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

private static Game loadGameFromJson(String filename) {
    JSONParser parser = new JSONParser();
    try (InputStream inputStream = GameManager.class.getClassLoader().getResourceAsStream(filename)) {
        if (inputStream == null) {
            throw new FileNotFoundException("No se encontró " + filename + " en el classpath.");
        }
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        String name = (String) jsonObject.get("name");
        JSONArray levelsArray = (JSONArray) jsonObject.get("levels");
        List<Level> levels = new ArrayList<>();
        for (Object levelObj : levelsArray) {
            JSONObject levelJson = (JSONObject) levelObj;
            String levelName = (String) levelJson.get("name");
            String description = (String) levelJson.get("description");
            JSONArray layersArray = (JSONArray) levelJson.get("layers");
            List<Layer> layers = new ArrayList<>();
            for (Object layerObj : layersArray) {
                JSONObject layerJson = (JSONObject) layerObj;
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
                layers.add(new Layer(layerName, tilesSheetFile, x, y, depth, tilesWidth, tilesHeight, tileMap));
            }
            JSONArray zonesArray = (JSONArray) levelJson.get("zones");
            List<Zone> zones = new ArrayList<>();
            for (Object zoneObj : zonesArray) {
                JSONObject zoneJson = (JSONObject) zoneObj;
                String type = (String) zoneJson.get("type");
                String color = (String) zoneJson.get("color");
                int x = ((Long) zoneJson.get("x")).intValue();
                int y = ((Long) zoneJson.get("y")).intValue();
                int width = ((Long) zoneJson.get("width")).intValue();
                int height = ((Long) zoneJson.get("height")).intValue();
                zones.add(new Zone(type, color, x, y, width, height));
            }
            JSONArray spritesArray = (JSONArray) levelJson.get("sprites");
            List<Sprite> sprites = new ArrayList<>();
            for (Object spriteObj : spritesArray) {
                JSONObject spriteJson = (JSONObject) spriteObj;
                String type = (String) spriteJson.get("type");
                String imageFile = (String) spriteJson.get("imageFile");
                int x = ((Long) spriteJson.get("x")).intValue();
                int y = ((Long) spriteJson.get("y")).intValue();
                int width = ((Long) spriteJson.get("width")).intValue();
                int height = ((Long) spriteJson.get("height")).intValue();
                sprites.add(new Sprite(type, imageFile, x, y, width, height));
            }
            levels.add(new Level(levelName, description, layers, zones, sprites));
        }
        return new Game(name, levels);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

private static void saveGameToJson(Game game, String filename) {
    if (!filename.endsWith(".json")) {
        filename += ".json";
    }
    String resourcesPath = "src/main/resources/";
    File resourcesDir = new File(resourcesPath);
    if (!resourcesDir.exists()) {
        boolean created = resourcesDir.mkdirs();
        if (!created) {
            System.out.println("No se pudo crear la carpeta resources. Verifica los permisos.");
            return;
        }
    }

    String filePath = resourcesPath + filename;

    try (FileWriter file = new FileWriter(filePath)) {
        file.write(game.toJson().toJSONString());
        file.flush();
        System.out.println("Juego guardado en: " + filePath);

        File savedFile = new File(filePath);
        if (savedFile.exists()) {
            System.out.println("El archivo se ha creado correctamente.");
        } else {
            System.out.println("El archivo no se ha creado. Verifica los permisos.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
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

private static void playInTerminal(Game game) {
    List<Level> levels = game.getLevels();
    while (true) {
        System.out.println("\n--- Selecciona un Nivel para Jugar ---");
        for (int i = 0; i < levels.size(); i++) {
            System.out.println((i + 1) + ". " + levels.get(i).getName());
        }
        System.out.println((levels.size() + 1) + ". Volver al Menú Principal");

        String choice = scanner.nextLine();
        try {
            int choiceInt = Integer.parseInt(choice);

            if (choiceInt >= 1 && choiceInt <= levels.size()) {
                Level selectedLevel = levels.get(choiceInt - 1);
                playLevelInTerminal(selectedLevel);
            } else if (choiceInt == levels.size() + 1) {
                break;
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, introduce un número válido.");
        }
    }
}

private static void playLevelInTerminal(Level level) {
    // Verificar si el nivel tiene capas
    if (level.getLayers().isEmpty()) {
        System.out.println("Este nivel no tiene capas. No se puede jugar.");
        return; // Salir del método si no hay capas
    }

    // Obtener la primera capa (Fons 0a)
    Layer layer = level.getLayers().get(0);
    List<List<Integer>> tileMap = layer.getTileMap();

    // Posición inicial del jugador
    int playerX = 1;
    int playerY = 1;

    Scanner scanner = new Scanner(System.in);
    while (true) {
        // Renderizar el nivel en la terminal
        renderLevel(tileMap, playerX, playerY);

        // Leer la entrada del usuario
        System.out.println("Mueve al jugador (W: arriba, A: izquierda, S: abajo, D: derecha, Q: salir):");
        String input = scanner.nextLine().toUpperCase();

        // Mover al jugador
        if (input.equals("Q")) {
            break; // Salir del juego
        }

        // Actualizar la posición del jugador
        int newX = playerX;
        int newY = playerY;

        switch (input) {
            case "W": newY--; break; // Arriba
            case "A": newX--; break; // Izquierda
            case "S": newY++; break; // Abajo
            case "D": newX++; break; // Derecha
        }

        // Verificar si la nueva posición es válida (no es una pared)
        if (newX >= 0 && newY >= 0 && newY < tileMap.size() && newX < tileMap.get(newY).size()) {
            int tile = tileMap.get(newY).get(newX);
            if (tile != 319 && tile != 320) { // Si no es una pared
                playerX = newX;
                playerY = newY;
            }
        }
    }
}

private static void renderLevel(List<List<Integer>> tileMap, int playerX, int playerY) {
    for (int y = 0; y < tileMap.size(); y++) {
        for (int x = 0; x < tileMap.get(y).size(); x++) {
            if (x == playerX && y == playerY) {
                System.out.print('@'); // Jugador
            } else {
                char tileChar = getTileChar(tileMap.get(y).get(x));
                System.out.print(tileChar);
            }
        }
        System.out.println(); // Nueva línea al final de cada fila
    }
}

private static void movePlayer(String input, List<List<Integer>> tileMap, int playerX, int playerY) {
    int newX = playerX;
    int newY = playerY;

    switch (input) {
        case "W": newY--; break;
        case "A": newX--; break;
        case "S": newY++; break;
        case "D": newX++; break;
    }

    // Verificar si la nueva posición es válida (no es una pared)
    if (newX >= 0 && newY >= 0 && newY < tileMap.size() && newX < tileMap.get(newY).size()) {
        int tile = tileMap.get(newY).get(newX);
        if (tile != 319 && tile != 320) { // Si no es una pared
            playerX = newX;
            playerY = newY;
        }
    }
}

private static char getTileChar(int tile) {
    // Asignar un carácter ASCII basado en el valor del tile
    switch (tile) {
        case 21: return '.'; // Suelo
        case 319: return '#'; // Pared
        case 320: return '#'; // Pared
        case 340: return '~'; // Agua
        case 341: return '~'; // Agua
        default: return ' '; // Espacio vacío
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

                // 🔥 Guardar automáticamente el nuevo nivel
                saveGameToJson(game, "game.json");
                System.out.println("Juego guardado correctamente.");
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
        // Mostrar información del nivel
        System.out.println("\n--- Gestionar Nivel: " + level.getName() + " ---");
        System.out.println("Descripción: " + level.getDescription());

        // Mostrar información de las capas
        System.out.println("\n--- Capas ---");
        List<Layer> layers = level.getLayers();
        if (layers.isEmpty()) {
            System.out.println("No hay capas en este nivel.");
        } else {
            for (int i = 0; i < layers.size(); i++) {
                Layer layer = layers.get(i);
                System.out.println((i + 1) + ". " + layer.getName());
                System.out.println("   - Archivo de tiles: " + layer.getTilesSheetFile());
                System.out.println("   - Posición (X, Y): (" + layer.getX() + ", " + layer.getY() + ")");
                System.out.println("   - Profundidad: " + layer.getDepth());
                System.out.println("   - Dimensiones de los tiles: " + layer.getTilesWidth() + "x" + layer.getTilesHeight());
            }
        }

        // Mostrar información de las zonas
        System.out.println("\n--- Zonas ---");
        List<Zone> zones = level.getZones();
        if (zones.isEmpty()) {
            System.out.println("No hay zonas en este nivel.");
        } else {
            for (int i = 0; i < zones.size(); i++) {
                Zone zone = zones.get(i);
                System.out.println((i + 1) + ". " + zone.getType());
                System.out.println("   - Color: " + zone.getColor());
                System.out.println("   - Posición (X, Y): (" + zone.getX() + ", " + zone.getY() + ")");
                System.out.println("   - Dimensiones: " + zone.getWidth() + "x" + zone.getHeight());
            }
        }

        // Mostrar información de los sprites
        System.out.println("\n--- Sprites ---");
        List<Sprite> sprites = level.getSprites();
        if (sprites.isEmpty()) {
            System.out.println("No hay sprites en este nivel.");
        } else {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.get(i);
                System.out.println((i + 1) + ". " + sprite.getType());
                System.out.println("   - Archivo de imagen: " + sprite.getImageFile());
                System.out.println("   - Posición (X, Y): (" + sprite.getX() + ", " + sprite.getY() + ")");
                System.out.println("   - Dimensiones: " + sprite.getWidth() + "x" + sprite.getHeight());
            }
        }

        // Menú de opciones
        System.out.println("\n--- Opciones ---");
        System.out.println("1. Añadir Capa");
        System.out.println("2. Añadir Zona");
        System.out.println("3. Añadir Sprite");
        System.out.println("4. Volver a Gestionar Niveles");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            addLayer(level); // Añadir una nueva capa
        } else if (choice.equals("2")) {
            addZone(level); // Añadir una nueva zona
        } else if (choice.equals("3")) {
            addSprite(level); // Añadir un nuevo sprite
        } else if (choice.equals("4")) {
            break; // Volver al menú anterior
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

    // Crear un tileMap vacío (puedes modificarlo para permitir la entrada del usuario)
    List<List<Integer>> tileMap = new ArrayList<>();

    // Añadir la nueva capa al nivel
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

    // Añadir la nueva zona al nivel
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

    // Añadir el nuevo sprite al nivel
    level.getSprites().add(new Sprite(type, imageFile, x, y, width, height));

    System.out.println("Sprite añadido con éxito.");
}

private static void manageLayers(Level level) {
    List<Layer> layers = level.getLayers();
    while (true) {
        System.out.println("\n--- Gestionar Capas ---");
        for (int i = 0; i < layers.size(); i++) {
            System.out.println((i + 1) + ". " + layers.get(i).getName());
        }
        System.out.println((layers.size() + 1) + ". Añadir Capa");
        System.out.println((layers.size() + 2) + ". Volver a Gestionar Nivel");

        String choice = scanner.nextLine();
        try {
            int choiceInt = Integer.parseInt(choice);

            if (choiceInt >= 1 && choiceInt <= layers.size()) {
                // Gestionar una capa existente
                manageLayer(layers.get(choiceInt - 1));
            } else if (choiceInt == layers.size() + 1) {
                // Añadir una nueva capa
                addLayer(level);
            } else if (choiceInt == layers.size() + 2) {
                // Volver al menú anterior
                break;
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, introduce un número válido.");
        }
    }
}

private static void manageLayer(Layer layer) {
    while (true) {
        System.out.println("\n--- Gestionar Capa: " + layer.getName() + " ---");
        System.out.println("1. Modificar Capa");
        System.out.println("2. Eliminar Capa");
        System.out.println("3. Volver a Gestionar Capas");

        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            // Modificar la capa
            modifyLayer(layer);
        } else if (choice.equals("2")) {
            // Eliminar la capa
            System.out.println("¿Estás seguro de que quieres eliminar la capa '" + layer.getName() + "'? (S/N)");
            String confirm = scanner.nextLine().toUpperCase();
            if (confirm.equals("S")) {
                // Eliminar la capa
                level.getLayers().remove(layer);
                System.out.println("Capa eliminada con éxito.");
                break; // Salir del menú de gestión de capas
            }
        } else if (choice.equals("3")) {
            // Volver al menú anterior
            break;
        } else {
            System.out.println("Opción no válida, por favor inténtelo de nuevo.");
        }
    }
}

private static void modifyLayer(Layer layer) {
    System.out.println("\n--- Modificar Capa: " + layer.getName() + " ---");

    System.out.println("Introduce el nuevo nombre de la capa (actual: " + layer.getName() + "):");
    String name = scanner.nextLine();
    if (!name.isEmpty()) {
        layer.setName(name);
    }

    System.out.println("Introduce el nuevo archivo de la hoja de tiles (actual: " + layer.getTilesSheetFile() + "):");
    String tilesSheetFile = scanner.nextLine();
    if (!tilesSheetFile.isEmpty()) {
        layer.setTilesSheetFile(tilesSheetFile);
    }

    System.out.println("Introduce la nueva posición X (actual: " + layer.getX() + "):");
    String xInput = scanner.nextLine();
    if (!xInput.isEmpty()) {
        layer.setX(Integer.parseInt(xInput));
    }

    System.out.println("Introduce la nueva posición Y (actual: " + layer.getY() + "):");
    String yInput = scanner.nextLine();
    if (!yInput.isEmpty()) {
        layer.setY(Integer.parseInt(yInput));
    }

    System.out.println("Introduce la nueva profundidad (actual: " + layer.getDepth() + "):");
    String depthInput = scanner.nextLine();
    if (!depthInput.isEmpty()) {
        layer.setDepth(Integer.parseInt(depthInput));
    }

    System.out.println("Introduce el nuevo ancho de los tiles (actual: " + layer.getTilesWidth() + "):");
    String tilesWidthInput = scanner.nextLine();
    if (!tilesWidthInput.isEmpty()) {
        layer.setTilesWidth(Integer.parseInt(tilesWidthInput));
    }

    System.out.println("Introduce el nuevo alto de los tiles (actual: " + layer.getTilesHeight() + "):");
    String tilesHeightInput = scanner.nextLine();
    if (!tilesHeightInput.isEmpty()) {
        layer.setTilesHeight(Integer.parseInt(tilesHeightInput));
    }

    System.out.println("Capa modificada con éxito.");
}

    private static void manageZones(Level level) {
        List<Zone> zones = level.getZones();
        while (true) {
            System.out.println("\n--- Gestionar Zonas ---");
            for (int i = 0; i < zones.size(); i++) {
                System.out.println((i + 1) + ". " + zones.get(i).getType());
            }
            System.out.println((zones.size() + 1) + ". Añadir Zona");
            System.out.println((zones.size() + 2) + ". Volver a Gestionar Nivel");
            String choice = scanner.nextLine();
            int choiceInt = Integer.parseInt(choice);
            if (choiceInt >= 1 && choiceInt <= zones.size()) {
                manageZone(zones.get(choiceInt - 1));
            } else if (choiceInt == zones.size() + 1) {
                System.out.println("Introduce el tipo de zona:");
                String type = scanner.nextLine();
                System.out.println("Introduce el color de la zona:");
                String color = scanner.nextLine();
                System.out.println("Introduce la posición X:");
                int x = Integer.parseInt(scanner.nextLine());
                System.out.println("Introduce la posición Y:");
                int y = Integer.parseInt(scanner.nextLine());
                System.out.println("Introduce el ancho:");
                int width = Integer.parseInt(scanner.nextLine());
                System.out.println("Introduce el alto:");
                int height = Integer.parseInt(scanner.nextLine());
                zones.add(new Zone(type, color, x, y, width, height));
            } else if (choiceInt == zones.size() + 2) {
                break;
            } else {
                System.out.println("Opción no válida, por favor inténtelo de nuevo.");
            }
        }
    }

    private static void manageZone(Zone zone) {
        System.out.println("\n--- Gestionar Zona: " + zone.getType() + " ---");
        System.out.println("1. Modificar Zona");
        System.out.println("2. Eliminar Zona");
        System.out.println("3. Volver a Gestionar Zonas");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            System.out.println("Introduce el nuevo tipo de zona:");
            String type = scanner.nextLine();
            System.out.println("Introduce el nuevo color de la zona:");
            String color = scanner.nextLine();
            System.out.println("Introduce la nueva posición X:");
            int x = Integer.parseInt(scanner.nextLine());
            System.out.println("Introduce la nueva posición Y:");
            int y = Integer.parseInt(scanner.nextLine());
            System.out.println("Introduce el nuevo ancho:");
            int width = Integer.parseInt(scanner.nextLine());
            System.out.println("Introduce el nuevo alto:");
            int height = Integer.parseInt(scanner.nextLine());
            zone.setType(type);
            zone.setColor(color);
            zone.setX(x);
            zone.setY(y);
            zone.setWidth(width);
            zone.setHeight(height);
        } else if (choice.equals("2")) {
            // Implementar la lógica para eliminar la zona
        } else if (choice.equals("3")) {
            return;
        } else {
            System.out.println("Opción no válida, por favor inténtelo de nuevo.");
        }
    }
    
}