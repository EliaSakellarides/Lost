package com.lost.save;

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
    private boolean blackRockExplored;
    private boolean radioBatteryInstalled;
    private boolean radioAntennaInstalled;
    private boolean radioFuseInstalled;
    private boolean radioRepaired;
    private boolean radioMessageReceived;
    private boolean dynamiteActive;
    private int dynamiteTimer;

    // Oggetti nelle stanze (roomKey -> lista oggetti)
    private Map<String, List<ItemData>> roomItems;

    /** Costruttore vuoto richiesto per la deserializzazione JSON. */
    public GameState() {}

    // Player
    /** {@return il nome del giocatore} */
    public String getPlayerName() { return playerName; }
    /**
     * Imposta il nome del giocatore.
     * @param playerName nome del giocatore
     */
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    /** {@return la salute del giocatore (0-100)} */
    public int getHealth() { return health; }
    /**
     * Imposta la salute del giocatore.
     * @param health salute (0-100)
     */
    public void setHealth(int health) { this.health = health; }
    /** {@return la sanita' mentale del giocatore (0-100)} */
    public int getSanity() { return sanity; }
    /**
     * Imposta la sanita' mentale del giocatore.
     * @param sanity sanita' mentale (0-100)
     */
    public void setSanity(int sanity) { this.sanity = sanity; }
    /** {@return i giorni trascorsi sull'isola} */
    public int getDaysOnIsland() { return daysOnIsland; }
    /**
     * Imposta i giorni trascorsi sull'isola.
     * @param daysOnIsland numero di giorni
     */
    public void setDaysOnIsland(int daysOnIsland) { this.daysOnIsland = daysOnIsland; }
    /** {@return la chiave della stanza corrente} */
    public String getCurrentRoomKey() { return currentRoomKey; }
    /**
     * Imposta la chiave della stanza corrente.
     * @param currentRoomKey chiave della stanza
     */
    public void setCurrentRoomKey(String currentRoomKey) { this.currentRoomKey = currentRoomKey; }
    /** {@return l'inventario del giocatore} */
    public List<ItemData> getInventory() { return inventory; }
    /**
     * Imposta l'inventario del giocatore.
     * @param inventory lista degli oggetti posseduti
     */
    public void setInventory(List<ItemData> inventory) { this.inventory = inventory; }

    // Narrativa
    /** {@return l'indice del capitolo corrente (0-based)} */
    public int getCurrentChapter() { return currentChapter; }
    /**
     * Imposta l'indice del capitolo corrente.
     * @param currentChapter indice del capitolo (0-based)
     */
    public void setCurrentChapter(int currentChapter) { this.currentChapter = currentChapter; }
    /** {@return true se il capitolo corrente e' stato completato} */
    public boolean isCurrentChapterCompleted() { return currentChapterCompleted; }
    /**
     * Imposta il flag di capitolo completato.
     * @param currentChapterCompleted true se il capitolo e' completato
     */
    public void setCurrentChapterCompleted(boolean currentChapterCompleted) { this.currentChapterCompleted = currentChapterCompleted; }
    /** {@return true se il capitolo corrente e' stato avviato} */
    public boolean isCurrentChapterStarted() { return currentChapterStarted; }
    /**
     * Imposta il flag di capitolo avviato.
     * @param currentChapterStarted true se il capitolo e' avviato
     */
    public void setCurrentChapterStarted(boolean currentChapterStarted) { this.currentChapterStarted = currentChapterStarted; }
    /** {@return true se la partita e' in corso} */
    public boolean isGameRunning() { return gameRunning; }
    /**
     * Imposta il flag di partita in corso.
     * @param gameRunning true se la partita e' in corso
     */
    public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }
    /** {@return true se la partita e' stata vinta} */
    public boolean isGameWon() { return gameWon; }
    /**
     * Imposta il flag di partita vinta.
     * @param gameWon true se la partita e' vinta
     */
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }

    // Flag eventi
    /** {@return true se la Black Rock e' stata esplorata} */
    public boolean isBlackRockExplored() { return blackRockExplored; }
    /**
     * Imposta il flag di esplorazione della Black Rock.
     * @param blackRockExplored true se esplorata
     */
    public void setBlackRockExplored(boolean blackRockExplored) { this.blackRockExplored = blackRockExplored; }
    /** {@return true se la batteria e' stata installata nella radio} */
    public boolean isRadioBatteryInstalled() { return radioBatteryInstalled; }
    /**
     * Imposta il flag della batteria installata.
     * @param radioBatteryInstalled true se installata
     */
    public void setRadioBatteryInstalled(boolean radioBatteryInstalled) { this.radioBatteryInstalled = radioBatteryInstalled; }
    /** {@return true se l'antenna e' stata installata nella radio} */
    public boolean isRadioAntennaInstalled() { return radioAntennaInstalled; }
    /**
     * Imposta il flag dell'antenna installata.
     * @param radioAntennaInstalled true se installata
     */
    public void setRadioAntennaInstalled(boolean radioAntennaInstalled) { this.radioAntennaInstalled = radioAntennaInstalled; }
    /** {@return true se il fusibile e' stato installato nella radio} */
    public boolean isRadioFuseInstalled() { return radioFuseInstalled; }
    /**
     * Imposta il flag del fusibile installato.
     * @param radioFuseInstalled true se installato
     */
    public void setRadioFuseInstalled(boolean radioFuseInstalled) { this.radioFuseInstalled = radioFuseInstalled; }
    /** {@return true se la radio e' stata riparata} */
    public boolean isRadioRepaired() { return radioRepaired; }
    /**
     * Imposta il flag della radio riparata.
     * @param radioRepaired true se riparata
     */
    public void setRadioRepaired(boolean radioRepaired) { this.radioRepaired = radioRepaired; }
    /** {@return true se il messaggio radio e' stato ricevuto} */
    public boolean isRadioMessageReceived() { return radioMessageReceived; }
    /**
     * Imposta il flag del messaggio radio ricevuto.
     * @param radioMessageReceived true se ricevuto
     */
    public void setRadioMessageReceived(boolean radioMessageReceived) { this.radioMessageReceived = radioMessageReceived; }
    /** {@return true se la dinamite e' innescata} */
    public boolean isDynamiteActive() { return dynamiteActive; }
    /**
     * Imposta il flag della dinamite innescata.
     * @param dynamiteActive true se innescata
     */
    public void setDynamiteActive(boolean dynamiteActive) { this.dynamiteActive = dynamiteActive; }
    /** {@return i turni rimanenti del timer della dinamite} */
    public int getDynamiteTimer() { return dynamiteTimer; }
    /**
     * Imposta il timer della dinamite.
     * @param dynamiteTimer turni rimanenti
     */
    public void setDynamiteTimer(int dynamiteTimer) { this.dynamiteTimer = dynamiteTimer; }

    // Stanze
    /** {@return gli oggetti presenti nelle stanze, indicizzati per chiave stanza} */
    public Map<String, List<ItemData>> getRoomItems() { return roomItems; }
    /**
     * Imposta gli oggetti presenti nelle stanze.
     * @param roomItems mappa chiave stanza - lista oggetti
     */
    public void setRoomItems(Map<String, List<ItemData>> roomItems) { this.roomItems = roomItems; }
}
