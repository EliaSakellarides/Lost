package com.lost;

import com.lost.gui.FullScreenGUI;
import com.lost.records.RecordApiServer;
import javax.swing.*;

/**
 * Lost - Avventura Grafica sull'Isola
 * 
 * Gioco testuale ispirato a LOST (la serie TV)
 * Trova la mappa della pista nascosta per fuggire dall'isola!
 * 
 * @author Elia Sakellarides
 * @version 1.0
 */
public final class Main {

    private Main() {
    }

    /**
     * Punto di ingresso del gioco: avvia il server dei record,
     * chiede il nome al giocatore e apre la GUI.
     * @param args argomenti da riga di comando (non usati)
     */
    public static void main(String[] args) {
        RecordApiServer.start();

        // Imposta look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usa quello di default
        }
        
        // La GUI mostra il menu iniziale (nuova partita, carica, record)
        SwingUtilities.invokeLater(FullScreenGUI::new);
    }
}
