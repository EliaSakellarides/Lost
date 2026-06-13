package com.lost.gui;

import com.lost.graphics.GameFonts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

/**
 * Helper per costruire scene dialog con layout standard:
 * immagine in alto (55%), titolo, testo narrativo e bottoni in basso
 */
public class SceneBuilder {
    /** Caratteri rivelati a ogni tick: l'intro scorre lenta, da film. */
    private static final int INTRO_CHARS_PER_TICK = 1;
    /** Millisecondi tra un carattere e l'altro. */
    private static final int INTRO_TICK_MS = 35;

    private final JFrame parent;
    private final int screenWidth;
    private final int screenHeight;
    /** Unica finestra riutilizzata da tutte le scene dell'intro. */
    private JDialog persistentDialog;

    /**
     * Crea il builder per le scene a schermo intero.
     * @param parent finestra principale a cui agganciare i dialog
     * @param screenWidth larghezza dello schermo
     * @param screenHeight altezza dello schermo
     */
    public SceneBuilder(JFrame parent, int screenWidth, int screenHeight) {
        this.parent = parent;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Crea un dialog senza decorazioni grande quanto lo schermo.
     * @return il dialog pronto da riempire
     */
    public JDialog createFullScreenDialog() {
        // Una sola finestra per tutta l'intro: le scene cambiano solo il
        // contenuto (setContentPane), senza mai aprire o chiudere finestre.
        // Cosi' sparisce il bordino bianco nelle transizioni.
        if (persistentDialog == null) {
            persistentDialog = new JDialog(parent, false);
            persistentDialog.setUndecorated(true);
            persistentDialog.setSize(screenWidth, screenHeight);
            persistentDialog.setLocationRelativeTo(parent);
            persistentDialog.setBackground(Color.BLACK);
            persistentDialog.getRootPane().setBackground(Color.BLACK);
            persistentDialog.getLayeredPane().setBackground(Color.BLACK);
            persistentDialog.getContentPane().setBackground(Color.BLACK);
        }
        return persistentDialog;
    }

    /** {@return la finestra persistente dell'intro, null se non creata} */
    public JDialog getPersistentDialog() {
        return persistentDialog;
    }

    /** Chiude la finestra dell'intro al termine della sequenza. */
    public void closeIntroDialog() {
        if (persistentDialog != null) {
            persistentDialog.dispose();
            persistentDialog = null;
        }
    }

    /**
     * Crea il pannello immagine della scena (55% dell'altezza).
     * @param imageFilename nome del file immagine nel classpath
     * @return pannello che disegna l'immagine ridimensionata
     */
    public JPanel createImagePanel(String imageFilename) {
        JPanel imagePanel = new JPanel() {
            private Image image;
            {
                image = loadImageFromClasspath(imageFilename);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (image != null) {
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(Color.BLACK);
        return imagePanel;
    }

    /**
     * Crea la fascia del titolo della scena.
     * @param title testo del titolo
     * @param bgColor colore di sfondo
     * @param fgColor colore del testo
     * @return pannello con il titolo centrato
     */
    public JPanel createTitlePanel(String title, Color bgColor, Color fgColor) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(bgColor);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameFonts.retroBold(32f));
        titleLabel.setForeground(fgColor);
        titlePanel.add(titleLabel);
        return titlePanel;
    }

    /**
     * Crea l'area di testo narrativo della scena.
     * Il testo appare con effetto macchina da scrivere, lento e
     * cinematografico; un click sul testo lo completa subito.
     * @param text testo da mostrare
     * @return area di testo non editabile con font retro'
     */
    public JTextArea createSceneText(String text) {
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(GameFonts.retroPlain(25f));
        startTypewriter(sceneText, text);
        return sceneText;
    }

    private static void startTypewriter(JTextArea area, String fullText) {
        if (fullText == null || fullText.isEmpty()) {
            area.setText("");
            return;
        }

        final int[] index = {0};
        Timer timer = new Timer(INTRO_TICK_MS, null);
        timer.addActionListener(e -> {
            index[0] = Math.min(fullText.length(), index[0] + INTRO_CHARS_PER_TICK);
            area.setText(fullText.substring(0, index[0]));
            area.setCaretPosition(area.getDocument().getLength()); // segue il testo
            if (index[0] >= fullText.length()) {
                timer.stop();
            }
        });

        // Un click sul testo completa subito la digitazione
        area.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                timer.stop();
                area.setText(fullText);
            }
        });

        timer.start();
    }

    /**
     * Crea la fascia dei bottoni in fondo alla scena.
     * @param buttons bottoni da disporre in fila
     * @return pannello con i bottoni centrati
     */
    public JPanel createButtonPanel(JButton... buttons) {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.BLACK);
        for (JButton btn : buttons) {
            btnPanel.add(btn);
        }
        return btnPanel;
    }

    /**
     * Assembla il layout standard di una scena.
     * @param dialog dialog di destinazione
     * @param imagePanel pannello immagine in alto
     * @param titlePanel fascia del titolo, puo essere null
     * @param sceneText area del testo narrativo
     * @param buttonPanel fascia dei bottoni
     * @param statusBar opzionale, puo essere null
     */
    public void assembleStandardScene(JDialog dialog, JPanel imagePanel,
                                       JPanel titlePanel, JTextArea sceneText,
                                       JPanel buttonPanel, JPanel statusBar) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        // Zona superiore a dimensione fissa: barra di stato + immagine + titolo.
        // Sta in NORTH, quindi usa la sua dimensione preferita (immagine al 55%)
        // e NON si ridimensiona quando il testo sotto si riempie.
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        if (statusBar != null) {
            topPanel.add(statusBar, BorderLayout.NORTH);
        }
        topPanel.add(imagePanel, BorderLayout.CENTER);
        if (titlePanel != null) {
            topPanel.add(titlePanel, BorderLayout.SOUTH);
        }

        // Il testo occupa lo spazio rimanente (fisso): l'effetto macchina da
        // scrivere lo riempie senza spostare immagine o bottoni.
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        // Scroll trasparente: i testi lunghi non vengono tagliati e si possono
        // scorrere; l'effetto macchina da scrivere segue il testo verso il basso.
        JScrollPane textScroll = new JScrollPane(sceneText);
        textScroll.setOpaque(false);
        textScroll.getViewport().setOpaque(false);
        textScroll.setBorder(null);
        textScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textScroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        textPanel.add(textScroll, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Sostituisce il contenuto della finestra persistente senza
        // chiuderla/riaprirla: transizione fluida tra le scene.
        dialog.setContentPane(mainPanel);
        dialog.getContentPane().setBackground(Color.BLACK);
        dialog.revalidate();
        dialog.repaint();
    }

    /**
     * Carica un'immagine dalla cartella images del classpath.
     * @param filename nome del file immagine
     * @return l'immagine caricata, null se non trovata
     */
    public static Image loadImageFromClasspath(String filename) {
        try {
            InputStream is = SceneBuilder.class.getResourceAsStream("/images/" + filename);
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
}
