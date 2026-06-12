package com.lost.records;

import java.util.List;

/**
 * Servizio della classifica: valida i dati e delega al repository H2.
 */
public class RecordService {
    private final RecordRepository repository;

    /** Crea il servizio sul database dei record predefinito. */
    public RecordService() {
        this(new RecordRepository());
    }

    /**
     * Crea il servizio su un repository specifico (usato nei test).
     * @param repository repository dei record da utilizzare
     */
    public RecordService(RecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra il completamento di una partita.
     * @param playerName nome del giocatore (default "Sopravvissuto" se vuoto)
     * @param completionMillis tempo di completamento in millisecondi
     * @return il record salvato
     */
    public GameRecord saveCompletion(String playerName, long completionMillis) {
        String safeName = playerName == null || playerName.isBlank() ? "Sopravvissuto" : playerName.trim();
        return repository.save(safeName, completionMillis);
    }

    /**
     * Restituisce i migliori tempi in ordine crescente.
     * @param limit numero massimo di record
     * @return lista dei migliori record
     */
    public List<GameRecord> getBestRecords(int limit) {
        return repository.findBest(limit);
    }

    /**
     * Restituisce tutti i record salvati.
     * @return lista completa dei record, dal piu' recente
     */
    public List<GameRecord> getAllRecords() {
        return repository.findAll();
    }
}
