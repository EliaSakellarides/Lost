package com.lost.minigames;

/**
 * Interfaccia comune a tutti i minigame.
 * Ogni minigame gestisce il proprio stato, l'input del giocatore
 * (tramite bottoni A/B/C o testo libero) e il display testuale.
 */
public interface MiniGame {
    /** {@return il nome del minigame} */
    String getName();
    /** {@return le istruzioni da mostrare al giocatore} */
    String getInstructions();
    /** {@return lo stato di avanzamento corrente} */
    MiniGameState getState();
    /**
     * Gestisce la pressione di uno dei bottoni A/B/C.
     * @param button identificativo del bottone premuto ("A", "B" o "C")
     * @return testo di risposta da mostrare
     */
    String handleButtonInput(String button);
    /**
     * Gestisce un input testuale libero del giocatore.
     * @param text testo digitato dal giocatore
     * @return testo di risposta da mostrare
     */
    String handleTextInput(String text);
    /** {@return il testo della schermata corrente del minigame} */
    String getCurrentDisplay();
    /** {@return l'etichetta del bottone A} */
    String getButtonALabel();
    /** {@return l'etichetta del bottone B} */
    String getButtonBLabel();
    /** {@return l'etichetta del bottone C} */
    String getButtonCLabel();
    /** Riporta il minigame allo stato iniziale. */
    void reset();
}
