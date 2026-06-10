package com.lost.gui;

import com.lost.engine.GameEngine;
import com.lost.graphics.GameFonts;
import com.lost.model.Item;

import javax.swing.*;
import java.awt.*;

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

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));

                Font lostFont = GameFonts.retroBold(190f);
                g2d.setFont(lostFont);
                g2d.setColor(Color.WHITE);

                String text = "LOST";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 30;
                g2d.drawString(text, x, y);
            }
        };

        introPanel.setBackground(Color.BLACK);
        introWindow.add(introPanel);
        introWindow.setVisible(true);

        Timer animTimer = new Timer(30, null);
        final int[] frame = {0};
        final boolean[] done = {false};
        final int FADE_IN_FRAMES = 150;
        final int HOLD_FRAMES = 70;
        final int FADE_OUT_FRAMES = 45;
        final int TOTAL_FRAMES = FADE_IN_FRAMES + HOLD_FRAMES + FADE_OUT_FRAMES;

        animTimer.addActionListener(e -> {
            if (done[0]) return;
            frame[0]++;

            if (frame[0] <= FADE_IN_FRAMES) {
                alpha[0] = (float) frame[0] / FADE_IN_FRAMES;
            } else if (frame[0] <= FADE_IN_FRAMES + HOLD_FRAMES) {
                alpha[0] = 1.0f;
            } else if (frame[0] <= TOTAL_FRAMES) {
                int fadeOutFrame = frame[0] - FADE_IN_FRAMES - HOLD_FRAMES;
                alpha[0] = Math.max(0f, 1.0f - ((float) fadeOutFrame / FADE_OUT_FRAMES));
            }

            if (frame[0] > TOTAL_FRAMES && currentScene == 0) {
                currentScene = 1;
                done[0] = true;
                animTimer.stop();
                introWindow.dispose();
                showIntroImages();
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
        JPanel imagePanel = scene.createImagePanel("aereo_scena_iniziale.jpg");
        JTextArea text = scene.createSceneText(
            "Sei a bordo del volo Oceanic 815, direzione Los Angeles.\n" +
            "Un viaggio come tanti altri.");

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(40, 60, 90), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showHostessScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 3: Hostess ====================

    private void showHostessScene() {
        if (currentScene != 2) return;
        currentScene = 3;
        JDialog dialog = scene.createFullScreenDialog();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

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
                    g2d.setFont(GameFonts.retro(Font.ITALIC, 22f));
                    g2d.drawString("[ Immagine: hostess.png ]", getWidth()/2 - 80, getHeight()/2);
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(Color.BLACK);

        JTextArea sceneText = scene.createSceneText(
            "Un'hostess si avvicina con il carrello delle bevande.\n\n" +
            "\"Posso offrirle qualcosa da bere?\"\n\n" +
            "E' particolarmente generosa. Ne approfitti e riponi\n" +
            "una bottiglietta di whisky nella tasca della giacca.\n" +
            "Non si sa mai.");

        // Whisky ottenuto automaticamente
        Item whisky = new Item(
            "Bottiglietta di Whisky",
            "Una piccola bottiglietta di whisky dell'aereo. Altamente infiammabile... potrebbe tornare utile.",
            true
        );
        engine.getPlayer().addItem(whisky);

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(120, 80, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showTurbulenceScene();
        });

        JPanel buttonPanel = scene.createButtonPanel(continueBtn);
        buttonPanel.setBackground(Color.BLACK);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        textPanel.add(sceneText, BorderLayout.CENTER);
        sceneText.setBackground(Color.BLACK);
        textPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);

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
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 100), 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setFont(GameFonts.retroPlain(72f));

        JLabel titleLabel = new JLabel("NUOVO OGGETTO!", SwingConstants.CENTER);
        titleLabel.setFont(GameFonts.retroBold(34f));
        titleLabel.setForeground(new Color(100, 255, 100));

        JLabel itemLabel = new JLabel(itemName, SwingConstants.CENTER);
        itemLabel.setFont(GameFonts.retro(Font.ITALIC, 26f));
        itemLabel.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.BLACK);
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
        JTextArea text = scene.createSceneText(
            "L'aereo inizia a tremare. Poi violentemente.\n" +
            "Le maschere d'ossigeno cadono dal soffitto.\n" +
            "Urla. Il rumore del metallo che cede.\n" +
            "Poi il buio.");

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(100, 50, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showPlaneBreakingScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 6: Aereo si spezza ====================

    private void showPlaneBreakingScene() {
        if (currentScene != 4) return;
        currentScene = 5;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("aereo_si_rompe_in_volo.jpg");
        JTextArea text = scene.createSceneText(
            "Un boato. La fusoliera si spezza in due.\n" +
            "Passeggeri, sedili, detriti — tutto vola nel vuoto.\n" +
            "Stai cadendo.");

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(100, 50, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showEyeOpeningScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 7: Occhio si apre ====================

    private void showEyeOpeningScene() {
        if (currentScene != 5) return;
        currentScene = 6;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("occhio_aperto.jpg");
        JTextArea text = scene.createSceneText(
            "Apri gli occhi.\n\n" +
            "Foglie di bambu oscillano sopra di te.\n" +
            "Non sai dove sei.\n" +
            "Non sai quanto tempo e passato.");

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(60, 60, 90), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showJungleAwakeningScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 8: Risveglio giungla ====================

    private void showJungleAwakeningScene() {
        if (currentScene != 6) return;
        currentScene = 7;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("risveglio_in_giungla.jpg");
        JTextArea text = scene.createSceneText(
            "Ti alzi a fatica. Sei nella giungla.\n" +
            "Alberi di bambu, luce filtrata, silenzio.\n" +
            "In lontananza: urla, e il rumore sordo di qualcosa che brucia.");

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(50, 80, 50), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showFollowVincentScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 9: Vincent ====================

    private void showFollowVincentScene() {
        if (currentScene != 7) return;
        currentScene = 8;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel imagePanel = scene.createImagePanel("segui_vincent.jpg");
        JTextArea text = scene.createSceneText(
            "Un cane Labrador bianco sbuca dalla vegetazione.\n" +
            "Ti guarda. Abbaia. Corre via verso la spiaggia.\n\n" +
            "Lo segui.");

        JButton continueBtn = GuiButtonFactory.create("Segui Vincent",
            GameFonts.retroBold(24f), new Color(80, 70, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showBeachCrashScene();
        });

        JPanel btnPanel = scene.createButtonPanel(continueBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, null);
        dialog.setVisible(true);
    }

    // ==================== SCENA 10: Spiaggia - narrativa ====================

    private void showBeachCrashScene() {
        if (currentScene != 8) return;
        currentScene = 9;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "spiaggia", screenWidth);
        JPanel imagePanel = scene.createImagePanel("aereo_distrutto_su_spiaggia.jpg");
        JTextArea text = scene.createSceneText(
            "Emergi dalla giungla e ti fermi.\n\n" +
            "La spiaggia e' una scena di guerra. Rottami fumanti, bagagli\n" +
            "sparsi, persone a terra. Qualcuno grida. Qualcuno non si muove.\n\n" +
            "Corri ad aiutare. Trascini feriti lontano dalle fiamme.\n" +
            "Non sai dove sei. Non sai come tornare a casa.\n" +
            "Ma sai che il mondo sa che il volo e' sparito.\n" +
            "E' solo questione di resistere.");

        // Effetto: +10 sanita, -10 vita (sforzo fisico)
        engine.getPlayer().addSanity(10);
        engine.getPlayer().removeHealth(10);

        int health = engine.getPlayer().getHealth();
        int sanity = engine.getPlayer().getSanity();
        JLabel statsLabel = new JLabel(
            "Salute " + health + "/100 (-10)  |  Sanita " + sanity + "/100 (+10)",
            SwingConstants.CENTER);
        statsLabel.setFont(GameFonts.retroBold(22f));
        statsLabel.setForeground(new Color(200, 200, 100));

        JButton continueBtn = GuiButtonFactory.create("Continua...",
            GameFonts.retroBold(24f), new Color(80, 60, 40), Color.WHITE);
        continueBtn.addActionListener(e -> {
            dialog.dispose();
            showJungleHealScene();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(statsLabel);
        btnPanel.add(Box.createHorizontalStrut(30));
        btnPanel.add(continueBtn);

        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }

    // ==================== SCENA 11: Giungla - prima scelta ====================

    private void showJungleHealScene() {
        if (currentScene != 9) return;
        currentScene = 10;
        JDialog dialog = scene.createFullScreenDialog();
        JPanel statusBar = StatusPanelFactory.createDialogStatusBar(engine, "giungla", screenWidth);
        JPanel imagePanel = scene.createImagePanel("risveglio_in_giungla.jpg");
        JTextArea text = scene.createSceneText(
            "Ti allontani dalla spiaggia per riprendere fiato.\n\n" +
            "Sei ferito, " + playerName + ". Tagli sulle braccia, lividi ovunque.\n" +
            "Hai bisogno di curarti.\n\n" +
            "Nella tasca della giacca senti ancora la bottiglietta di whisky.\n" +
            "Potrebbe servire come disinfettante.\n\n" +
            "Come ti curi?");

        JButton whiskyBtn = GuiButtonFactory.create("Usa il whisky sulle ferite",
            GameFonts.retroBold(22f), new Color(120, 80, 40), Color.WHITE);
        JButton bandageBtn = GuiButtonFactory.create("Fasciature di fortuna",
            GameFonts.retroBold(22f), new Color(60, 100, 60), Color.WHITE);
        JButton toughBtn = GuiButtonFactory.create("Stringi i denti",
            GameFonts.retroBold(22f), new Color(80, 80, 80), Color.WHITE);

        whiskyBtn.addActionListener(e -> {
            dialog.dispose();
            // Whisky: cura bene ma brucia e stordisce
            engine.getPlayer().addHealth(15);
            engine.getPlayer().removeSanity(10);
            showHealResultScene(
                "DISINFETTARE LE FERITE",
                "curarsi_con_alcol.jpg",
                new Color(100, 70, 40), new Color(255, 220, 150),
                "Apri la bottiglietta di whisky.\n\n" +
                "Versi l'alcol sulle ferite. BRUCIA TERRIBILMENTE!\n" +
                "Ma almeno non si infetteranno.\n\n" +
                "Bevi un sorso per calmare i nervi...\n" +
                "Ti senti stordito, ma le ferite sono pulite.",
                "+15 salute  |  -10 sanita");
        });

        bandageBtn.addActionListener(e -> {
            dialog.dispose();
            // Fasciature: piccola cura, mantieni sanita
            engine.getPlayer().addHealth(5);
            showHealResultScene(
                "FASCIATURE DI FORTUNA",
                "aiuto_sopravvissuti.jpg",
                new Color(50, 80, 50), new Color(200, 255, 200),
                "Strappi strisce di tessuto dalla camicia.\n\n" +
                "Fasci le ferite pi\u00F9 profonde alla meglio.\n" +
                "Non \u00E8 il massimo, ma almeno fermi il sangue.\n\n" +
                "Mantieni la lucidit\u00E0. Devi restare concentrato.",
                "+5 salute");
        });

        toughBtn.addActionListener(e -> {
            dialog.dispose();
            // Stringi i denti: nessuna cura, bonus sanita
            engine.getPlayer().addSanity(5);
            showHealResultScene(
                "STRINGI I DENTI",
                "risveglio_in_giungla.jpg",
                new Color(60, 60, 80), new Color(200, 200, 255),
                "Non hai tempo per questo. Ci sono cose pi\u00F9 importanti.\n\n" +
                "Il dolore \u00E8 solo nella testa. Puoi farcela.\n" +
                "Stringi i denti e ti rialzi.\n\n" +
                "Sei pi\u00F9 determinato che mai a sopravvivere.",
                "+5 sanita");
        });

        JPanel btnPanel = scene.createButtonPanel(whiskyBtn, bandageBtn, toughBtn);
        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, statusBar);
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
        statsLabel.setFont(GameFonts.retroBold(22f));
        statsLabel.setForeground(new Color(200, 200, 100));

        JButton continueBtn = GuiButtonFactory.create("Continua",
            GameFonts.retroBold(24f), titleBg, Color.WHITE);
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

        scene.assembleStandardScene(dialog, imagePanel, null, text, btnPanel, statusBar);
        dialog.setVisible(true);
    }
}
