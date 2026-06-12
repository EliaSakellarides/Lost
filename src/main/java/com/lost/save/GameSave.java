package com.lost.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lost.engine.GameEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce il salvataggio e caricamento dei file di salvataggio.
 * I salvataggi sono memorizzati in ~/.lost/saves/ come JSON.
 * I vecchi salvataggi in ~/.lostthesis/saves/ restano caricabili.
 */
public final class GameSave {

    private GameSave() {
    }

    private static final Path SAVE_DIR = Paths.get(
            System.getProperty("user.home"), ".lost", "saves");
    private static final Path LEGACY_SAVE_DIR = Paths.get(
            System.getProperty("user.home"), ".lostthesis", "saves");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Salva lo stato corrente del gioco in uno slot
     * @param engine motore di gioco da salvare
     * @param slotName nome dello slot di salvataggio
     * @return true se il salvataggio e' andato a buon fine
     */
    public static boolean save(GameEngine engine, String slotName) {
        try {
            Files.createDirectories(SAVE_DIR);
            String safeSlotName = sanitizeSlotName(slotName);
            if (safeSlotName.isEmpty()) {
                return false;
            }

            GameState state = GameConverter.extractState(engine);
            String json = GameConverter.toJson(state);

            // Scrivi il file di salvataggio
            Path saveFile = resolveSlotFile(SAVE_DIR, safeSlotName);
            Files.writeString(saveFile, json);

            // Aggiorna indice salvataggi
            GameSaveInstance instance = new GameSaveInstance(
                    safeSlotName,
                    engine.getPlayer().getName(),
                    engine.getCurrentChapterNumber(),
                    engine.getCurrentChapterTitle()
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
     * @param slotName nome dello slot da caricare
     * @return lo stato caricato, null se lo slot non esiste o e' illeggibile
     */
    public static GameState load(String slotName) {
        try {
            Path saveFile = resolveSaveFile(slotName);
            if (saveFile == null || !Files.exists(saveFile)) {
                return null;
            }
            String json = Files.readString(saveFile);
            return GameConverter.fromJson(json);
        } catch (Exception e) {
            System.out.println("Errore caricamento: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elenca tutti i salvataggi disponibili
     * @return lista dei metadati dei salvataggi trovati
     */
    public static List<GameSaveInstance> listSaves() {
        List<GameSaveInstance> saves = new ArrayList<>();
        addSavesFromDirectory(saves, LEGACY_SAVE_DIR);
        addSavesFromDirectory(saves, SAVE_DIR);
        return saves;
    }

    private static void addSavesFromDirectory(List<GameSaveInstance> saves, Path saveDir) {
        Path indexFile = saveDir.resolve("index.json");
        if (!Files.exists(indexFile)) {
            return;
        }

        try {
            String json = Files.readString(indexFile);
            GameSaveInstance[] instances = GSON.fromJson(json, GameSaveInstance[].class);
            if (instances != null) {
                for (GameSaveInstance inst : instances) {
                    String safeSlotName = sanitizeSlotName(inst.getSlotName());
                    Path saveFile = resolveSlotFile(saveDir, safeSlotName);
                    if (!safeSlotName.isEmpty()
                            && Files.exists(saveFile)
                            && saves.stream().noneMatch(s -> s.getSlotName().equals(safeSlotName))) {
                        inst.setSlotName(safeSlotName);
                        inst.setFilename(safeSlotName + ".json");
                        saves.add(inst);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Errore lettura indice: " + e.getMessage());
        }
    }

    /**
     * Elimina un salvataggio
     * @param slotName nome dello slot da eliminare
     * @return true se l'operazione e' andata a buon fine
     */
    public static boolean delete(String slotName) {
        try {
            String safeSlotName = sanitizeSlotName(slotName);
            if (safeSlotName.isEmpty()) {
                return false;
            }

            Files.deleteIfExists(resolveSlotFile(SAVE_DIR, safeSlotName));
            Files.deleteIfExists(resolveSlotFile(LEGACY_SAVE_DIR, safeSlotName));

            // Aggiorna indice
            List<GameSaveInstance> saves = listSaves();
            saves.removeIf(s -> s.getSlotName().equals(safeSlotName));
            writeIndex(saves);

            return true;
        } catch (IOException e) {
            System.out.println("Errore eliminazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se esiste almeno un salvataggio
     * @return true se c'e' almeno un salvataggio disponibile
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

    /**
     * Normalizza il nome di uno slot: rimuove caratteri non sicuri
     * per il filesystem e limita la lunghezza a 40 caratteri.
     * @param slotName nome richiesto dall'utente
     * @return nome sicuro, stringa vuota se non utilizzabile
     */
    public static String sanitizeSlotName(String slotName) {
        if (slotName == null) {
            return "";
        }
        String safe = slotName.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        safe = safe.replaceAll("_+", "_");
        safe = safe.replaceAll("^_+|_+$", "");
        if (safe.length() > 40) {
            safe = safe.substring(0, 40);
        }
        return safe;
    }

    private static Path resolveSaveFile(String slotName) {
        String safeSlotName = sanitizeSlotName(slotName);
        if (safeSlotName.isEmpty()) {
            return null;
        }

        Path saveFile = resolveSlotFile(SAVE_DIR, safeSlotName);
        if (Files.exists(saveFile)) {
            return saveFile;
        }
        return resolveSlotFile(LEGACY_SAVE_DIR, safeSlotName);
    }

    private static Path resolveSlotFile(Path saveDir, String slotName) {
        Path normalizedDir = saveDir.toAbsolutePath().normalize();
        Path file = normalizedDir.resolve(slotName + ".json").normalize();
        if (!file.startsWith(normalizedDir)) {
            throw new IllegalArgumentException("Slot salvataggio non valido");
        }
        return file;
    }
}
