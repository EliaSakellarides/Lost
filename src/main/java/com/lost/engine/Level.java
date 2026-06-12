package com.lost.engine;

import java.util.*;

/**
 * Rappresenta un capitolo/livello del gioco
 */
public class Level {
    private String key;
    private String title;
    private String prompt;
    private List<String> acceptableAnswers;
    private String hint;
    private Map<String, String> choices; // A -> risposta, B -> risposta, C -> risposta
    private String miniGameKey; // chiave del mini gioco opzionale (es. "jungle_tracking")

    /**
     * Crea un capitolo a risposta libera.
     * @param key chiave identificativa del capitolo
     * @param title titolo del capitolo
     * @param prompt testo narrativo/domanda del capitolo
     * @param acceptableAnswers risposte accettate (case insensitive)
     * @param hint suggerimento mostrato in caso di errore
     */
    public Level(String key, String title, String prompt, List<String> acceptableAnswers, String hint) {
        this.key = key;
        this.title = title;
        this.prompt = prompt;
        this.acceptableAnswers = new ArrayList<>();
        for (String a : acceptableAnswers) this.acceptableAnswers.add(a.toLowerCase());
        this.hint = hint;
        this.choices = null;
    }
    
    /**
     * Crea un capitolo a scelte multiple (A/B/C).
     * @param key chiave identificativa del capitolo
     * @param title titolo del capitolo
     * @param prompt testo narrativo/domanda del capitolo
     * @param choices mappa lettera - testo dell'opzione
     * @param correctChoice lettera della scelta corretta
     * @param hint suggerimento mostrato in caso di errore
     */
    public Level(String key, String title, String prompt, Map<String, String> choices, String correctChoice, String hint) {
        this.key = key;
        this.title = title;
        this.prompt = prompt;
        this.acceptableAnswers = new ArrayList<>();
        this.acceptableAnswers.add(correctChoice.toLowerCase());
        this.hint = hint;
        this.choices = choices;
    }

    /** {@return la chiave identificativa del capitolo} */
    public String getKey() { return key; }
    /** {@return il titolo del capitolo} */
    public String getTitle() { return title; }
    /** {@return il testo narrativo/domanda del capitolo} */
    public String getPrompt() { return prompt; }
    /** {@return il suggerimento per il giocatore} */
    public String getHint() { return hint; }
    /** {@return le opzioni A/B/C, null se il capitolo e' a risposta libera} */
    public Map<String, String> getChoices() { return choices; }
    /** {@return true se il capitolo prevede scelte multiple} */
    public boolean hasChoices() { return choices != null && !choices.isEmpty(); }
    /** {@return la chiave del mini gioco associato, null se assente} */
    public String getMiniGameKey() { return miniGameKey; }
    /**
     * Associa un mini gioco al capitolo.
     * @param miniGameKey chiave del mini gioco (es. "jungle_tracking")
     */
    public void setMiniGameKey(String miniGameKey) { this.miniGameKey = miniGameKey; }
    /** {@return true se il capitolo ha un mini gioco associato} */
    public boolean hasMiniGame() { return miniGameKey != null && !miniGameKey.isEmpty(); }

    /**
     * Verifica se una risposta del giocatore e' corretta.
     * Per i capitoli a scelte confronta la lettera esatta,
     * per quelli a risposta libera accetta anche sottostringhe.
     * @param answer risposta del giocatore
     * @return true se la risposta e' accettata
     */
    public boolean checkAnswer(String answer) {
        if (answer == null) return false;
        String a = answer.trim().toLowerCase();

        if (hasChoices()) {
            for (String ok : acceptableAnswers) {
                if (a.equals(ok)) return true;
            }
            return false;
        }

        for (String ok : acceptableAnswers) {
            if (a.equals(ok) || a.contains(ok)) return true;
        }
        return false;
    }
}
