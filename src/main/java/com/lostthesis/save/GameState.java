package com.lostthesis.save;

import java.util.List;
import java.util.Map;

/**
 * POJO che contiene tutto lo stato del gioco per il salvataggio
 */
public class GameState {

    // Stato giocatore
    private String playerName;
    private int health;
    private int sanity;
    private int daysOnIsland;
    private String currentRoomKey;
    private List<ItemData> inventory;

    // Stato narrativa
    private int currentChapter;
    private boolean currentChapterCompleted;
    private boolean currentChapterStarted;
    private boolean gameRunning;
    private boolean gameWon;

    // Flag di stato eventi
    private boolean hatchOpened;
    private boolean blackRockExplored;
    private boolean jacobMet;
    private boolean templeBathed;
    private boolean dynamiteActive;
    private int dynamiteTimer;
    private int smokeMonsterTimer;
    private int othersTimer;

    // Oggetti nelle stanze (roomKey -> lista oggetti)
    private Map<String, List<ItemData>> roomItems;

    public GameState() {}

    // Player
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getSanity() { return sanity; }
    public void setSanity(int sanity) { this.sanity = sanity; }
    public int getDaysOnIsland() { return daysOnIsland; }
    public void setDaysOnIsland(int daysOnIsland) { this.daysOnIsland = daysOnIsland; }
    public String getCurrentRoomKey() { return currentRoomKey; }
    public void setCurrentRoomKey(String currentRoomKey) { this.currentRoomKey = currentRoomKey; }
    public List<ItemData> getInventory() { return inventory; }
    public void setInventory(List<ItemData> inventory) { this.inventory = inventory; }

    // Narrativa
    public int getCurrentChapter() { return currentChapter; }
    public void setCurrentChapter(int currentChapter) { this.currentChapter = currentChapter; }
    public boolean isCurrentChapterCompleted() { return currentChapterCompleted; }
    public void setCurrentChapterCompleted(boolean currentChapterCompleted) { this.currentChapterCompleted = currentChapterCompleted; }
    public boolean isCurrentChapterStarted() { return currentChapterStarted; }
    public void setCurrentChapterStarted(boolean currentChapterStarted) { this.currentChapterStarted = currentChapterStarted; }
    public boolean isGameRunning() { return gameRunning; }
    public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }
    public boolean isGameWon() { return gameWon; }
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }

    // Flag eventi
    public boolean isHatchOpened() { return hatchOpened; }
    public void setHatchOpened(boolean hatchOpened) { this.hatchOpened = hatchOpened; }
    public boolean isBlackRockExplored() { return blackRockExplored; }
    public void setBlackRockExplored(boolean blackRockExplored) { this.blackRockExplored = blackRockExplored; }
    public boolean isJacobMet() { return jacobMet; }
    public void setJacobMet(boolean jacobMet) { this.jacobMet = jacobMet; }
    public boolean isTempleBathed() { return templeBathed; }
    public void setTempleBathed(boolean templeBathed) { this.templeBathed = templeBathed; }
    public boolean isDynamiteActive() { return dynamiteActive; }
    public void setDynamiteActive(boolean dynamiteActive) { this.dynamiteActive = dynamiteActive; }
    public int getDynamiteTimer() { return dynamiteTimer; }
    public void setDynamiteTimer(int dynamiteTimer) { this.dynamiteTimer = dynamiteTimer; }
    public int getSmokeMonsterTimer() { return smokeMonsterTimer; }
    public void setSmokeMonsterTimer(int smokeMonsterTimer) { this.smokeMonsterTimer = smokeMonsterTimer; }
    public int getOthersTimer() { return othersTimer; }
    public void setOthersTimer(int othersTimer) { this.othersTimer = othersTimer; }

    // Stanze
    public Map<String, List<ItemData>> getRoomItems() { return roomItems; }
    public void setRoomItems(Map<String, List<ItemData>> roomItems) { this.roomItems = roomItems; }
}
