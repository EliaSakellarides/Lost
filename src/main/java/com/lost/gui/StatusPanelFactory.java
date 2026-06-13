package com.lost.gui;

import com.lost.engine.GameEngine;
import com.lost.graphics.GameFonts;
import com.lost.model.Item;

import javax.swing.*;
import java.awt.*;

/**
 * Factory per pannelli di stato (giorno e posizione)
 */
public final class StatusPanelFactory {

    private StatusPanelFactory() {
    }

    /**
     * Pannello di stato permanente aggiornabile
     */
    public static class StatusPanel {
        private final JPanel panel;
        private final JLabel dayLabel;
        private final JLabel locationLabel;
        private final JPanel inventoryPanel;
        private String lastInventorySignature = "";

        StatusPanel(JPanel panel, JLabel dayLabel, JLabel locationLabel, JPanel inventoryPanel) {
            this.panel = panel;
            this.dayLabel = dayLabel;
            this.locationLabel = locationLabel;
            this.inventoryPanel = inventoryPanel;
        }

        /** {@return il pannello Swing da inserire nella finestra} */
        public JPanel getPanel() {
            return panel;
        }

        /**
         * Aggiorna giorno, posizione e inventario visualizzati.
         * L'inventario viene ridisegnato solo quando cambia, per evitare
         * sfarfallii a ogni tick del timer.
         * @param engine motore di gioco da cui leggere lo stato
         * @param currentLocation chiave della stanza corrente
         */
        public void update(GameEngine engine, String currentLocation) {
            if (engine == null || engine.getPlayer() == null) return;

            dayLabel.setText("Giorno " + engine.getPlayer().getDaysOnIsland());
            locationLabel.setText(currentLocation.toUpperCase());
            updateInventory(engine);
        }

        private void updateInventory(GameEngine engine) {
            java.util.List<Item> items = engine.getPlayer().getInventory();
            StringBuilder sig = new StringBuilder();
            for (Item it : items) {
                sig.append(it.getName()).append('|');
            }
            if (sig.toString().equals(lastInventorySignature)) {
                return; // inventario invariato: niente da ridisegnare
            }
            lastInventorySignature = sig.toString();

            inventoryPanel.removeAll();
            for (Item it : items) {
                ImageIcon icon = ItemIcons.iconFor(it);
                JLabel label;
                if (icon != null) {
                    label = new JLabel(icon);
                } else {
                    // Finche' l'icona non esiste, mostra il nome breve
                    label = new JLabel(it.getName());
                    label.setFont(GameFonts.retroPlain(16f));
                    label.setForeground(new Color(200, 220, 200));
                }
                label.setToolTipText(it.getName());
                inventoryPanel.add(label);
            }
            inventoryPanel.revalidate();
            inventoryPanel.repaint();
        }
    }

    /**
     * Crea la barra di stato permanente in cima alla finestra.
     * @param screenWidth larghezza dello schermo
     * @return il pannello di stato aggiornabile
     */
    public static StatusPanel createPermanentStatusPanel(int screenWidth) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setPreferredSize(new Dimension(screenWidth, 50));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));

        // === GIORNO ===
        JLabel dayLabel = new JLabel("Giorno 1");
        dayLabel.setFont(GameFonts.retroBold(22f));
        dayLabel.setForeground(new Color(255, 220, 100));
        panel.add(dayLabel);

        panel.add(Box.createHorizontalStrut(25));

        // === POSIZIONE ===
        JLabel locationLabel = new JLabel("SPIAGGIA");
        locationLabel.setFont(GameFonts.retroBold(22f));
        locationLabel.setForeground(new Color(150, 220, 150));
        panel.add(locationLabel);

        panel.add(Box.createHorizontalStrut(25));

        // === INVENTARIO (icone degli oggetti raccolti) ===
        JLabel bagLabel = new JLabel("Zaino:");
        bagLabel.setFont(GameFonts.retroPlain(18f));
        bagLabel.setForeground(new Color(150, 150, 150));
        panel.add(bagLabel);

        JPanel inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        inventoryPanel.setBackground(Color.BLACK);
        panel.add(inventoryPanel);

        return new StatusPanel(panel, dayLabel, locationLabel, inventoryPanel);
    }

    /**
     * Crea una barra di stato statica per i dialog delle scene.
     * @param engine motore di gioco da cui leggere lo stato
     * @param currentLocation chiave della stanza corrente
     * @param screenWidth larghezza dello schermo
     * @return pannello con lo stato del giocatore
     */
    public static JPanel createDialogStatusBar(GameEngine engine, String currentLocation, int screenWidth) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setPreferredSize(new Dimension(screenWidth, 44));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));

        int day = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getDaysOnIsland() : 1;

        // === GIORNO ===
        JLabel dayLabelDialog = new JLabel("Giorno " + day);
        dayLabelDialog.setFont(GameFonts.retroBold(18f));
        dayLabelDialog.setForeground(new Color(255, 220, 100));
        panel.add(dayLabelDialog);

        panel.add(Box.createHorizontalStrut(15));

        // === POSIZIONE ===
        JLabel locationLabelDialog = new JLabel(currentLocation.toUpperCase());
        locationLabelDialog.setFont(GameFonts.retroBold(18f));
        locationLabelDialog.setForeground(new Color(150, 220, 150));
        panel.add(locationLabelDialog);

        return panel;
    }
}
