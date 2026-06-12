package com.lost.records;

/**
 * Record di completamento di una partita: giocatore,
 * tempo impiegato e data di completamento.
 */
public class GameRecord {
    private int id;
    private String playerName;
    private long completionMillis;
    private String completedAt;

    /** Costruttore vuoto richiesto per la deserializzazione. */
    public GameRecord() {
    }

    /**
     * Crea un record completo.
     * @param id identificativo del record nel database
     * @param playerName nome del giocatore
     * @param completionMillis tempo di completamento in millisecondi
     * @param completedAt data/ora di completamento
     */
    public GameRecord(int id, String playerName, long completionMillis, String completedAt) {
        this.id = id;
        this.playerName = playerName;
        this.completionMillis = completionMillis;
        this.completedAt = completedAt;
    }

    /** {@return l'identificativo del record nel database} */
    public int getId() { return id; }
    /** {@return il nome del giocatore} */
    public String getPlayerName() { return playerName; }
    /** {@return il tempo di completamento in millisecondi} */
    public long getCompletionMillis() { return completionMillis; }
    /** {@return la data/ora di completamento} */
    public String getCompletedAt() { return completedAt; }

    /**
     * Formatta il tempo di completamento come mm:ss.
     * @return tempo nel formato "MM:SS"
     */
    public String getFormattedTime() {
        long totalSeconds = completionMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
