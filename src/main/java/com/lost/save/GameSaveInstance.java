package com.lost.save;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Metadati di un singolo salvataggio
 */
public class GameSaveInstance {
    private String slotName;
    private String playerName;
    private int chapter;
    private String chapterTitle;
    private String timestamp;
    private String filename;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Costruttore vuoto richiesto per la deserializzazione JSON. */
    public GameSaveInstance() {}

    /**
     * Crea i metadati di un salvataggio con timestamp corrente.
     * @param slotName nome dello slot
     * @param playerName nome del giocatore
     * @param chapter numero del capitolo raggiunto
     * @param chapterTitle titolo del capitolo raggiunto
     */
    public GameSaveInstance(String slotName, String playerName, int chapter, String chapterTitle) {
        this.slotName = slotName;
        this.playerName = playerName;
        this.chapter = chapter;
        this.chapterTitle = chapterTitle;
        this.timestamp = LocalDateTime.now().format(FMT);
        this.filename = slotName + ".json";
    }

    /**
     * Riga descrittiva del salvataggio da mostrare nella lista.
     * @return testo con slot, giocatore, capitolo e data
     */
    public String getDisplayText() {
        return slotName + " - " + playerName +
               " | Cap. " + chapter + ": " + chapterTitle +
               " | " + timestamp;
    }

    /** {@return il nome dello slot} */
    public String getSlotName() { return slotName; }
    /**
     * Imposta il nome dello slot.
     * @param slotName nome dello slot
     */
    public void setSlotName(String slotName) { this.slotName = slotName; }
    /** {@return il nome del giocatore} */
    public String getPlayerName() { return playerName; }
    /**
     * Imposta il nome del giocatore.
     * @param playerName nome del giocatore
     */
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    /** {@return il numero del capitolo raggiunto} */
    public int getChapter() { return chapter; }
    /**
     * Imposta il capitolo raggiunto.
     * @param chapter numero del capitolo
     */
    public void setChapter(int chapter) { this.chapter = chapter; }
    /** {@return il titolo del capitolo raggiunto} */
    public String getChapterTitle() { return chapterTitle; }
    /**
     * Imposta il titolo del capitolo.
     * @param chapterTitle titolo del capitolo
     */
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    /** {@return la data/ora del salvataggio} */
    public String getTimestamp() { return timestamp; }
    /**
     * Imposta la data/ora del salvataggio.
     * @param timestamp data/ora formattata
     */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    /** {@return il nome del file di salvataggio} */
    public String getFilename() { return filename; }
    /**
     * Imposta il nome del file.
     * @param filename nome del file di salvataggio
     */
    public void setFilename(String filename) { this.filename = filename; }
}
