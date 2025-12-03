package com.lostthesis.gui;

import com.lostthesis.engine.GameEngine;
import com.lostthesis.graphics.FullScreenRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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
    
    // Dimensioni schermo
    private int screenWidth;
    private int screenHeight;
    
    public FullScreenGUI() {
        // Ottieni dimensioni schermo
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        screenWidth = dm.getWidth();
        screenHeight = dm.getHeight();
        
        // Setup finestra fullscreen
        setTitle("LOST THESIS - L'Isola Misteriosa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        
        // Inizializza renderer
        renderer = new FullScreenRenderer(screenWidth, screenHeight);
        
        // Pannello di gioco
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        
        // Layout principale
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        
        // Pannello controlli
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        // Fullscreen
        gd.setFullScreenWindow(this);
        
        // Key bindings
        setupKeyBindings();
        
        // Chiedi nome giocatore e inizia
        SwingUtilities.invokeLater(this::askPlayerName);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 30, 40));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setPreferredSize(new Dimension(screenWidth, 70));
        
        // Stile pulsanti
        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        Color buttonBg = new Color(50, 70, 50);
        Color buttonFg = Color.WHITE;
        
        // Pulsanti scelta A, B, C
        btnA = createStyledButton("A", buttonFont, buttonBg, buttonFg);
        btnB = createStyledButton("B", buttonFont, buttonBg, buttonFg);
        btnC = createStyledButton("C", buttonFont, buttonBg, buttonFg);
        
        btnA.addActionListener(e -> processInput("A"));
        btnB.addActionListener(e -> processInput("B"));
        btnC.addActionListener(e -> processInput("C"));
        
        // Pulsante Avanti
        btnAdvance = createStyledButton("➡️ AVANTI", buttonFont, 
            new Color(70, 100, 70), buttonFg);
        btnAdvance.addActionListener(e -> processInput("avanti"));
        
        // Campo input
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
        
        // Pulsanti utilità
        btnInventory = createStyledButton("🎒", buttonFont, buttonBg, buttonFg);
        btnStatus = createStyledButton("❤️", buttonFont, buttonBg, buttonFg);
        JButton btnHelp = createStyledButton("❓", buttonFont, buttonBg, buttonFg);
        JButton btnExit = createStyledButton("🚪", buttonFont, new Color(100, 50, 50), buttonFg);
        
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
        
        // Aggiungi componenti
        panel.add(new JLabel(""));
        panel.add(btnA);
        panel.add(btnB);
        panel.add(btnC);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnAdvance);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(inputField);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnInventory);
        panel.add(btnStatus);
        panel.add(btnHelp);
        panel.add(btnExit);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Font font, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 100), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
        
        return button;
    }
    
    private void setupKeyBindings() {
        // ESC per uscire
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
        
        // Tasti rapidi A, B, C (quando non nel campo input)
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
        // Dialog personalizzato per il nome
        JDialog dialog = new JDialog(this, "LOST THESIS", true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(20, 30, 40));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(20, 30, 40));
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Titolo
        JLabel titleLabel = new JLabel("✈️ LOST THESIS ✈️");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 220, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Sottotitolo
        JLabel subLabel = new JLabel("L'Isola Misteriosa");
        subLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        subLabel.setForeground(new Color(150, 180, 150));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Messaggio
        JLabel msgLabel = new JLabel("Il volo Oceanic 815 è precipitato...");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel msg2Label = new JLabel("Come ti chiami, sopravvissuto?");
        msg2Label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msg2Label.setForeground(Color.WHITE);
        msg2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo nome
        JTextField nameField = new JTextField("Jack", 20);
        nameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameField.setMaximumSize(new Dimension(200, 35));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(40, 50, 60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 100), 2));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Pulsante
        JButton startBtn = createStyledButton("🏝️ INIZIA L'AVVENTURA", 
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
        
        // Bordo
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(100, 150, 100));
        wrapper.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        wrapper.add(content, BorderLayout.CENTER);
        
        dialog.add(wrapper);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Avvia il gioco con un nome giocatore specifico
     * @param playerName il nome del giocatore
     */
    public void startGame(String playerName) {
        initializeGame(playerName);
    }
    
    private void initializeGame(String playerName) {
        engine = new GameEngine();
        engine.initializeGame(playerName);
        
        // Mostra intro
        currentText = "✈️ Benvenuto sull'isola, " + playerName + "!\n\n" +
                      "Il volo Oceanic 815 è precipitato.\n" +
                      "Sei uno dei pochi sopravvissuti.\n\n" +
                      "L'isola nasconde segreti terrificanti...\n" +
                      "Ma anche la chiave per la tua fuga: LA TESI.\n\n" +
                      "Trova la TESI perduta per scappare con l'aereo!\n\n" +
                      "Premi AVANTI per iniziare...";
        currentTitle = "🏝️ L'ISOLA MISTERIOSA";
        currentLocation = "spiaggia";
        
        gamePanel.repaint();
    }
    
    private void processInput(String input) {
        if (engine == null) return;
        
        String response = engine.processCommand(input);
        currentText = response;
        currentLocation = engine.getCurrentRoomKey();
        
        // Aggiorna titolo se siamo in un capitolo
        if (response.contains("CAP.")) {
            int start = response.indexOf(": ");
            int end = response.indexOf("\n\n");
            if (start > 0 && end > start) {
                currentTitle = response.substring(start + 2, end);
            }
        }
        
        gamePanel.repaint();
        
        // Controlla fine gioco
        if (engine.isGameWon()) {
            Timer timer = new Timer(5000, e -> {
                JOptionPane.showMessageDialog(this,
                    "🎓 HAI COMPLETATO LOST THESIS!\n\n" +
                    "Sei fuggito dall'isola!\n" +
                    "La TESI ti ha salvato!\n\n" +
                    "Grazie per aver giocato!",
                    "VITTORIA!", JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    // Pannello di gioco personalizzato
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Status info
            String status = "";
            if (engine != null && engine.getPlayer() != null) {
                status = String.format("❤️ %d%%  |  🧠 %d%%  |  📅 Giorno %d  |  📍 %s",
                    engine.getPlayer().getHealth(),
                    engine.getPlayer().getSanity(),
                    engine.getPlayer().getDaysOnIsland(),
                    currentLocation.toUpperCase());
            }
            
            // Render
            renderer.render(g2d, currentLocation, currentText, currentTitle, status);
        }
    }
    
    public static void main(String[] args) {
        // Look and feel scuro
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
