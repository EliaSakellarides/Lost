package com.lostthesis.minigames;

/**
 * Interfaccia comune a tutti i minigame.
 * Ogni minigame gestisce il proprio stato, l'input del giocatore
 * (tramite bottoni A/B/C o testo libero) e il display testuale.
 */
public interface MiniGame {
    String getName();
    String getInstructions();
    MiniGameState getState();
    String handleButtonInput(String button);
    String handleTextInput(String text);
    String getCurrentDisplay();
    String getButtonALabel();
    String getButtonBLabel();
    String getButtonCLabel();
    void reset();
}
