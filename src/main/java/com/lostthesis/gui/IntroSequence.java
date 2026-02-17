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
    private int currentScene = 0;

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
        final boolean[] done = {false};
        final int FADE_IN_FRAMES = 60;
        final int ROTATE_FRAMES = 200;
        final int TOTAL_FRAMES = FADE_IN_FRAMES + ROTATE_FRAMES;

        animTimer.addActionListener(e -> {
            if (done[0]) return;
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

                if (alpha[0] <= 0f && currentScene == 0) {
                    currentScene = 1;
                    done[0] = true;
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
        if (currentScene != 1) return;
        currentScene = 2;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("areo scena iniziale.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\u2708\uFE0F VOL OCEANIC 815",
            new Color(30, 40, 60), new Color(200, 220, 255));
        JTextArea text = scene.createSceneText(
            "Sei a bordo del volo Oceanic 815, direzione Los Angeles.\n" +
            "Un viaggio come tanti altri... o almeno cos\u00EC credi.");

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(40, 60, 90), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showHostessScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 3: Hostess ====================

    private void showHostessScene() {
        if (currentScene != 2) return;
        currentScene = 3;
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
            "\uD83D\uDC69\u200D\u2708\uFE0F \"Posso offrirle qualcosa da bere?\"\n\n" +
            "L'hostess \u00E8 particolarmente generosa... ti offre pi\u00F9 del dovuto.\n" +
            "Ne approfitti e riponi una bottiglietta di whisky nella giacca.\n" +
            "Non si sa mai, potrebbe tornare utile...");

        // Whisky ottenuto automaticamente
        Item whisky = new Item(
            "Bottiglietta di Whisky",
            "Una piccola bottiglietta di whisky dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
            true
        );
        engine.getPlayer().addItem(whisky);

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(120, 80, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showTurbulenceScene();
        });

        JPanel buttonPanel = scene.createButtonPanel(continueBtn);
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

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 50, 30));
        centerPanel.add(Box.createVerticalGlue());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(itemLabel);
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
        if (currentScene != 3) return;
        currentScene = 4;
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
        if (currentScene != 4) return;
        currentScene = 5;
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
        if (currentScene != 5) return;
        currentScene = 6;
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
        if (currentScene != 6) return;
        currentScene = 7;
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
        if (currentScene != 7) return;
        currentScene = 8;
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

    // ==================== SCENA 10: Spiaggia - narrativa ====================

    private void showBeachCrashScene() {
        if (currentScene != 8) return;
        currentScene = 9;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("aereo distrutto su spiaggia.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83C\uDFDD\uFE0F LA SPIAGGIA", new Color(80, 60, 40), new Color(255, 220, 150));
        JTextArea text = scene.createSceneText(
            "Emergi dalla giungla e resti senza fiato.\n\n" +
            "La spiaggia \u00E8 un caos di detriti fumanti, bagagli sparsi,\n" +
            "e sopravvissuti in stato di shock.\n\n" +
            "Senza pensarci, corri ad aiutare. Trascini persone lontano\n" +
            "dai rottami in fiamme, rassicuri chi \u00E8 sotto shock.\n" +
            "Il tuo coraggio ispira gli altri.");

        // Effetto: +10 sanita, -10 vita (sforzo fisico)
        engine.getPlayer().addSanity(10);
        engine.getPlayer().removeHealth(10);

        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel(
            "\u2764\uFE0F " + health + "/100 (-10)  |  \uD83E\uDDE0 " + sanity + "/100 (+10)",
            SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 200, 100));

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua...",
            new Font("SansSerif", Font.BOLD, 16), new Color(80, 60, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showJungleHealScene();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);

        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 11: Giungla - prima scelta ====================

    private void showJungleHealScene() {
        if (currentScene != 9) return;
        currentScene = 10;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "giungla", screenWidth);
        JPanel imagePanel = scene.createImagePanel("risveglio in giungla.jpg");
        JPanel titlePanel = scene.createTitlePanel(
            "\uD83C\uDF3F DA SOLO NELLA GIUNGLA", new Color(30, 60, 30), new Color(150, 220, 150));
        JTextArea text = scene.createSceneText(
            "Dopo aver aiutato tutti, ti allontani dalla spiaggia.\n" +
            "Ti rifugi tra gli alberi per riprendere fiato.\n\n" +
            "Sei ferito, " + playerName + ". Tagli sulle braccia,\n" +
            "lividi ovunque. Hai bisogno di curarti.\n\n" +
            "Nella giacca senti la bottiglietta di whisky...\n" +
            "Potrebbe servire come disinfettante.\n\n" +
            "\u2753 Come ti curi?");

        JButton whiskyBtn = GuiButtonFactory.create("\uD83E\uDD43 Usa il whisky sulle ferite",
            new Font("SansSerif", Font.BOLD, 14), new Color(120, 80, 40), Color.WHITE);
        JButton bandageBtn = GuiButtonFactory.create("\uD83E\uDE79 Fasciature di fortuna",
            new Font("SansSerif", Font.BOLD, 14), new Color(60, 100, 60), Color.WHITE);
        JButton toughBtn = GuiButtonFactory.create("\uD83D\uDCAA Stringi i denti",
            new Font("SansSerif", Font.BOLD, 14), new Color(80, 80, 80), Color.WHITE);

        whiskyBtn.addActionListener(e -> {
            dialog.dispose();
            // Whisky: cura bene ma brucia e stordisce
            engine.getPlayer().addHealth(15);
            engine.getPlayer().removeSanity(10);
            showHealResultScene(
                "\uD83E\uDD43 DISINFETTARE LE FERITE",
                "curarsi con alchol.jpg",
                new Color(100, 70, 40), new Color(255, 220, 150),
                "Apri la bottiglietta di whisky.\n\n" +
                "Versi l'alcol sulle ferite. BRUCIA TERRIBILMENTE!\n" +
                "Ma almeno non si infetteranno.\n\n" +
                "Bevi un sorso per calmare i nervi...\n" +
                "Ti senti stordito, ma le ferite sono pulite.",
                "\u2764\uFE0F +" + 15 + " salute  |  \uD83E\uDDE0 -10 sanit\u00E0");
        });

        bandageBtn.addActionListener(e -> {
            dialog.dispose();
            // Fasciature: piccola cura, mantieni sanita
            engine.getPlayer().addHealth(5);
            showHealResultScene(
                "\uD83E\uDE79 FASCIATURE DI FORTUNA",
                "aiuto spravvissuti.jpg",
                new Color(50, 80, 50), new Color(200, 255, 200),
                "Strappi strisce di tessuto dalla camicia.\n\n" +
                "Fasci le ferite pi\u00F9 profonde alla meglio.\n" +
                "Non \u00E8 il massimo, ma almeno fermi il sangue.\n\n" +
                "Mantieni la lucidit\u00E0. Devi restare concentrato.",
                "\u2764\uFE0F +5 salute");
        });

        toughBtn.addActionListener(e -> {
            dialog.dispose();
            // Stringi i denti: nessuna cura, bonus sanita
            engine.getPlayer().addSanity(5);
            showHealResultScene(
                "\uD83D\uDCAA STRINGI I DENTI",
                "risveglio in giungla.jpg",
                new Color(60, 60, 80), new Color(200, 200, 255),
                "Non hai tempo per questo. Ci sono cose pi\u00F9 importanti.\n\n" +
                "Il dolore \u00E8 solo nella testa. Puoi farcela.\n" +
                "Stringi i denti e ti rialzi.\n\n" +
                "Sei pi\u00F9 determinato che mai a sopravvivere.",
                "\uD83E\uDDE0 +5 sanit\u00E0");
        });

        JPanel btnPanel = scene.createButtonPanel(whiskyBtn, bandageBtn, toughBtn);
        scene.assembleStandardScene(dialog, imagePanel, titlePanel, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 12: Risultato cura ====================

    private void showHealResultScene(String title, String imageName,
            Color titleBg, Color titleFg, String description, String statsText) {
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "giungla", screenWidth);
        JPanel imagePanel = scene.createImagePanel(imageName);
        JPanel titlePanel = scene.createTitlePanel(title, titleBg, titleFg);
        JTextArea text = scene.createSceneText(description);

        JLabel statsLabel = new JLabel(statsText, SwingConstants.CENTER);
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statsLabel.setForeground(new Color(200, 200, 100));

        JButton continueBtn = GuiButtonFactory.create("\u27A1\uFE0F Continua",
            new Font("SansSerif", Font.BOLD, 16), titleBg, Color.WHITE);
        continueBtn.addActionListener(e -> {
            if (currentScene != 10) return;
            currentScene = 11;
            onComplete.run();
            dialog.dispose();
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
