package com.lostthesis.engine;

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
    private String miniGameKey; // chiave del mini gioco opzionale (es. "smoke_chase")

    public Level(String key, String title, String prompt, List<String> acceptableAnswers, String hint) {
        this.key = key;
        this.title = title;
        this.prompt = prompt;
        this.acceptableAnswers = new ArrayList<>();
        for (String a : acceptableAnswers) this.acceptableAnswers.add(a.toLowerCase());
        this.hint = hint;
        this.choices = null;
    }
    
    // Costruttore con scelte multiple
    public Level(String key, String title, String prompt, Map<String, String> choices, String correctChoice, String hint) {
        this.key = key;
        this.title = title;
        this.prompt = prompt;
        this.acceptableAnswers = new ArrayList<>();
        this.acceptableAnswers.add(correctChoice.toLowerCase());
        this.hint = hint;
        this.choices = choices;
    }

    public String getKey() { return key; }
    public String getTitle() { return title; }
    public String getPrompt() { return prompt; }
    public String getHint() { return hint; }
    public Map<String, String> getChoices() { return choices; }
    public boolean hasChoices() { return choices != null && !choices.isEmpty(); }
    public String getMiniGameKey() { return miniGameKey; }
    public void setMiniGameKey(String miniGameKey) { this.miniGameKey = miniGameKey; }
    public boolean hasMiniGame() { return miniGameKey != null && !miniGameKey.isEmpty(); }

    public boolean checkAnswer(String answer) {
        if (answer == null) return false;
        String a = answer.trim().toLowerCase();
        for (String ok : acceptableAnswers) {
            if (a.equals(ok) || a.contains(ok)) return true;
        }
        return false;
    }
}
