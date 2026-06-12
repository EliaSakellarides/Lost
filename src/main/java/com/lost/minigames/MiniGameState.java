package com.lost.minigames;

/**
 * Stato di avanzamento di un minigame.
 */
public enum MiniGameState {
    /** Non ancora avviato. */
    PENDING,
    /** In corso. */
    IN_PROGRESS,
    /** Concluso con vittoria. */
    WON,
    /** Concluso con sconfitta. */
    LOST
}
