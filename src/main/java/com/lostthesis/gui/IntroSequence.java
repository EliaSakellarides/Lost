package com.lostthesis.gui;

import com.lostthesis.engine.GameEngine;
import com.lostthesis.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Gestisce tutta la sequenza cinematica introduttiva del gioco.
 * Contiene 13 scene: dall'animazione LOST fino alla scelta sulla spiaggia.
 */
public class IntroSequence {

    private final JFrame parent;
    private final GameEngine engine;
    private final SceneBuilder scene;
    private final int screenWidth;
    private final int screenHeight;
    private final Runnable onComplete;
    private String playerName;

    public IntroSequence(JFrame parent, GameEngine engine,
                         int screenWidth, int screenHeight, Runnable onComplete) {
        this.parent = parent;
        this.engine = engine;
        this.scene = new SceneBuilder(parent, screenWidth, screenHeight);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.onComplete = onComplete;
    }

    public void start(String playerName) {
        this.playerName = playerName;
        showLostIntro();
    }

    // ==================== SCENA 1: Animazione LOST ====================

    private void showLostIntro() {
        JWindow introWindow = new JWindow();
        introWindow.setSize(screenWidth, screenHeight);
        introWindow.setLocationRelativeTo(parent);

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

                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                AffineTransform originalTransform = g2d.getTransform();

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                g2d.translate(centerX, centerY);
                g2d.rotate(rotation[0]);
                g2d.scale(scale[0], scale[0]);
                g2d.translate(-centerX, -centerY);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));

                Font lostFont = new Font("Times New Roman", Font.BOLD, 180);
                g2d.setFont(lostFont);
                g2d.setColor(Color.WHITE);

                String text = "LOST";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 30;
                g2d.drawString(text, x, y);

                g2d.setTransform(originalTransform);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));

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

        Timer animTimer = new Timer(30, null);
        final int[] frame = {0};
        final int FADE_IN_FRAMES = 60;
        final int ROTATE_FRAMES = 200;
        final int TOTAL_FRAMES = FADE_IN_FRAMES + ROTATE_FRAMES;

        animTimer.addActionListener(e -> {
            frame[0]++;

            if (frame[0] <= FADE_IN_FRAMES) {
                alpha[0] = (float) frame[0] / FADE_IN_FRAMES;
                scale[0] = 0.5 + (0.5 * frame[0] / FADE_IN_FRAMES);
                rotation[0] = Math.toRadians(90.0 * frame[0] / FADE_IN_FRAMES);
            } else if (frame[0] <= TOTAL_FRAMES) {
                alpha[0] = 1.0f;
                scale[0] = 1.0;
                int rotateFrame = frame[0] - FADE_IN_FRAMES;
                rotation[0] = Math.toRadians(90.0 + (270.0 * rotateFrame / ROTATE_FRAMES));
            } else {
                alpha[0] -= 0.05f;
                scale[0] += 0.02;

                if (alpha[0] <= 0f) {
                    animTimer.stop();
                    introWindow.dispose();
                    showIntroImages();
                }
            }

            introPanel.repaint();
        });

        animTimer.start();
    }

    // ==================== SCENA 2: Immagine aereo ====================

    private void showIntroImages() {
        JWindow imageWindow = new JWindow();
        imageWindow.setSize(screenWidth, screenHeight);
        imageWindow.setLocationRelativeTo(parent);

        final Image[] airplaneImage = {SceneBuilder.loadImageFromClasspath("areo scena iniziale.jpg")};
        final float[] alpha = {0f};

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (airplaneImage[0] != null) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));

                    int imgW = airplaneImage[0].getWidth(null);
                    int imgH = airplaneImage[0].getHeight(null);
                    double scaleX = (double) getWidth() / imgW;
                    double scaleY = (double) getHeight() / imgH;
                    double sc = Math.max(scaleX, scaleY);

                    int newW = (int) (imgW * sc);
                    int newH = (int) (imgH * sc);
                    int x = (getWidth() - newW) / 2;
                    int y = (getHeight() - newH) / 2;

                    g2d.drawImage(airplaneImage[0], x, y, newW, newH, null);
                }
            }
        };

        imagePanel.setBackground(Color.BLACK);
        imageWindow.add(imagePanel);
        imageWindow.setVisible(true);

        Timer imageTimer = new Timer(30, null);
        final int[] frame = {0};
        final int FADE_IN = 40;
        final int HOLD = 100;
        final int FADE_OUT = 40;

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
                showHostessScene();
            }

            imagePanel.repaint();
        });

        imageTimer.start();
    }

    // ==================== SCENA 3: Hostess ====================

    private void showHostessScene() {
        JDialog dialog = scene.createFullScreenDialog();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 40, 50));

        // Immagine hostess (caricamento custom per fallback jpg/png)
        JPanel imagePanel = new JPanel() {
            private Image hostessImage;
            {
                hostessImage = SceneBuilder.loadImageFromClasspath("hostess.jpg");
                if (hostessImage == null) {
                    hostessImage = SceneBuilder.loadImageFromClasspath("hostess.png");
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (hostessImage != null) {
                    g2d.drawImage(hostessImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(new Color(50, 60, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.ITALIC, 16));
                    g2d.drawString("[ Immagine: hostess.png ]", getWidth()/2 - 80, getHeight()/2);
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(new Color(30, 40, 50));

        JPanel titlePanel = scene.createTitlePanel(
            "\u2708\uFE0F VOL OCEANIC 815 - IN VOLO",
            new Color(40, 60, 40), new Color(255, 220, 100));
        titlePanel.getComponent(0); // label gia nel panel

        JTextArea sceneText = scene.createSceneText(
            "Un'hostess si avvicina con un carrello delle bevande.\n\n" +
            "\uD83D\uDC69\u200D\u2708\uFE0F \"Posso offrirle qualcosa da bere? Vodka o whisky?\"");

        JButton vodkaBtn = GuiButtonFactory.create("\uD83C\uDF78 Vodka",
            new Font("SansSerif", Font.BOLD, 16), new Color(60, 80, 120), Color.WHITE);
        JButton whiskyBtn = GuiButtonFactory.create("\uD83E\uDD43 Whisky",
            new Font("SansSerif", Font.BOLD, 16), new Color(120, 80, 40), Color.WHITE);
        JButton noBtn = GuiButtonFactory.create("\u274C No grazie",
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 80, 80), Color.WHITE);

        vodkaBtn.addActionListener(e -> {
            dialog.dispose();
            Item vodka = new Item(
                "Bottiglietta di Vodka",
                "Una piccola bottiglietta di vodka dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
                true
            );
            engine.getPlayer().addItem(vodka);
            engine.getPlayer().removeHealth(5);
            showItemObtainedScene("Bottiglietta di Vodka");
        });

        whiskyBtn.addActionListener(e -> {
            dialog.dispose();
            Item whisky = new Item(
                "Bottiglietta di Whisky",
                "Una piccola bottiglietta di whisky dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
                true
            );
            engine.getPlayer().addItem(whisky);
            engine.getPlayer().removeHealth(5);
            showItemObtainedScene("Bottiglietta di Whisky");
        });

        noBtn.addActionListener(e -> {
            dialog.dispose();
            showTurbulenceScene();
        });

        JPanel buttonPanel = scene.createButtonPanel(vodkaBtn, whiskyBtn, noBtn);
        buttonPanel.setBackground(new Color(30, 40, 50));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(30, 40, 50));
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        textPanel.add(sceneText, BorderLayout.CENTER);
        sceneText.setBackground(new Color(30, 40, 50));
        textPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // ==================== SCENA 4: Oggetto ottenuto ====================

    private void showItemObtainedScene(String itemName) {
        JDialog itemDialog = new JDialog(parent, false);
        itemDialog.setUndecorated(true);
        itemDialog.setSize(500, 300);
        itemDialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 50, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 100), 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel iconLabel = new JLabel("\uD83C\uDF92", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));

        JLabel titleLabel = new JLabel("NUOVO OGGETTO!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(100, 255, 100));

        JLabel itemLabel = new JLabel(itemName, SwingConstants.CENTER);
        itemLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        itemLabel.setForeground(Color.WHITE);

        int health = engine.getPlayer().getHealth();
        JLabel healthLabel = new JLabel("\u2764\uFE0F Vita: " + health + "/100 (-5)", SwingConstants.CENTER);
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

        Timer closeTimer = new Timer(2000, e -> {
            itemDialog.dispose();
            showTurbulenceScene();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    // ==================== SCENA 5: Turbolenza ====================

    private void showTurbulenceScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("aereotrema.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\u26A1 TURBOLENZA! \u26A1", new Color(80, 30, 30), new Color(255, 100, 100));
        JTextArea text = scene.createSceneText(
            "L'aereo inizia a tremare violentemente!\n" +
            "Le maschere d'ossigeno cadono dal soffitto.\n" +
            "Urla di panico... poi tutto diventa nero...");

        JButton continueBtn = GuiButtonFactory.create("\uD83D\uDCA5 Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 50, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showPlaneBreakingScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 6: Aereo si spezza ====================

    private void showPlaneBreakingScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("aereo si rompe in volo.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83D\uDCA5 L'AEREO SI SPEZZA! \uD83D\uDCA5", new Color(100, 30, 30), new Color(255, 80, 80));
        JTextArea text = scene.createSceneText(
            "Un boato assordante! La fusoliera si spezza in due!\n" +
            "Passeggeri e detriti volano nel vuoto...\n" +
            "Stai precipitando verso un'isola sconosciuta...");

        JButton continueBtn = GuiButtonFactory.create("\u2B07\uFE0F Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 50, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showEyeOpeningScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 7: Occhio si apre ====================

    private void showEyeOpeningScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("occhio aperto -2.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83D\uDC41\uFE0F ... ", new Color(50, 50, 80), new Color(200, 200, 255));
        JTextArea text = scene.createSceneText(
            "...\n\n" +
            "Apri gli occhi.\n\n" +
            "Foglie di bamb\u00F9 oscillano sopra di te...");

        JButton continueBtn = GuiButtonFactory.create("\uD83D\uDC40 Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(60, 60, 90), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showJungleAwakeningScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 8: Risveglio giungla ====================

    private void showJungleAwakeningScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("risveglio in giungla.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83C\uDF34 RISVEGLIO", new Color(30, 60, 30), new Color(150, 220, 150));
        JTextArea text = scene.createSceneText(
            "Ti alzi a fatica. Sei nella giungla.\n" +
            "Alberi di bamb\u00F9 ti circondano. Sei confuso, disorientato.\n" +
            "In lontananza senti urla e il rumore di un motore...");

        JButton continueBtn = GuiButtonFactory.create("\uD83C\uDF3F Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(50, 80, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showFollowVincentScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 9: Vincent ====================

    private void showFollowVincentScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("segui vincent.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83D\uDC15 VINCENT", new Color(60, 50, 30), new Color(220, 200, 150));
        JTextArea text = scene.createSceneText(
            "Un cane Labrador bianco sbuca dalla vegetazione!\n" +
            "Ti guarda, abbaia, e corre via verso la spiaggia.\n" +
            "Decidi di seguirlo...");

        JButton continueBtn = GuiButtonFactory.create("\uD83C\uDFC3 Segui Vincent!",
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 70, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showBeachCrashScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 10: Spiaggia con scelte ====================

    private void showBeachCrashScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("aereo distrutto su spiaggia.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83C\uDFDD\uFE0F LA SPIAGGIA", new Color(80, 60, 40), new Color(255, 220, 150));
        JTextArea text = scene.createSceneText(
            "Emergi dalla giungla e resti senza fiato.\n\n" +
            "La spiaggia \u00E8 un caos di detriti fumanti, bagagli sparsi,\n" +
            "e sopravvissuti in stato di shock.\n\n" +
            "Il volo Oceanic 815 \u00E8 precipitato su un'isola sconosciuta.\n" +
            "Benvenuto nel tuo incubo, " + playerName + ".");

        JButton helpBtn = GuiButtonFactory.create("\uD83E\uDE79 Aiuta i sopravvissuti",
            new Font("SansSerif", Font.BOLD, 14), new Color(60, 100, 60), Color.WHITE);
        JButton healBtn = GuiButtonFactory.create("\uD83C\uDFE5 Cura i feriti",
            new Font("SansSerif", Font.BOLD, 14), new Color(100, 60, 60), Color.WHITE);
        JButton alcoholBtn = GuiButtonFactory.create("\uD83E\uDD43 Usa l'alcol per curarti",
            new Font("SansSerif", Font.BOLD, 14), new Color(120, 80, 40), Color.WHITE);

        helpBtn.addActionListener(e -> {
            dialog.dispose();
            showHelpSurvivorsScene();
        });

        healBtn.addActionListener(e -> {
            dialog.dispose();
            showHealWoundedScene();
        });

        alcoholBtn.addActionListener(e -> {
            dialog.dispose();
            showUseAlcoholScene();
        });

        JPanel btnPanel = scene.createButtonPanel(helpBtn, healBtn, alcoholBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 11: Aiuta sopravvissuti ====================

    private void showHelpSurvivorsScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("aiuto spravvissuti.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83E\uDE79 AIUTO AI SOPRAVVISSUTI", new Color(50, 80, 50), new Color(200, 255, 200));
        JTextArea text = scene.createSceneText(
            "Corri tra i detriti fumanti per aiutare gli altri sopravvissuti.\n\n" +
            "Trascini persone lontano dai rottami in fiamme,\n" +
            "rassicuri chi \u00E8 in stato di shock, cerchi superstiti tra le valigie.\n\n" +
            "Il tuo coraggio ispira gli altri. Hai guadagnato rispetto.\n" +
            "Ma lo sforzo ti ha stancato...");

        // Effetto: +10 sanita, -10 vita
        engine.getPlayer().addSanity(10);
        engine.getPlayer().removeHealth(10);

        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("\u2764\uFE0F " + health + "/100 (-10)  |  \uD83E\uDDE0 " + sanity + "/100 (+10)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 200, 100));

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua",
            new Font("SansSerif", Font.BOLD, 16), new Color(50, 100, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            onComplete.run();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);

        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 12: Cura feriti ====================

    private void showHealWoundedScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("curare feriti.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83C\uDFE5 CURA I FERITI", new Color(80, 50, 50), new Color(255, 200, 200));
        JTextArea text = scene.createSceneText(
            "Ti avvicini ai feriti pi\u00F9 gravi.\n\n" +
            "Usando strisce di tessuto dalle valigie, fasci le ferite.\n" +
            "Un uomo con una gamba rotta ti ringrazia con le lacrime agli occhi.\n\n" +
            "Hai salvato delle vite oggi. Ma vedere tutto quel sangue...\n" +
            "ti ha scosso profondamente.");

        // Effetto: -5 sanita
        engine.getPlayer().removeSanity(5);

        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("\uD83E\uDDE0 " + sanity + "/100 (-5)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 150, 150));

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua",
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 50, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            onComplete.run();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);

        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 13: Usa alcol ====================

    private void showUseAlcoholScene() {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("curarsi con alchol.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83E\uDD43 DISINFETTARE LE FERITE", new Color(100, 70, 40), new Color(255, 220, 150));

        String drinkName = engine.getPlayer().hasItem("Bottiglietta di Vodka") ?
            "vodka" : "whisky";

        JTextArea text = scene.createSceneText(
            "Apri la bottiglietta di " + drinkName + " dell'aereo.\n\n" +
            "Versi l'alcol sulle tue ferite. BRUCIA TERRIBILMENTE!\n" +
            "Ma almeno non si infetteranno.\n\n" +
            "Bevi un sorso per calmare i nervi... forse non era una buona idea.\n" +
            "Ti senti un po' stordito, ma le ferite sono pulite.");

        // Effetto: +15 vita, -10 sanita
        engine.getPlayer().addHealth(15);
        engine.getPlayer().removeSanity(10);

        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel("\u2764\uFE0F " + health + "/100 (+15)  |  \uD83E\uDDE0 " + sanity + "/100 (-10)", SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 180, 100));

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua",
            new Font("SansSerif", Font.BOLD, 16), new Color(100, 70, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            onComplete.run();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);

        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }
}
