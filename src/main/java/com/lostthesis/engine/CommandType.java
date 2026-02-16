package com.lostthesis.engine;

/**
 * Tipi di comando riconosciuti dal parser.
 * Ogni tipo corrisponde a un'azione di gioco.
 */
public enum CommandType {
    AVANTI,
    RISPONDI,
    SCEGLI,
    PRENDI,
    LASCIA,
    GUARDA,
    MANGIA,
    ATTIVA,
    USA,
    INVENTARIO,
    STATO,
    AIUTO,
    SALVA,
    CARICA_PARTITA,
    MAPPA,
    SCONOSCIUTO
}
