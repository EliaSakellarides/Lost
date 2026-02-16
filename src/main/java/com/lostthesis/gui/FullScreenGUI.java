package com.lostthesis.gui;

import com.lostthesis.engine.GameEngine;
import com.lostthesis.graphics.FullScreenRenderer;
import com.lostthesis.minigames.MiniGame;
import com.lostthesis.save.GameSave;
import com.lostthesis.save.GameSaveInstance;
import com.lostthesis.save.GameState;

import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GUI fullscreen per Lost Thesis
 * Ispirata alla serie TV LOST
 */
public class FullScreenGUI extends JFrame {
    private GameEngine engine;
    private FullScreenRenderer renderer;
    private GamePanel gamePanel;

    // Componenti UI
    private JTextField inputField;
    private JButton btnA, btnB, btnC;
    private JButton btnAdvance;
    private JButton btnInventory;
    private JButton btnStatus;

    // Stato display
    private String currentText = "";
    private String currentTitle = "";
    private String currentLocation = "spiaggia";
    private String currentImageKey = "spiaggia";

    // Etichette originali dei bottoni
    private static final String DEFAULT_BTN_A = "A";
    private static final String DEFAULT_BTN_B = "B";
    private static final String DEFAULT_BTN_C = "C";

    // Dimensioni schermo
    private int screenWidth;
    private int screenHeight;

    // Pannello stato permanente
    private StatusPanelFactory.StatusPanel statusPanel;
    private Timer statusUpdateTimer;

    public FullScreenGUI() {
        screenWidth = 1024;
        screenHeight = 768;

        setTitle("\u2708\uFE0F LOST THESIS - L'Isola Misteriosa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);

        renderer = new FullScreenRenderer(screenWidth, screenHeight - 70);

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight - 70));

        setLayout(new BorderLayout());

        statusPanel = StatusPanelFactory.createPermanentStatusPanel(screenWidth);
        add(statusPanel.getPanel(), BorderLayout.NORTH);

        add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);

        setupKeyBindings();

        statusUpdateTimer = new Timer(500, e -> statusPanel.update(engine, currentLocation));
        statusUpdateTimer.start();

        SwingUtilities.invokeLater(this::askPlayerName);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 30, 40));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setPreferredSize(new Dimension(screenWidth, 70));

        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        Color buttonBg = new Color(50, 70, 50);
        Color buttonFg = Color.WHITE;

        btnA = GuiButtonFactory.create("A", buttonFont, buttonBg, buttonFg);
        btnB = GuiButtonFactory.create("B", buttonFont, buttonBg, buttonFg);
        btnC = GuiButtonFactory.create("C", buttonFont, buttonBg, buttonFg);

        btnA.addActionListener(e -> processInput("A"));
        btnB.addActionListener(e -> processInput("B"));
        btnC.addActionListener(e -> processInput("C"));

        btnAdvance = GuiButtonFactory.create("\u27A1\uFE0F AVANTI", buttonFont,
            new Color(70, 100, 70), buttonFg);
        btnAdvance.addActionListener(e -> processInput("avanti"));

        inputField = new JTextField(25);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setBackground(new Color(40, 50, 60));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 100), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                processInput(text);
                inputField.setText("");
            }
        });

        btnInventory = GuiButtonFactory.create("\uD83C\uDF92", buttonFont, buttonBg, buttonFg);
        btnStatus = GuiButtonFactory.create("\u2764\uFE0F", buttonFont, buttonBg, buttonFg);
        JButton btnHelp = GuiButtonFactory.create("\u2753", buttonFont, buttonBg, buttonFg);
        JButton btnExit = GuiButtonFactory.create("\uD83D\uDEAA", buttonFont, new Color(100, 50, 50), buttonFg);

        JButton btnSave = GuiButtonFactory.create("\uD83D\uDCBE", buttonFont, buttonBg, buttonFg);
        btnSave.addActionListener(e -> showSaveDialog());

        btnInventory.addActionListener(e -> processInput("inventario"));
        btnStatus.addActionListener(e -> processInput("stato"));
        btnHelp.addActionListener(e -> processInput("aiuto"));
        btnExit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Vuoi davvero uscire dall'isola?", "Esci", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        panel.add(new JLabel(""));
        panel.add(btnA);
        panel.add(btnB);
        panel.add(btnC);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnAdvance);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(inputField);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnSave);
        panel.add(btnInventory);
        panel.add(btnStatus);
        panel.add(btnHelp);
        panel.add(btnExit);

        return panel;
    }

    public void setButtonLabels(String a, String b, String c) {
        if (btnA != null) btnA.setText(a);
        if (btnB != null) btnB.setText(b);
        if (btnC != null) btnC.setText(c);
    }

    public void resetButtonLabels() {
        setButtonLabels(DEFAULT_BTN_A, DEFAULT_BTN_B, DEFAULT_BTN_C);
    }

    private void updateButtonLabelsForMiniGame() {
        if (engine != null && engine.hasMiniGameActive()) {
            MiniGame mg = engine.getActiveMiniGame();
            setButtonLabels(mg.getButtonALabel(), mg.getButtonBLabel(), mg.getButtonCLabel());
        } else {
            resetButtonLabels();
        }
    }

    private void setupKeyBindings() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
        getRootPane().getActionMap().put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(FullScreenGUI.this,
                    "Vuoi davvero uscire?", "Esci", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED &&
                    !inputField.hasFocus()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_A:
                            processInput("A");
                            return true;
                        case KeyEvent.VK_B:
                            processInput("B");
                            return true;
                        case KeyEvent.VK_C:
                            processInput("C");
                            return true;
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_SPACE:
                            processInput("avanti");
                            return true;
                    }
                }
                return false;
            });
    }

    private void askPlayerName() {
        JDialog dialog = new JDialog(this, "LOST THESIS", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(20, 30, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(20, 30, 40));
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("\u2708\uFE0F LOST THESIS \u2708\uFE0F");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 220, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("L'Isola Misteriosa");
        subLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        subLabel.setForeground(new Color(150, 180, 150));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel("Il volo Oceanic 815 \u00E8 precipitato...");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg2Label = new JLabel("Come ti chiami, sopravvissuto?");
        msg2Label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msg2Label.setForeground(Color.WHITE);
        msg2Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField("Jack", 20);
        nameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameField.setMaximumSize(new Dimension(200, 35));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(40, 50, 60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 100), 2));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startBtn = GuiButtonFactory.create("\uD83C\uDFDD\uFE0F INIZIA L'AVVENTURA",
            new Font("SansSerif", Font.BOLD, 16),
            new Color(50, 100, 50), Color.WHITE);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        Runnable startGameAction = () -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Jack";
            dialog.dispose();
            initializeGame(name);
        };

        startBtn.addActionListener(e -> startGameAction.run());
        nameField.addActionListener(e -> startGameAction.run());

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(subLabel);
        content.add(Box.createVerticalStrut(30));
        content.add(msgLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(msg2Label);
        content.add(Box.createVerticalStrut(20));
        content.add(nameField);
        content.add(Box.createVerticalStrut(25));
        content.add(startBtn);

        if (GameSave.hasSaves()) {
            content.add(Box.createVerticalStrut(10));
            JButton loadBtn = GuiButtonFactory.create("\uD83D\uDCC2 CARICA PARTITA",
                new Font("SansSerif", Font.BOLD, 14),
                new Color(70, 70, 100), Color.WHITE);
            loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            loadBtn.addActionListener(e -> {
                dialog.dispose();
                showLoadDialog();
            });
            content.add(loadBtn);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(100, 150, 100));
        wrapper.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        wrapper.add(content, BorderLayout.CENTER);

        dialog.add(wrapper);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void startGame(String playerName) {
        initializeGame(playerName);
    }

    private void initializeGame(String playerName) {
        engine = new GameEngine();
        engine.initializeGame(playerName);

        IntroSequence intro = new IntroSequence(this, engine,
            screenWidth, screenHeight, () -> showGameIntro(playerName));
        intro.start(playerName);
    }

    private void showGameIntro(String playerName) {
        currentText = "\u2708\uFE0F Benvenuto sull'isola, " + playerName + "!\n\n" +
                      "Il volo Oceanic 815 \u00E8 precipitato.\n" +
                      "Sei uno dei pochi sopravvissuti.\n\n" +
                      "L'isola nasconde segreti terrificanti...\n" +
                      "Ma anche la chiave per la tua fuga: LA TESI.\n\n" +
                      "Trova la TESI perduta per scappare con l'aereo!\n\n" +
                      "Premi AVANTI per iniziare...";
        currentTitle = "\uD83C\uDFDD\uFE0F L'ISOLA MISTERIOSA";
        currentLocation = "spiaggia";

        gamePanel.repaint();
    }

    private void processInput(String input) {
        if (engine == null) return;

        String response = engine.processCommand(input);
        currentText = response;
        currentLocation = engine.getCurrentRoomKey();

        currentImageKey = engine.getCurrentChapterImageKey();

        if (response.contains("CAP.")) {
            int start = response.indexOf(": ");
            int end = response.indexOf("\n\n");
            if (start > 0 && end > start) {
                currentTitle = response.substring(start + 2, end);
            }
        }

        updateButtonLabelsForMiniGame();

        gamePanel.repaint();

        if (engine.isGameWon()) {
            Timer timer = new Timer(5000, e -> {
                JOptionPane.showMessageDialog(this,
                    "\uD83C\uDF93 HAI COMPLETATO LOST THESIS!\n\n" +
                    "Sei fuggito dall'isola!\n" +
                    "La TESI ti ha salvato!\n\n" +
                    "Grazie per aver giocato!",
                    "VITTORIA!", JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String status = "";
            if (engine != null && engine.getPlayer() != null) {
                status = String.format("\u2764\uFE0F %d%%  |  \uD83E\uDDE0 %d%%  |  \uD83D\uDCC5 Giorno %d  |  \uD83D\uDCCD %s",
                    engine.getPlayer().getHealth(),
                    engine.getPlayer().getSanity(),
                    engine.getPlayer().getDaysOnIsland(),
                    currentLocation.toUpperCase());
            }

            renderer.render(g2d, currentImageKey, currentText, currentTitle, status);
        }
    }

    private void showSaveDialog() {
        if (engine == null) return;

        String slotName = JOptionPane.showInputDialog(this,
            "Nome del salvataggio:",
            "\uD83D\uDCBE Salva Partita",
            JOptionPane.PLAIN_MESSAGE);

        if (slotName == null || slotName.trim().isEmpty()) return;
        slotName = slotName.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        boolean ok = GameSave.save(engine, slotName);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Partita salvata nello slot '" + slotName + "'!",
                "\uD83D\uDCBE Salvato!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Errore durante il salvataggio!",
                "\u274C Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoadDialog() {
        List<GameSaveInstance> saves = GameSave.listSaves();
        if (saves.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nessun salvataggio trovato.",
                "\uD83D\uDCC2 Carica Partita", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(this::askPlayerName);
            return;
        }

        String[] options = new String[saves.size()];
        for (int i = 0; i < saves.size(); i++) {
            options[i] = saves.get(i).getDisplayText();
        }

        String choice = (String) JOptionPane.showInputDialog(this,
            "Scegli un salvataggio da caricare:",
            "\uD83D\uDCC2 Carica Partita",
            JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        if (choice == null) {
            SwingUtilities.invokeLater(this::askPlayerName);
            return;
        }

        GameSaveInstance selected = null;
        for (GameSaveInstance s : saves) {
            if (s.getDisplayText().equals(choice)) {
                selected = s;
                break;
            }
        }

        if (selected == null) return;

        GameState state = GameSave.load(selected.getSlotName());
        if (state == null) {
            JOptionPane.showMessageDialog(this,
                "Errore nel caricamento!",
                "\u274C Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        engine = new GameEngine();
        engine.loadGameState(state);

        currentLocation = engine.getCurrentRoomKey();
        currentImageKey = engine.getCurrentChapterImageKey();
        currentText = "\u2705 Partita caricata!\n\n" +
            "\uD83D\uDC64 " + engine.getPlayer().getName() +
            " | Cap. " + engine.getCurrentChapterNumber() +
            "/" + engine.getTotalChapters() +
            " | \u2764\uFE0F " + engine.getPlayer().getHealth() +
            " | \uD83E\uDDE0 " + engine.getPlayer().getSanity() + "\n\n" +
            "Premi AVANTI per continuare...";
        currentTitle = "\uD83D\uDCC2 PARTITA CARICATA";

        gamePanel.repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new FullScreenGUI();
        });
    }
}
