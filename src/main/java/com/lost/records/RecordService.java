package com.lost.records;

import java.util.List;

public class RecordService {
    private final RecordRepository repository;

    public RecordService() {
        this(new RecordRepository());
    }

    public RecordService(RecordRepository repository) {
        this.repository = repository;
    }

    public GameRecord saveCompletion(String playerName, long completionMillis) {
        String safeName = playerName == null || playerName.isBlank() ? "Sopravvissuto" : playerName.trim();
        return repository.save(safeName, completionMillis);
    }

    public List<GameRecord> getBestRecords(int limit) {
        return repository.findBest(limit);
    }

    public List<GameRecord> getAllRecords() {
        return repository.findAll();
    }
}
