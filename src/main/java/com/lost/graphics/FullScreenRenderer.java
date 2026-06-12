package com.lost.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renderer per la modalità fullscreen con grafica pixel art
 */
public class FullScreenRenderer {
    private static final int IMAGE_TOP_MARGIN = 30;
    private static final int IMAGE_TEXT_GAP = 14;
    private static final int STATUS_BAR_HEIGHT = 50;
    private static final int BOTTOM_MARGIN = 20;
    private static final int MIN_TEXT_BOX_HEIGHT = 170;
    private static final int MAX_TEXT_BOX_HEIGHT = 230;

    private int screenWidth;
    private int screenHeight;
    private PixelArtManager pixelArtManager;
    
    // Layout
    private int imageWidth;
    private int imageHeight;
    private int textBoxX;
    private int textBoxY;
    private int textBoxWidth;
    private int textBoxHeight;
    
    // Colori tema LOST
    /** Colore di sfondo della finestra. */
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    /** Sfondo semitrasparente del box di testo. */
    public static final Color TEXT_BOX_COLOR = new Color(0, 0, 0, 235);
    /** Colore dei bordi (verde giungla). */
    public static final Color BORDER_COLOR = new Color(100, 150, 100);
    /** Colore del testo principale. */
    public static final Color TEXT_COLOR = new Color(200, 220, 200);
    /** Colore dei titoli (giallo ambra). */
    public static final Color TITLE_COLOR = new Color(255, 220, 100);
    /** Colore per le evidenziazioni. */
    public static final Color HIGHLIGHT_COLOR = new Color(100, 200, 100);

    /**
     * Crea il renderer e calcola il layout iniziale.
     * @param width larghezza dello schermo in pixel
     * @param height altezza dello schermo in pixel
     */
    public FullScreenRenderer(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        calculateLayout(width, height);
        
        // Inizializza il gestore delle immagini
        this.pixelArtManager = new PixelArtManager(imageWidth, imageHeight);
    }
    
    /**
     * Disegna la schermata di gioco: sfondo, immagine della location,
     * box del testo, barra di stato e logo.
     * @param g contesto grafico su cui disegnare
     * @param locationKey chiave dell'immagine da mostrare
     * @param statusInfo testo della barra di stato
     */
    public void render(Graphics2D g, String locationKey, String statusInfo) {
        // Sfondo
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Immagine della location
        BufferedImage locationImage = pixelArtManager.getImage(locationKey);
        if (locationImage != null) {
            int imgX = (screenWidth - imageWidth) / 2;
            int imgY = IMAGE_TOP_MARGIN;

            // Bordo immagine
            g.setColor(BORDER_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawRect(imgX - 3, imgY - 3, imageWidth + 6, imageHeight + 6);

            // Immagine
            g.drawImage(locationImage, imgX, imgY, imageWidth, imageHeight, null);
        }

        // Box del testo (solo sfondo + bordo, il testo lo fa JTextPane)
        renderTextBox(g);

        // Barra di stato in basso
        renderStatusBar(g, statusInfo);

        // Logo LOST in alto
        renderLogo(g);
    }
    
    private void renderTextBox(Graphics2D g) {
        // Sfondo semitrasparente
        g.setColor(TEXT_BOX_COLOR);
        g.fillRoundRect(textBoxX, textBoxY, textBoxWidth, textBoxHeight, 15, 15);

        // Bordo
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(textBoxX, textBoxY, textBoxWidth, textBoxHeight, 15, 15);

        // Il testo viene ora renderizzato dal JTextPane in FullScreenGUI
    }

    /**
     * Ricalcola il layout dopo un ridimensionamento della finestra.
     * Se le dimensioni dell'immagine cambiano, ricarica le immagini.
     * @param width nuova larghezza in pixel
     * @param height nuova altezza in pixel
     */
    public void updateLayout(int width, int height) {
        int oldImageWidth = imageWidth;
        int oldImageHeight = imageHeight;

        this.screenWidth = width;
        this.screenHeight = height;

        calculateLayout(width, height);

        if (oldImageWidth != imageWidth || oldImageHeight != imageHeight) {
            this.pixelArtManager = new PixelArtManager(imageWidth, imageHeight);
        }
    }

    private void calculateLayout(int width, int height) {
        this.imageWidth = (int) (width * 0.9);
        int maxImageHeight = height - IMAGE_TOP_MARGIN - IMAGE_TEXT_GAP -
            MIN_TEXT_BOX_HEIGHT - STATUS_BAR_HEIGHT - BOTTOM_MARGIN;
        this.imageHeight = Math.min(Math.max(180, (int) (height * 0.45)),
            Math.max(180, maxImageHeight));

        this.textBoxWidth = (int) (width * 0.9);
        int availableTextHeight = height - IMAGE_TOP_MARGIN - imageHeight -
            IMAGE_TEXT_GAP - STATUS_BAR_HEIGHT - BOTTOM_MARGIN;
        this.textBoxHeight = Math.max(MIN_TEXT_BOX_HEIGHT,
            Math.min(MAX_TEXT_BOX_HEIGHT, availableTextHeight));
        this.textBoxX = (width - textBoxWidth) / 2;
        this.textBoxY = IMAGE_TOP_MARGIN + imageHeight + IMAGE_TEXT_GAP;
    }
    
    private void renderStatusBar(Graphics2D g, String statusInfo) {
        int barHeight = 50;
        int barY = screenHeight - barHeight;
        
        // Sfondo barra
        g.setColor(Color.BLACK);
        g.fillRect(0, barY, screenWidth, barHeight);
        
        // Linea superiore
        g.setColor(BORDER_COLOR);
        g.drawLine(0, barY, screenWidth, barY);
        
        // Formato: "Salute 85% | Sanita 90% | Giorno 1 | SPIAGGIA"
        int health = 100;
        int sanity = 100;
        int day = 1;
        String location = "";
        
        if (statusInfo != null && !statusInfo.isEmpty()) {
            String[] parts = statusInfo.split("\\|");
            if (parts.length >= 4) {
                health = parseFirstNumber(parts[0], health);
                sanity = parseFirstNumber(parts[1], sanity);
                day = parseFirstNumber(parts[2], day);
                location = parts[3].trim();
            }
        }
        
        int xPos = 20;
        
        // === BARRA SALUTE ===
        g.setFont(GameFonts.retroBold(18f));
        g.setColor(new Color(255, 150, 150));
        g.drawString("Salute", xPos, barY + 20);

        int healthBarX = xPos + 55;
        int healthBarWidth = 110;
        int healthBarHeight = 12;

        g.setColor(new Color(70, 35, 35));
        g.fillRoundRect(healthBarX, barY + 10, healthBarWidth, healthBarHeight, 5, 5);

        Color healthColor;
        if (health > 70) healthColor = new Color(120, 220, 120);
        else if (health > 40) healthColor = new Color(255, 200, 100);
        else healthColor = new Color(255, 100, 100);

        int healthFillWidth = (int) (healthBarWidth * clampPercent(health) / 100.0);
        g.setColor(healthColor);
        g.fillRoundRect(healthBarX, barY + 10, healthFillWidth, healthBarHeight, 5, 5);

        g.setColor(new Color(140, 80, 80));
        g.drawRoundRect(healthBarX, barY + 10, healthBarWidth, healthBarHeight, 5, 5);

        g.setColor(Color.WHITE);
        g.drawString(health + "%", healthBarX + healthBarWidth + 8, barY + 20);
        
        // === BARRA SANITÀ MENTALE ===
        int sanityBarX = healthBarX + healthBarWidth + 60;
        int sanityBarWidth = 100;
        int sanityBarHeight = 12;
        
        g.setFont(GameFonts.retroBold(18f));
        g.setColor(new Color(150, 150, 255));
        g.drawString("Mente", sanityBarX - 42, barY + 20);
        
        // Sfondo barra sanità
        g.setColor(new Color(50, 50, 70));
        g.fillRoundRect(sanityBarX, barY + 10, sanityBarWidth, sanityBarHeight, 5, 5);
        
        // Riempimento barra sanità
        Color sanityColor;
        if (sanity > 70) sanityColor = new Color(100, 150, 255);
        else if (sanity > 40) sanityColor = new Color(255, 200, 100);
        else sanityColor = new Color(255, 100, 100);
        
        int fillWidth = (int) (sanityBarWidth * clampPercent(sanity) / 100.0);
        g.setColor(sanityColor);
        g.fillRoundRect(sanityBarX, barY + 10, fillWidth, sanityBarHeight, 5, 5);
        
        // Bordo barra
        g.setColor(new Color(100, 100, 150));
        g.drawRoundRect(sanityBarX, barY + 10, sanityBarWidth, sanityBarHeight, 5, 5);
        
        // Percentuale sanità
        g.setFont(GameFonts.retroBold(15f));
        g.setColor(Color.WHITE);
        g.drawString(sanity + "%", sanityBarX + sanityBarWidth/2 - 10, barY + 20);
        
        // === GIORNO E LOCATION ===
        g.setFont(GameFonts.retroBold(18f));
        g.setColor(new Color(255, 220, 100));
        g.drawString("Giorno " + day, sanityBarX + sanityBarWidth + 30, barY + 20);
        
        g.setColor(new Color(150, 200, 150));
        g.drawString(location, sanityBarX + sanityBarWidth + 120, barY + 20);
        
        // === ISTRUZIONI ===
        g.setFont(GameFonts.retroPlain(15f));
        String instructions = "[A/B/C] Scegli | [INVIO] Avanti";
        FontMetrics fm = g.getFontMetrics();
        int instWidth = fm.stringWidth(instructions);
        g.setColor(new Color(120, 120, 120));
        g.drawString(instructions, screenWidth - instWidth - 15, barY + 20);
    }
    
    private void renderLogo(Graphics2D g) {
        // Logo "LOST" stilizzato
        g.setColor(new Color(50, 60, 50));
        g.setFont(GameFonts.retroBold(18f));
        g.drawString("LOST", 10, 15);
    }

    private int parseFirstNumber(String text, int fallback) {
        if (text == null) return fallback;

        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return fallback;

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int clampPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }
    
    /** {@return il gestore delle immagini pixel art} */
    public PixelArtManager getPixelArtManager() {
        return pixelArtManager;
    }

    /** {@return l'altezza del box di testo in pixel} */
    public int getTextBoxHeight() { return textBoxHeight; }
    /** {@return la larghezza del box di testo in pixel} */
    public int getTextBoxWidth() { return textBoxWidth; }
    /** {@return la coordinata X del box di testo} */
    public int getTextBoxX() { return textBoxX; }
    /** {@return la coordinata Y del box di testo} */
    public int getTextBoxY() { return textBoxY; }
}
