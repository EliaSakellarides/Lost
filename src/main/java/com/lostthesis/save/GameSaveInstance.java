package com.lostthesis.save;

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

    public GameSaveInstance() {}

    public GameSaveInstance(String slotName, String playerName, int chapter, String chapterTitle) {
        this.slotName = slotName;
        this.playerName = playerName;
        this.chapter = chapter;
        this.chapterTitle = chapterTitle;
        this.timestamp = LocalDateTime.now().format(FMT);
        this.filename = slotName + ".json";
    }

    public String getDisplayText() {
        return slotName + " - " + playerName +
               " | Cap. " + chapter + ": " + chapterTitle +
               " | " + timestamp;
    }

    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getChapter() { return chapter; }
    public void setChapter(int chapter) { this.chapter = chapter; }
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
