package com.lostthesis.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renderer per la modalità fullscreen con grafica pixel art
 */
public class FullScreenRenderer {
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
    public static final Color BACKGROUND_COLOR = new Color(10, 15, 20);
    public static final Color TEXT_BOX_COLOR = new Color(20, 30, 40, 230);
    public static final Color BORDER_COLOR = new Color(100, 150, 100);
    public static final Color TEXT_COLOR = new Color(200, 220, 200);
    public static final Color TITLE_COLOR = new Color(255, 220, 100);
    public static final Color HIGHLIGHT_COLOR = new Color(100, 200, 100);
    
    public FullScreenRenderer(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        // Calcola dimensioni immagine
        this.imageWidth = (int) (width * 0.9);
        this.imageHeight = (int) (height * 0.5);
        
        // Calcola posizione text box
        this.textBoxWidth = (int) (width * 0.9);
        this.textBoxHeight = 280;
        this.textBoxX = (width - textBoxWidth) / 2;
        this.textBoxY = height - textBoxHeight - 80;
        
        // Inizializza il gestore delle immagini
        this.pixelArtManager = new PixelArtManager(imageWidth, imageHeight);
    }
    
    public void render(Graphics2D g, String locationKey, String statusInfo) {
        // Sfondo
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Immagine della location
        BufferedImage locationImage = pixelArtManager.getImage(locationKey);
        if (locationImage != null) {
            int imgX = (screenWidth - imageWidth) / 2;
            int imgY = 20;

            // Bordo immagine
            g.setColor(BORDER_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawRect(imgX - 3, imgY - 3, imageWidth + 6, imageHeight + 6);

            // Immagine
            g.drawImage(locationImage, imgX, imgY, null);
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

    public void updateLayout(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        this.imageWidth = (int) (width * 0.9);
        this.imageHeight = (int) (height * 0.5);

        this.textBoxWidth = (int) (width * 0.9);
        this.textBoxHeight = 280;
        this.textBoxX = (width - textBoxWidth) / 2;
        this.textBoxY = height - textBoxHeight - 80;
    }
    
    private void renderStatusBar(Graphics2D g, String statusInfo) {
        int barHeight = 50;
        int barY = screenHeight - barHeight;
        
        // Sfondo barra
        g.setColor(new Color(30, 40, 50));
        g.fillRect(0, barY, screenWidth, barHeight);
        
        // Linea superiore
        g.setColor(BORDER_COLOR);
        g.drawLine(0, barY, screenWidth, barY);
        
        // Parsing dello statusInfo per estrarre valori
        // Formato: "❤️ 85%  |  🧠 90%  |  📅 Giorno 1  |  📍 SPIAGGIA"
        int health = 100;
        int sanity = 100;
        int day = 1;
        String location = "";
        
        if (statusInfo != null && !statusInfo.isEmpty()) {
            try {
                // Estrai salute
                int hStart = statusInfo.indexOf("❤️") + 3;
                int hEnd = statusInfo.indexOf("%");
                if (hStart > 2 && hEnd > hStart) {
                    health = Integer.parseInt(statusInfo.substring(hStart, hEnd).trim());
                }
                // Estrai sanità
                int sStart = statusInfo.indexOf("🧠") + 3;
                int sEnd = statusInfo.indexOf("%", sStart);
                if (sStart > 2 && sEnd > sStart) {
                    sanity = Integer.parseInt(statusInfo.substring(sStart, sEnd).trim());
                }
                // Estrai giorno
                int dStart = statusInfo.indexOf("Giorno ") + 7;
                int dEnd = statusInfo.indexOf(" ", dStart);
                if (dStart > 6 && dEnd > dStart) {
                    day = Integer.parseInt(statusInfo.substring(dStart, dEnd).trim());
                }
                // Estrai location
                int lStart = statusInfo.indexOf("📍") + 3;
                if (lStart > 2) {
                    location = statusInfo.substring(lStart).trim();
                }
            } catch (Exception e) {
                // Ignora errori di parsing
            }
        }
        
        int xPos = 20;
        
        // === CUORICINI SALUTE ===
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        int totalHearts = 10;
        int fullHearts = health / 10;
        int halfHeart = (health % 10 >= 5) ? 1 : 0;
        
        for (int i = 0; i < totalHearts; i++) {
            if (i < fullHearts) {
                g.setColor(new Color(255, 80, 80)); // Cuore pieno - rosso
                g.drawString("❤", xPos + i * 18, barY + 22);
            } else if (i == fullHearts && halfHeart == 1) {
                g.setColor(new Color(255, 150, 150)); // Mezzo cuore
                g.drawString("💔", xPos + i * 18, barY + 22);
            } else {
                g.setColor(new Color(80, 80, 80)); // Cuore vuoto - grigio
                g.drawString("♡", xPos + i * 18, barY + 22);
            }
        }
        
        // Numero salute
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(255, 150, 150));
        g.drawString(health + "%", xPos + totalHearts * 18 + 5, barY + 20);
        
        // === BARRA SANITÀ MENTALE ===
        int sanityBarX = xPos + totalHearts * 18 + 60;
        int sanityBarWidth = 100;
        int sanityBarHeight = 12;
        
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(150, 150, 255));
        g.drawString("🧠", sanityBarX - 20, barY + 20);
        
        // Sfondo barra sanità
        g.setColor(new Color(50, 50, 70));
        g.fillRoundRect(sanityBarX, barY + 10, sanityBarWidth, sanityBarHeight, 5, 5);
        
        // Riempimento barra sanità
        Color sanityColor;
        if (sanity > 70) sanityColor = new Color(100, 150, 255);
        else if (sanity > 40) sanityColor = new Color(255, 200, 100);
        else sanityColor = new Color(255, 100, 100);
        
        int fillWidth = (int) (sanityBarWidth * sanity / 100.0);
        g.setColor(sanityColor);
        g.fillRoundRect(sanityBarX, barY + 10, fillWidth, sanityBarHeight, 5, 5);
        
        // Bordo barra
        g.setColor(new Color(100, 100, 150));
        g.drawRoundRect(sanityBarX, barY + 10, sanityBarWidth, sanityBarHeight, 5, 5);
        
        // Percentuale sanità
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        g.drawString(sanity + "%", sanityBarX + sanityBarWidth/2 - 10, barY + 20);
        
        // === GIORNO E LOCATION ===
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(255, 220, 100));
        g.drawString("📅 Giorno " + day, sanityBarX + sanityBarWidth + 30, barY + 20);
        
        g.setColor(new Color(150, 200, 150));
        g.drawString("📍 " + location, sanityBarX + sanityBarWidth + 120, barY + 20);
        
        // === ISTRUZIONI ===
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String instructions = "[A/B/C] Scegli | [INVIO] Avanti";
        FontMetrics fm = g.getFontMetrics();
        int instWidth = fm.stringWidth(instructions);
        g.setColor(new Color(120, 120, 120));
        g.drawString(instructions, screenWidth - instWidth - 15, barY + 20);
    }
    
    private void renderLogo(Graphics2D g) {
        // Logo "LOST THESIS" stilizzato
        g.setColor(new Color(50, 60, 50));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("✈ LOST THESIS", 10, 15);
    }
    
    public PixelArtManager getPixelArtManager() {
        return pixelArtManager;
    }
    
    public int getTextBoxHeight() { return textBoxHeight; }
    public int getTextBoxWidth() { return textBoxWidth; }
    public int getTextBoxX() { return textBoxX; }
    public int getTextBoxY() { return textBoxY; }
}
