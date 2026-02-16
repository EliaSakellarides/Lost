package com.lostthesis;

import com.lostthesis.gui.FullScreenGUI;
import javax.swing.*;

/**
 * Lost Thesis - Avventura Grafica sull'Isola
 * 
 * Gioco testuale ispirato a LOST (la serie TV)
 * Trova la TESI per fuggire dall'isola con l'aereo!
 * 
 * @author Elia Sakellarides
 * @version Alpha 0.1
 */
public class Main {
    public static void main(String[] args) {
        // Imposta look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usa quello di default
        }
        
        SwingUtilities.invokeLater(() -> {
            // Chiedi il nome al giocatore
            String playerName = JOptionPane.showInputDialog(null,
                "✈️ LOST THESIS - L'ISOLA MISTERIOSA ✈️\n\n" +
                "Il tuo aereo è precipitato su un'isola misteriosa.\n" +
                "Per fuggire, devi trovare la TESI perduta!\n\n" +
                "Come ti chiami, sopravvissuto?",
                "Lost Thesis",
                JOptionPane.QUESTION_MESSAGE);
            
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Jack";
            }
            
            // Avvia il gioco
            FullScreenGUI gui = new FullScreenGUI();
            gui.startGame(playerName);
        });
    }
}
