package com.lostthesis.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lostthesis.engine.GameEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Gestisce il salvataggio e caricamento dei file di salvataggio.
 * I salvataggi sono memorizzati in ~/.lostthesis/saves/ come JSON.
 */
public class GameSave {

    private static final Path SAVE_DIR = Paths.get(
            System.getProperty("user.home"), ".lostthesis", "saves");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Salva lo stato corrente del gioco in uno slot
     */
    public static boolean save(GameEngine engine, String slotName) {
        try {
            Files.createDirectories(SAVE_DIR);

            GameState state = GameConverter.extractState(engine);
            String json = GameConverter.toJson(state);

            // Scrivi il file di salvataggio
            Path saveFile = SAVE_DIR.resolve(slotName + ".json");
            Files.writeString(saveFile, json);

            // Aggiorna indice salvataggi
            GameSaveInstance instance = new GameSaveInstance(
                    slotName,
                    engine.getPlayer().getName(),
                    engine.getCurrentChapterNumber(),
                    getCurrentChapterTitle(engine)
            );
            saveIndex(instance);

            return true;
        } catch (IOException e) {
            System.out.println("Errore salvataggio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carica uno stato di gioco da uno slot
     */
    public static GameState load(String slotName) {
        try {
            Path saveFile = SAVE_DIR.resolve(slotName + ".json");
            if (!Files.exists(saveFile)) {
                return null;
            }
            String json = Files.readString(saveFile);
            return GameConverter.fromJson(json);
        } catch (IOException e) {
            System.out.println("Errore caricamento: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elenca tutti i salvataggi disponibili
     */
    public static List<GameSaveInstance> listSaves() {
        List<GameSaveInstance> saves = new ArrayList<>();
        Path indexFile = SAVE_DIR.resolve("index.json");

        if (!Files.exists(indexFile)) {
            return saves;
        }

        try {
            String json = Files.readString(indexFile);
            GameSaveInstance[] instances = GSON.fromJson(json, GameSaveInstance[].class);
            if (instances != null) {
                for (GameSaveInstance inst : instances) {
                    // Verifica che il file di salvataggio esista ancora
                    if (Files.exists(SAVE_DIR.resolve(inst.getFilename()))) {
                        saves.add(inst);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore lettura indice: " + e.getMessage());
        }

        return saves;
    }

    /**
     * Elimina un salvataggio
     */
    public static boolean delete(String slotName) {
        try {
            Path saveFile = SAVE_DIR.resolve(slotName + ".json");
            Files.deleteIfExists(saveFile);

            // Aggiorna indice
            List<GameSaveInstance> saves = listSaves();
            saves.removeIf(s -> s.getSlotName().equals(slotName));
            writeIndex(saves);

            return true;
        } catch (IOException e) {
            System.out.println("Errore eliminazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se esiste almeno un salvataggio
     */
    public static boolean hasSaves() {
        return !listSaves().isEmpty();
    }

    private static void saveIndex(GameSaveInstance instance) throws IOException {
        List<GameSaveInstance> saves = listSaves();

        // Sostituisci se lo slot esiste gia'
        saves.removeIf(s -> s.getSlotName().equals(instance.getSlotName()));
        saves.add(instance);

        writeIndex(saves);
    }

    private static void writeIndex(List<GameSaveInstance> saves) throws IOException {
        Files.createDirectories(SAVE_DIR);
        Path indexFile = SAVE_DIR.resolve("index.json");
        Files.writeString(indexFile, GSON.toJson(saves));
    }

    private static String getCurrentChapterTitle(GameEngine engine) {
        int chapterNum = engine.getCurrentChapterNumber();
        int total = engine.getTotalChapters();
        if (chapterNum > total) {
            return "Completato";
        }
        return "Capitolo " + chapterNum;
    }
}
