package com.lost.engine;

/**
 * Tipi di comando riconosciuti dal parser.
 * Ogni tipo corrisponde a un'azione di gioco.
 */
public enum CommandType {
    /** Prosegue nella storia al capitolo/paragrafo successivo. */
    AVANTI,
    /** Si muove in una direzione (nord/sud/est/ovest). */
    VAI,
    /** Risponde a una domanda posta dal capitolo corrente. */
    RISPONDI,
    /** Sceglie una delle opzioni proposte (A/B/C). */
    SCEGLI,
    /** Raccoglie un oggetto dalla stanza corrente. */
    PRENDI,
    /** Lascia un oggetto dell'inventario nella stanza. */
    LASCIA,
    /** Esamina un oggetto o l'ambiente circostante. */
    GUARDA,
    /** Consuma cibo o bevande dall'inventario. */
    MANGIA,
    /** Attiva o accende un oggetto (es. dinamite, torcia). */
    ATTIVA,
    /** Usa un oggetto generico dell'inventario. */
    USA,
    /** Mostra il contenuto dell'inventario. */
    INVENTARIO,
    /** Mostra giorno, posizione e inventario del giocatore. */
    STATO,
    /** Mostra l'elenco dei comandi disponibili. */
    AIUTO,
    /** Salva la partita in uno slot. */
    SALVA,
    /** Carica una partita salvata. */
    CARICA_PARTITA,
    /** Mostra la mappa dell'isola. */
    MAPPA,
    /** Comando non riconosciuto dal parser. */
    SCONOSCIUTO
}
