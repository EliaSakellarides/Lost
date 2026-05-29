package com.lost.records;

public class GameRecord {
    private int id;
    private String playerName;
    private long completionMillis;
    private String completedAt;

    public GameRecord() {
    }

    public GameRecord(int id, String playerName, long completionMillis, String completedAt) {
        this.id = id;
        this.playerName = playerName;
        this.completionMillis = completionMillis;
        this.completedAt = completedAt;
    }

    public int getId() { return id; }
    public String getPlayerName() { return playerName; }
    public long getCompletionMillis() { return completionMillis; }
    public String getCompletedAt() { return completedAt; }

    public String getFormattedTime() {
        long totalSeconds = completionMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
