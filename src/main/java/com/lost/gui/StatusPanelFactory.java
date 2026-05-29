package com.lost.gui;

import com.lost.engine.GameEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Factory per pannelli di stato (barra cuori, sanita, giorno, posizione)
 */
public class StatusPanelFactory {

    /**
     * Pannello di stato permanente aggiornabile
     */
    public static class StatusPanel {
        private final JPanel panel;
        private final JLabel[] heartLabels;
        private final JProgressBar sanityBar;
        private final JLabel dayLabel;
        private final JLabel locationLabel;

        StatusPanel(JPanel panel, JLabel[] heartLabels, JProgressBar sanityBar,
                    JLabel dayLabel, JLabel locationLabel) {
            this.panel = panel;
            this.heartLabels = heartLabels;
            this.sanityBar = sanityBar;
            this.dayLabel = dayLabel;
            this.locationLabel = locationLabel;
        }

        public JPanel getPanel() {
            return panel;
        }

        public void update(GameEngine engine, String currentLocation) {
            if (engine == null || engine.getPlayer() == null) return;

            int health = engine.getPlayer().getHealth();
            int sanity = engine.getPlayer().getSanity();
            int day = engine.getPlayer().getDaysOnIsland();

            // Aggiorna la barra salute testuale, senza icone.
            int fullSegments = health / 10;
            int halfSegment = (health % 10 >= 5) ? 1 : 0;

            for (int i = 0; i < 10; i++) {
                if (i < fullSegments) {
                    heartLabels[i].setText("#");
                    heartLabels[i].setForeground(new Color(255, 80, 80));
                } else if (i == fullSegments && halfSegment == 1) {
                    heartLabels[i].setText("+");
                    heartLabels[i].setForeground(new Color(255, 150, 150));
                } else {
                    heartLabels[i].setText("-");
                    heartLabels[i].setForeground(new Color(80, 80, 80));
                }
            }

            // Aggiorna barra sanita con colore dinamico
            sanityBar.setValue(sanity);
            sanityBar.setString(sanity + "%");
            if (sanity > 70) {
                sanityBar.setForeground(new Color(100, 150, 255));
            } else if (sanity > 40) {
                sanityBar.setForeground(new Color(255, 200, 100));
            } else {
                sanityBar.setForeground(new Color(255, 100, 100));
            }

            dayLabel.setText("Giorno " + day);

            String loc = currentLocation.toUpperCase();
            locationLabel.setText(loc);
        }
    }

    public static StatusPanel createPermanentStatusPanel(int screenWidth) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 20, 25));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setPreferredSize(new Dimension(screenWidth, 40));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));

        // === SALUTE ===
        JLabel heartTitle = new JLabel("Salute");
        heartTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        heartTitle.setForeground(new Color(255, 150, 150));
        panel.add(heartTitle);

        JLabel[] heartLabels = new JLabel[10];
        for (int i = 0; i < 10; i++) {
            heartLabels[i] = new JLabel("#");
            heartLabels[i].setFont(new Font("Monospaced", Font.BOLD, 16));
            heartLabels[i].setForeground(new Color(255, 80, 80));
            panel.add(heartLabels[i]);
        }

        panel.add(Box.createHorizontalStrut(15));

        // === BARRA SANITA ===
        JLabel brainLabel = new JLabel("Mente");
        brainLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        brainLabel.setForeground(new Color(150, 150, 255));
        panel.add(brainLabel);

        JProgressBar sanityBar = new JProgressBar(0, 100);
        sanityBar.setValue(100);
        sanityBar.setPreferredSize(new Dimension(100, 18));
        sanityBar.setStringPainted(true);
        sanityBar.setFont(new Font("SansSerif", Font.BOLD, 10));
        sanityBar.setForeground(new Color(100, 150, 255));
        sanityBar.setBackground(new Color(40, 40, 60));
        sanityBar.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120), 1));
        panel.add(sanityBar);

        panel.add(Box.createHorizontalStrut(20));

        // === GIORNO ===
        JLabel dayLabel = new JLabel("Giorno 1");
        dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dayLabel.setForeground(new Color(255, 220, 100));
        panel.add(dayLabel);

        panel.add(Box.createHorizontalStrut(20));

        // === POSIZIONE ===
        JLabel locationLabel = new JLabel("SPIAGGIA");
        locationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        locationLabel.setForeground(new Color(150, 220, 150));
        panel.add(locationLabel);

        return new StatusPanel(panel, heartLabels, sanityBar, dayLabel, locationLabel);
    }

    public static JPanel createDialogStatusBar(GameEngine engine, String currentLocation, int screenWidth) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 20, 25));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setPreferredSize(new Dimension(screenWidth, 35));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));

        int health = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getHealth() : 100;
        int sanity = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getSanity() : 100;
        int day = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getDaysOnIsland() : 1;

        // === SALUTE ===
        JLabel heartTitle = new JLabel("Salute");
        heartTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        heartTitle.setForeground(new Color(255, 150, 150));
        panel.add(heartTitle);

        int fullSegments = health / 10;
        for (int i = 0; i < 10; i++) {
            JLabel heart = new JLabel(i < fullSegments ? "#" : "-");
            heart.setFont(new Font("Monospaced", Font.BOLD, 14));
            heart.setForeground(i < fullSegments ? new Color(255, 80, 80) : new Color(80, 80, 80));
            panel.add(heart);
        }

        panel.add(Box.createHorizontalStrut(10));

        // === BARRA SANITA ===
        JLabel brainLabel = new JLabel("Mente");
        brainLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        brainLabel.setForeground(new Color(150, 150, 255));
        panel.add(brainLabel);

        JProgressBar sanityBarDialog = new JProgressBar(0, 100);
        sanityBarDialog.setValue(sanity);
        sanityBarDialog.setPreferredSize(new Dimension(80, 15));
        sanityBarDialog.setStringPainted(true);
        sanityBarDialog.setFont(new Font("SansSerif", Font.BOLD, 9));
        sanityBarDialog.setString(sanity + "%");
        sanityBarDialog.setForeground(sanity > 70 ? new Color(100, 150, 255) :
                                       sanity > 40 ? new Color(255, 200, 100) : new Color(255, 100, 100));
        sanityBarDialog.setBackground(new Color(40, 40, 60));
        sanityBarDialog.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120), 1));
        panel.add(sanityBarDialog);

        panel.add(Box.createHorizontalStrut(15));

        // === GIORNO ===
        JLabel dayLabelDialog = new JLabel("Giorno " + day);
        dayLabelDialog.setFont(new Font("SansSerif", Font.BOLD, 12));
        dayLabelDialog.setForeground(new Color(255, 220, 100));
        panel.add(dayLabelDialog);

        panel.add(Box.createHorizontalStrut(15));

        // === POSIZIONE ===
        JLabel locationLabelDialog = new JLabel(currentLocation.toUpperCase());
        locationLabelDialog.setFont(new Font("SansSerif", Font.BOLD, 12));
        locationLabelDialog.setForeground(new Color(150, 220, 150));
        panel.add(locationLabelDialog);

        return panel;
    }
}
