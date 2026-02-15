package com.lostthesis.gui;

import com.lostthesis.engine.GameEngine;
import com.lostthesis.graphics.FullScreenRenderer;
import com.lostthesis.minigames.MiniGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

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
    private String currentImageKey = "spiaggia"; // Chiave immagine da mostrare (capitolo o location)
    
    // Etichette originali dei bottoni
    private static final String DEFAULT_BTN_A = "A";
    private static final String DEFAULT_BTN_B = "B";
    private static final String DEFAULT_BTN_C = "C";

    // Dimensioni schermo
    private int screenWidth;
    private int screenHeight;
    
    // Pannello stato permanente
    private JPanel permanentStatusPanel;
    private JLabel[] heartLabels;
    private JProgressBar sanityBar;
    private JLabel dayLabel;
    private JLabel locationLabel;
    private Timer statusUpdateTimer;
    
    public FullScreenGUI() {
        // Dimensioni finestra (non fullscreen)
        screenWidth = 1024;
        screenHeight = 768;
        
        // Setup finestra normale
        setTitle("✈️ LOST THESIS - L'Isola Misteriosa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null); // Centra la finestra
        
        // Inizializza renderer
        renderer = new FullScreenRenderer(screenWidth, screenHeight - 70);
        
        // Pannello di gioco
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight - 70));
        
        // Layout principale
        setLayout(new BorderLayout());
        
        // === PANNELLO STATO PERMANENTE IN ALTO ===
        permanentStatusPanel = createPermanentStatusPanel();
        add(permanentStatusPanel, BorderLayout.NORTH);
        
        add(gamePanel, BorderLayout.CENTER);
        
        // Pannello controlli
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        // Mostra finestra
        setVisible(true);
        
        // Key bindings
        setupKeyBindings();
        
        // Timer per aggiornare lo stato continuamente
        statusUpdateTimer = new Timer(500, e -> updatePermanentStatus());
        statusUpdateTimer.start();
        
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
    
    /**
     * Crea il pannello di stato permanente con cuoricini, sanità, giorno e posizione
     */
    private JPanel createPermanentStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 20, 25));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setPreferredSize(new Dimension(screenWidth, 40));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));
        
        // === CUORICINI ===
        JLabel heartTitle = new JLabel("❤️");
        heartTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(heartTitle);
        
        heartLabels = new JLabel[10];
        for (int i = 0; i < 10; i++) {
            heartLabels[i] = new JLabel("♥");
            heartLabels[i].setFont(new Font("SansSerif", Font.BOLD, 16));
            heartLabels[i].setForeground(new Color(255, 80, 80));
            panel.add(heartLabels[i]);
        }
        
        panel.add(Box.createHorizontalStrut(15));
        
        // === BARRA SANITÀ ===
        JLabel brainLabel = new JLabel("🧠");
        brainLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(brainLabel);
        
        sanityBar = new JProgressBar(0, 100);
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
        dayLabel = new JLabel("📅 Giorno 1");
        dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dayLabel.setForeground(new Color(255, 220, 100));
        panel.add(dayLabel);
        
        panel.add(Box.createHorizontalStrut(20));
        
        // === POSIZIONE ===
        locationLabel = new JLabel("📍 SPIAGGIA");
        locationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        locationLabel.setForeground(new Color(150, 220, 150));
        panel.add(locationLabel);
        
        return panel;
    }
    
    /**
     * Aggiorna il pannello di stato permanente con i valori attuali
     */
    private void updatePermanentStatus() {
        if (engine == null || engine.getPlayer() == null) return;
        
        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        int day = engine.getPlayer().getDaysOnIsland();
        
        // Aggiorna cuoricini
        int fullHearts = health / 10;
        int halfHeart = (health % 10 >= 5) ? 1 : 0;
        
        for (int i = 0; i < 10; i++) {
            if (i < fullHearts) {
                heartLabels[i].setText("♥");
                heartLabels[i].setForeground(new Color(255, 80, 80)); // Rosso pieno
            } else if (i == fullHearts && halfHeart == 1) {
                heartLabels[i].setText("♥");
                heartLabels[i].setForeground(new Color(255, 150, 150)); // Rosa (mezzo)
            } else {
                heartLabels[i].setText("♡");
                heartLabels[i].setForeground(new Color(80, 80, 80)); // Grigio vuoto
            }
        }
        
        // Aggiorna barra sanità con colore dinamico
        sanityBar.setValue(sanity);
        sanityBar.setString(sanity + "%");
        if (sanity > 70) {
            sanityBar.setForeground(new Color(100, 150, 255)); // Blu
        } else if (sanity > 40) {
            sanityBar.setForeground(new Color(255, 200, 100)); // Giallo
        } else {
            sanityBar.setForeground(new Color(255, 100, 100)); // Rosso
        }
        
        // Aggiorna giorno
        dayLabel.setText("📅 Giorno " + day);
        
        // Aggiorna posizione
        String loc = currentLocation.toUpperCase();
        locationLabel.setText("📍 " + loc);
    }
    
    /**
     * Crea una barra di stato per le scene dei dialog (intro, spiaggia, etc.)
     * Mostra lo stato attuale del giocatore (salute, sanità, giorno, posizione)
     */
    private JPanel createDialogStatusBar() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 20, 25));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setPreferredSize(new Dimension(screenWidth, 35));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 150, 100)));
        
        int health = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getHealth() : 100;
        int sanity = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getSanity() : 100;
        int day = (engine != null && engine.getPlayer() != null) ? engine.getPlayer().getDaysOnIsland() : 1;
        
        // === CUORICINI ===
        JLabel heartTitle = new JLabel("❤️");
        heartTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(heartTitle);
        
        int fullHearts = health / 10;
        for (int i = 0; i < 10; i++) {
            JLabel heart = new JLabel(i < fullHearts ? "♥" : "♡");
            heart.setFont(new Font("SansSerif", Font.BOLD, 14));
            heart.setForeground(i < fullHearts ? new Color(255, 80, 80) : new Color(80, 80, 80));
            panel.add(heart);
        }
        
        panel.add(Box.createHorizontalStrut(10));
        
        // === BARRA SANITÀ ===
        JLabel brainLabel = new JLabel("🧠");
        brainLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
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
        JLabel dayLabelDialog = new JLabel("📅 Giorno " + day);
        dayLabelDialog.setFont(new Font("SansSerif", Font.BOLD, 12));
        dayLabelDialog.setForeground(new Color(255, 220, 100));
        panel.add(dayLabelDialog);
        
        panel.add(Box.createHorizontalStrut(15));
        
        // === POSIZIONE ===
        JLabel locationLabelDialog = new JLabel("📍 " + currentLocation.toUpperCase());
        locationLabelDialog.setFont(new Font("SansSerif", Font.BOLD, 12));
        locationLabelDialog.setForeground(new Color(150, 220, 150));
        panel.add(locationLabelDialog);
        
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
    
    /**
     * Cambia le etichette dei pulsanti A/B/C (usato dai mini giochi)
     */
    public void setButtonLabels(String a, String b, String c) {
        if (btnA != null) btnA.setText(a);
        if (btnB != null) btnB.setText(b);
        if (btnC != null) btnC.setText(c);
    }

    /**
     * Ripristina le etichette originali dei pulsanti A/B/C
     */
    public void resetButtonLabels() {
        setButtonLabels(DEFAULT_BTN_A, DEFAULT_BTN_B, DEFAULT_BTN_C);
    }

    /**
     * Aggiorna le etichette dei bottoni in base al mini gioco attivo
     */
    private void updateButtonLabelsForMiniGame() {
        if (engine != null && engine.hasMiniGameActive()) {
            MiniGame mg = engine.getActiveMiniGame();
            setButtonLabels(mg.getButtonALabel(), mg.getButtonBLabel(), mg.getButtonCLabel());
        } else {
            resetButtonLabels();
        }
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
        
        // 🎬 INTRO CINEMATICA STILE LOST!
        showLostIntro(playerName);
    }
    
    /**
     * Mostra l'intro cinematica con la scritta "LOST" come nella serie TV
     * Con rotazione e sincronizzazione musicale!
     */
    private void showLostIntro(String playerName) {
        // Finestra intro a schermo pieno nero
        JWindow introWindow = new JWindow();
        introWindow.setSize(screenWidth, screenHeight);
        introWindow.setLocationRelativeTo(this);
        
        // Variabili per animazione
        final float[] alpha = {0f};
        final double[] rotation = {0.0};
        final double[] scale = {0.3};
        
        JPanel introPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                
                // Sfondo nero
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Salva trasformazione originale
                java.awt.geom.AffineTransform originalTransform = g2d.getTransform();
                
                // Centro dello schermo
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Applica trasformazioni: rotazione + scala
                g2d.translate(centerX, centerY);
                g2d.rotate(rotation[0]);
                g2d.scale(scale[0], scale[0]);
                g2d.translate(-centerX, -centerY);
                
                // Scritta "LOST" con effetto fade
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                
                // Font grande stile LOST (serif, bold)
                Font lostFont = new Font("Times New Roman", Font.BOLD, 180);
                g2d.setFont(lostFont);
                g2d.setColor(Color.WHITE);
                
                String text = "LOST";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 30;
                
                g2d.drawString(text, x, y);
                
                // Ripristina trasformazione per sottotitolo (non ruota)
                g2d.setTransform(originalTransform);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                
                // Sottotitolo "THESIS" più piccolo (non ruota, solo fade)
                Font thesisFont = new Font("Times New Roman", Font.ITALIC, 40);
                g2d.setFont(thesisFont);
                g2d.setColor(new Color(200, 200, 200));
                
                String subtitle = "T H E S I S";
                FontMetrics fm2 = g2d.getFontMetrics();
                int x2 = (getWidth() - fm2.stringWidth(subtitle)) / 2;
                int y2 = centerY + 120;
                g2d.drawString(subtitle, x2, y2);
            }
        };
        
        introPanel.setBackground(Color.BLACK);
        introWindow.add(introPanel);
        introWindow.setVisible(true);
        
        // Animazione: fade in + rotazione LENTA come nella serie TV
        Timer animTimer = new Timer(30, null);
        final int[] frame = {0};
        final int FADE_IN_FRAMES = 60;       // ~1.8 sec fade in
        final int ROTATE_FRAMES = 200;       // ~6 sec rotazione lenta
        final int TOTAL_FRAMES = FADE_IN_FRAMES + ROTATE_FRAMES;
        
        animTimer.addActionListener(e -> {
            frame[0]++;
            
            if (frame[0] <= FADE_IN_FRAMES) {
                // Fase 1: Fade in + scala crescente + inizio rotazione lenta
                alpha[0] = (float) frame[0] / FADE_IN_FRAMES;
                scale[0] = 0.5 + (0.5 * frame[0] / FADE_IN_FRAMES); // da 0.5 a 1.0
                // Rotazione molto lenta - solo 90° durante il fade in
                rotation[0] = Math.toRadians(90.0 * frame[0] / FADE_IN_FRAMES);
            } else if (frame[0] <= TOTAL_FRAMES) {
                // Fase 2: Continua rotazione lenta fino a 360° totali
                alpha[0] = 1.0f;
                scale[0] = 1.0;
                int rotateFrame = frame[0] - FADE_IN_FRAMES;
                // Da 90° a 360° molto lentamente
                rotation[0] = Math.toRadians(90.0 + (270.0 * rotateFrame / ROTATE_FRAMES));
            } else {
                // Fase 3: La musica sta per partire - FADE OUT
                alpha[0] -= 0.05f;
                scale[0] += 0.02; // leggero zoom out
                
                if (alpha[0] <= 0f) {
                    animTimer.stop();
                    introWindow.dispose();
                    
                    // 🎬 Mostra sequenza immagini intro, POI il gioco!
                    showIntroImages(playerName);
                }
            }
            
            introPanel.repaint();
        });
        
        animTimer.start();
    }
    
    /**
     * Mostra la sequenza di immagini intro (aereo, ecc.)
     */
    private void showIntroImages(String playerName) {
        JWindow imageWindow = new JWindow();
        imageWindow.setSize(screenWidth, screenHeight);
        imageWindow.setLocationRelativeTo(this);
        
        // Carica l'immagine dell'aereo dal classpath
        final Image[] airplaneImage = {loadImageFromClasspath("areo scena iniziale.jpg")};
        
        final float[] alpha = {0f};
        
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Sfondo nero
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Disegna immagine con fade
                if (airplaneImage[0] != null) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                    
                    // Scala immagine per riempire lo schermo mantenendo proporzioni
                    int imgW = airplaneImage[0].getWidth(null);
                    int imgH = airplaneImage[0].getHeight(null);
                    double scaleX = (double) getWidth() / imgW;
                    double scaleY = (double) getHeight() / imgH;
                    double scale = Math.max(scaleX, scaleY);
                    
                    int newW = (int) (imgW * scale);
                    int newH = (int) (imgH * scale);
                    int x = (getWidth() - newW) / 2;
                    int y = (getHeight() - newH) / 2;
                    
                    g2d.drawImage(airplaneImage[0], x, y, newW, newH, null);
                }
            }
        };
        
        imagePanel.setBackground(Color.BLACK);
        imageWindow.add(imagePanel);
        imageWindow.setVisible(true);
        
        // Animazione: fade in, hold, fade out
        Timer imageTimer = new Timer(30, null);
        final int[] frame = {0};
        final int FADE_IN = 40;    // ~1.2 sec
        final int HOLD = 100;      // ~3 sec
        final int FADE_OUT = 40;   // ~1.2 sec
        
        imageTimer.addActionListener(e -> {
            frame[0]++;
            
            if (frame[0] <= FADE_IN) {
                alpha[0] = (float) frame[0] / FADE_IN;
            } else if (frame[0] <= FADE_IN + HOLD) {
                alpha[0] = 1.0f;
            } else if (frame[0] <= FADE_IN + HOLD + FADE_OUT) {
                alpha[0] = 1.0f - (float)(frame[0] - FADE_IN - HOLD) / FADE_OUT;
            } else {
                imageTimer.stop();
                imageWindow.dispose();
                
                // 🛫 Scena hostess!
                showHostessScene(playerName);
            }
            
            imagePanel.repaint();
        });
        
        imageTimer.start();
    }
    
    /**
     * Scena interattiva: l'hostess offre da bere
     * Layout: immagine grande sopra, testo e scelte sotto
     */
    private void showHostessScene(String playerName) {
        JDialog hostessDialog = new JDialog(this, false);
        hostessDialog.setUndecorated(true);
        hostessDialog.setSize(screenWidth, screenHeight);
        hostessDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 40, 50));
        
        // === PARTE SUPERIORE: IMMAGINE (riempie tutto lo spazio) ===
        JPanel imagePanel = new JPanel() {
            private Image hostessImage;
            {
                hostessImage = loadImageFromClasspath("hostess.jpg");
                if (hostessImage == null) {
                    hostessImage = loadImageFromClasspath("hostess.png");
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (hostessImage != null) {
                    // Riempi TUTTO lo spazio disponibile
                    g2d.drawImage(hostessImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // Placeholder se manca immagine
                    g2d.setColor(new Color(50, 60, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.ITALIC, 16));
                    g2d.drawString("[ Immagine: hostess.png ]", getWidth()/2 - 80, getHeight()/2);
                }
            }
        };
        // Immagine prende 60% dello schermo
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(new Color(30, 40, 50));
        
        // === PARTE CENTRALE: TITOLO ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(40, 60, 40));
        JLabel titleLabel = new JLabel("✈️ VOL OCEANIC 815 - IN VOLO");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 220, 100));
        titlePanel.add(titleLabel);
        
        // === PARTE INFERIORE: TESTO + BOTTONI ===
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(30, 40, 50));
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(new Color(30, 40, 50));
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sceneText.setText(
            "Un'hostess si avvicina con un carrello delle bevande.\n\n" +
            "👩‍✈️ \"Posso offrirle qualcosa da bere? Vodka o whisky?\""
        );
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(new Color(30, 40, 50));
        
        JButton vodkaBtn = createStyledButton("🍸 Vodka", 
            new Font("SansSerif", Font.BOLD, 16), new Color(60, 80, 120), Color.WHITE);
        JButton whiskyBtn = createStyledButton("🥃 Whisky", 
            new Font("SansSerif", Font.BOLD, 16), new Color(120, 80, 40), Color.WHITE);
        JButton noBtn = createStyledButton("❌ No grazie", 
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 80, 80), Color.WHITE);
        
        // Azione: prendi vodka
        vodkaBtn.addActionListener(e -> {
            hostessDialog.dispose();
            com.lostthesis.model.Item vodka = new com.lostthesis.model.Item(
                "Bottiglietta di Vodka",
                "Una piccola bottiglietta di vodka dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
                true
            );
            engine.getPlayer().addItem(vodka);
            engine.getPlayer().removeHealth(5); // Bere abbassa un po' la vita
            showItemObtainedScene(playerName, "Bottiglietta di Vodka", "vodka");
        });
        
        // Azione: prendi whisky
        whiskyBtn.addActionListener(e -> {
            hostessDialog.dispose();
            com.lostthesis.model.Item whisky = new com.lostthesis.model.Item(
                "Bottiglietta di Whisky",
                "Una piccola bottiglietta di whisky dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
                true
            );
            engine.getPlayer().addItem(whisky);
            engine.getPlayer().removeHealth(5); // Bere abbassa un po' la vita
            showItemObtainedScene(playerName, "Bottiglietta di Whisky", "whisky");
        });
        
        // Azione: rifiuta
        noBtn.addActionListener(e -> {
            hostessDialog.dispose();
            showTurbulenceScene(playerName);
        });
        
        buttonPanel.add(vodkaBtn);
        buttonPanel.add(whiskyBtn);
        buttonPanel.add(noBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Assembla il layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        hostessDialog.add(mainPanel);
        hostessDialog.setVisible(true);
    }
    
    /**
     * Mostra "Hai ottenuto un nuovo oggetto!" con vita che si abbassa
     */
    private void showItemObtainedScene(String playerName, String itemName, String drinkType) {
        JDialog itemDialog = new JDialog(this, false);
        itemDialog.setUndecorated(true);
        itemDialog.setSize(500, 300);
        itemDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 50, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 100), 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Icona e testo
        JLabel iconLabel = new JLabel("🎒", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        
        JLabel titleLabel = new JLabel("NUOVO OGGETTO!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(100, 255, 100));
        
        JLabel itemLabel = new JLabel(itemName, SwingConstants.CENTER);
        itemLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        itemLabel.setForeground(Color.WHITE);
        
        // Mostra vita attuale
        int health = engine.getPlayer().getHealth();
        JLabel healthLabel = new JLabel("❤️ Vita: " + health + "/100 (-5)", SwingConstants.CENTER);
        healthLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        healthLabel.setForeground(new Color(255, 150, 150));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 50, 30));
        centerPanel.add(Box.createVerticalGlue());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(itemLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(healthLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        itemDialog.add(panel);
        itemDialog.setVisible(true);
        
        // Chiudi dopo 2 secondi e mostra turbolenza
        Timer closeTimer = new Timer(2000, e -> {
            itemDialog.dispose();
            showTurbulenceScene(playerName);
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    /**
     * Scena della turbolenza - l'aereo inizia a tremare!
     * Con immagine aereotrema.jpg
     */
    private void showTurbulenceScene(String playerName) {
        JDialog turbulenceDialog = new JDialog(this, false);
        turbulenceDialog.setUndecorated(true);
        turbulenceDialog.setSize(screenWidth, screenHeight);
        turbulenceDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        // === IMMAGINE AEREO CHE TREMA ===
        JPanel imagePanel = new JPanel() {
            private Image turbulenceImage;
            {
                turbulenceImage = loadImageFromClasspath("aereotrema.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (turbulenceImage != null) {
                    // Riempi tutto lo spazio
                    g2d.drawImage(turbulenceImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(Color.BLACK);
        
        // === TITOLO ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(80, 30, 30));
        JLabel titleLabel = new JLabel("⚡ TURBOLENZA! ⚡");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 100, 100));
        titlePanel.add(titleLabel);
        
        // === TESTO E BOTTONE ===
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "L'aereo inizia a tremare violentemente!\n" +
            "Le maschere d'ossigeno cadono dal soffitto.\n" +
            "Urla di panico... poi tutto diventa nero..."
        );
        
        JButton continueBtn = createStyledButton("💥 Continua...", 
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 50, 50), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            turbulenceDialog.dispose();
            showPlaneBreakingScene(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        // Assembla layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        turbulenceDialog.add(mainPanel);
        turbulenceDialog.setVisible(true);
    }
    
    /**
     * L'aereo si rompe in volo!
     */
    private void showPlaneBreakingScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        JPanel imagePanel = new JPanel() {
            private Image planeImage;
            {
                planeImage = loadImageFromClasspath("aereo si rompe in volo.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (planeImage != null) {
                    g2d.drawImage(planeImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(100, 30, 30));
        JLabel titleLabel = new JLabel("💥 L'AEREO SI SPEZZA! 💥");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 80, 80));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Un boato assordante! La fusoliera si spezza in due!\n" +
            "Passeggeri e detriti volano nel vuoto...\n" +
            "Stai precipitando verso un'isola sconosciuta..."
        );
        
        JButton continueBtn = createStyledButton("⬇️ Continua...", 
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 50, 50), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showEyeOpeningScene(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * L'iconico occhio che si apre - momento cult di LOST!
     */
    private void showEyeOpeningScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        JPanel imagePanel = new JPanel() {
            private Image eyeImage;
            {
                eyeImage = loadImageFromClasspath("occhio aperto -2.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (eyeImage != null) {
                    g2d.drawImage(eyeImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(50, 50, 80));
        JLabel titleLabel = new JLabel("👁️ ... ");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(200, 200, 255));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "...\n\n" +
            "Apri gli occhi.\n\n" +
            "Foglie di bambù oscillano sopra di te..."
        );
        
        JButton continueBtn = createStyledButton("👀 Continua...", 
            new Font("SansSerif", Font.BOLD, 16), new Color(60, 60, 90), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showJungleAwakeningScene(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Risveglio nella giungla
     */
    private void showJungleAwakeningScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        JPanel imagePanel = new JPanel() {
            private Image jungleImage;
            {
                jungleImage = loadImageFromClasspath("risveglio in giungla.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (jungleImage != null) {
                    g2d.drawImage(jungleImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(20, 40, 20));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(30, 60, 30));
        JLabel titleLabel = new JLabel("🌴 RISVEGLIO");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(150, 220, 150));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Ti alzi a fatica. Sei nella giungla.\n" +
            "Alberi di bambù ti circondano. Sei confuso, disorientato.\n" +
            "In lontananza senti urla e il rumore di un motore..."
        );
        
        JButton continueBtn = createStyledButton("🌿 Continua...", 
            new Font("SansSerif", Font.BOLD, 16), new Color(50, 80, 50), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showFollowVincentScene(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Vincent il cane appare!
     */
    private void showFollowVincentScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        JPanel imagePanel = new JPanel() {
            private Image vincentImage;
            {
                vincentImage = loadImageFromClasspath("segui vincent.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (vincentImage != null) {
                    g2d.drawImage(vincentImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(40, 50, 40));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(60, 50, 30));
        JLabel titleLabel = new JLabel("🐕 VINCENT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 200, 150));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Un cane Labrador bianco sbuca dalla vegetazione!\n" +
            "Ti guarda, abbaia, e corre via verso la spiaggia.\n" +
            "Decidi di seguirlo..."
        );
        
        JButton continueBtn = createStyledButton("🏃 Segui Vincent!", 
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 70, 40), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showBeachCrashScene(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Scena finale intro: il relitto sulla spiaggia
     */
    private void showBeachCrashScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        // === BARRA DI STATO IN ALTO ===
        JPanel statusBar = createDialogStatusBar();
        mainPanel.add(statusBar, BorderLayout.NORTH);
        
        JPanel imagePanel = new JPanel() {
            private Image crashImage;
            {
                crashImage = loadImageFromClasspath("aereo distrutto su spiaggia.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (crashImage != null) {
                    g2d.drawImage(crashImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(60, 50, 40));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(80, 60, 40));
        JLabel titleLabel = new JLabel("🏝️ LA SPIAGGIA");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 220, 150));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Emergi dalla giungla e resti senza fiato.\n\n" +
            "La spiaggia è un caos di detriti fumanti, bagagli sparsi,\n" +
            "e sopravvissuti in stato di shock.\n\n" +
            "Il volo Oceanic 815 è precipitato su un'isola sconosciuta.\n" +
            "Benvenuto nel tuo incubo, " + playerName + "."
        );
        
        // Scelta: cosa fare sulla spiaggia?
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.BLACK);
        
        JButton helpBtn = createStyledButton("🩹 Aiuta i sopravvissuti", 
            new Font("SansSerif", Font.BOLD, 14), new Color(60, 100, 60), Color.WHITE);
        JButton healBtn = createStyledButton("🏥 Cura i feriti", 
            new Font("SansSerif", Font.BOLD, 14), new Color(100, 60, 60), Color.WHITE);
        
        // Se ha l'alcol, può usarlo per curarsi
        boolean hasAlcohol = engine.getPlayer().hasItem("Bottiglietta di Vodka") || 
                            engine.getPlayer().hasItem("Bottiglietta di Whisky");
        
        JButton alcoholBtn = createStyledButton("🥃 Usa l'alcol per curarti", 
            new Font("SansSerif", Font.BOLD, 14), new Color(120, 80, 40), Color.WHITE);
        
        helpBtn.addActionListener(e -> {
            dialog.dispose();
            showHelpSurvivorsScene(playerName);
        });
        
        healBtn.addActionListener(e -> {
            dialog.dispose();
            showHealWoundedScene(playerName);
        });
        
        alcoholBtn.addActionListener(e -> {
            dialog.dispose();
            showUseAlcoholScene(playerName);
        });
        
        buttonPanel.add(helpBtn);
        buttonPanel.add(healBtn);
        buttonPanel.add(alcoholBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Scena: Aiuta i sopravvissuti
     */
    private void showHelpSurvivorsScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        // === BARRA DI STATO IN ALTO ===
        JPanel statusBar = createDialogStatusBar();
        mainPanel.add(statusBar, BorderLayout.NORTH);
        
        JPanel imagePanel = new JPanel() {
            private Image helpImage;
            {
                helpImage = loadImageFromClasspath("aiuto spravvissuti.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (helpImage != null) {
                    g2d.drawImage(helpImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(40, 50, 60));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(50, 80, 50));
        JLabel titleLabel = new JLabel("🩹 AIUTO AI SOPRAVVISSUTI");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(200, 255, 200));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Corri tra i detriti fumanti per aiutare gli altri sopravvissuti.\n\n" +
            "Trascini persone lontano dai rottami in fiamme,\n" +
            "rassicuri chi è in stato di shock, cerchi superstiti tra le valigie.\n\n" +
            "Il tuo coraggio ispira gli altri. Hai guadagnato rispetto.\n" +
            "Ma lo sforzo ti ha stancato..."
        );
        
        // Effetto: +10 sanità, -10 vita (sforzo fisico)
        engine.getPlayer().addSanity(10);
        engine.getPlayer().removeHealth(10);
        
        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("❤️ " + health + "/100 (-10)  |  🧠 " + sanity + "/100 (+10)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 200, 100));
        
        JButton continueBtn = createStyledButton("➡️ Continua", 
            new Font("SansSerif", Font.BOLD, 16), new Color(50, 100, 50), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showGameIntro(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Scena: Cura i feriti
     */
    private void showHealWoundedScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        // === BARRA DI STATO IN ALTO ===
        JPanel statusBar = createDialogStatusBar();
        mainPanel.add(statusBar, BorderLayout.NORTH);
        
        JPanel imagePanel = new JPanel() {
            private Image healImage;
            {
                healImage = loadImageFromClasspath("curare feriti.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (healImage != null) {
                    g2d.drawImage(healImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(60, 40, 40));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(80, 50, 50));
        JLabel titleLabel = new JLabel("🏥 CURA I FERITI");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 200, 200));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Ti avvicini ai feriti più gravi.\n\n" +
            "Usando strisce di tessuto dalle valigie, fasci le ferite.\n" +
            "Un uomo con una gamba rotta ti ringrazia con le lacrime agli occhi.\n\n" +
            "Hai salvato delle vite oggi. Ma vedere tutto quel sangue...\n" +
            "ti ha scosso profondamente."
        );
        
        // Effetto: -5 sanità (trauma), nessun danno fisico
        engine.getPlayer().removeSanity(5);
        
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("🧠 " + sanity + "/100 (-5)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 150, 150));
        
        JButton continueBtn = createStyledButton("➡️ Continua", 
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 50, 50), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showGameIntro(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Scena: Usa l'alcol per curarti
     */
    private void showUseAlcoholScene(String playerName) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        
        // === BARRA DI STATO IN ALTO ===
        JPanel statusBar = createDialogStatusBar();
        mainPanel.add(statusBar, BorderLayout.NORTH);
        
        JPanel imagePanel = new JPanel() {
            private Image alcoholImage;
            {
                alcoholImage = loadImageFromClasspath("curarsi con alchol.jpg");
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (alcoholImage != null) {
                    g2d.drawImage(alcoholImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(50, 40, 30));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(100, 70, 40));
        JLabel titleLabel = new JLabel("🥃 DISINFETTARE LE FERITE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 220, 150));
        titlePanel.add(titleLabel);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        // Determina quale alcol ha
        String drinkName = engine.getPlayer().hasItem("Bottiglietta di Vodka") ? 
            "vodka" : "whisky";
        
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sceneText.setText(
            "Apri la bottiglietta di " + drinkName + " dell'aereo.\n\n" +
            "Versi l'alcol sulle tue ferite. BRUCIA TERRIBILMENTE!\n" +
            "Ma almeno non si infetteranno.\n\n" +
            "Bevi un sorso per calmare i nervi... forse non era una buona idea.\n" +
            "Ti senti un po' stordito, ma le ferite sono pulite."
        );
        
        // Effetto: +15 vita (ferite pulite), -10 sanità (alcol + dolore)
        engine.getPlayer().addHealth(15);
        engine.getPlayer().removeSanity(10);
        
        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("❤️ " + health + "/100 (+15)  |  🧠 " + sanity + "/100 (-10)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 180, 100));
        
        JButton continueBtn = createStyledButton("➡️ Continua", 
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 70, 40), Color.WHITE);
        
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showGameIntro(playerName);
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);
        
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(btnPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Mostra l'intro del gioco dopo la scritta LOST
     */
    private void showGameIntro(String playerName) {
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

        // Usa l'immagine del capitolo corrente
        currentImageKey = engine.getCurrentChapterImageKey();

        // Aggiorna titolo se siamo in un capitolo
        if (response.contains("CAP.")) {
            int start = response.indexOf(": ");
            int end = response.indexOf("\n\n");
            if (start > 0 && end > start) {
                currentTitle = response.substring(start + 2, end);
            }
        }

        // Aggiorna etichette bottoni per mini giochi
        updateButtonLabelsForMiniGame();

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
            
            // Render - usa l'immagine del capitolo corrente
            renderer.render(g2d, currentImageKey, currentText, currentTitle, status);
        }
    }
    
    /**
     * Carica un'immagine dal classpath (src/main/resources/images/)
     */
    private static Image loadImageFromClasspath(String filename) {
        try {
            InputStream is = FullScreenGUI.class.getResourceAsStream("/images/" + filename);
            if (is != null) {
                Image img = ImageIO.read(is);
                is.close();
                return img;
            }
        } catch (Exception e) {
            System.out.println("Immagine non trovata: " + filename);
        }
        return null;
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
