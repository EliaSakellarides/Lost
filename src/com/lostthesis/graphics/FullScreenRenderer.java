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
    
    public void render(Graphics2D g, String locationKey, String text, 
                       String title, String statusInfo) {
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
        
        // Box del testo
        renderTextBox(g, text, title);
        
        // Barra di stato in basso
        renderStatusBar(g, statusInfo);
        
        // Logo LOST in alto
        renderLogo(g);
    }
    
    private void renderTextBox(Graphics2D g, String text, String title) {
        // Sfondo semitrasparente
        g.setColor(TEXT_BOX_COLOR);
        g.fillRoundRect(textBoxX, textBoxY, textBoxWidth, textBoxHeight, 15, 15);
        
        // Bordo
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(textBoxX, textBoxY, textBoxWidth, textBoxHeight, 15, 15);
        
        // Titolo
        if (title != null && !title.isEmpty()) {
            g.setColor(TITLE_COLOR);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g.drawString(title, textBoxX + (textBoxWidth - titleWidth) / 2, textBoxY + 25);
        }
        
        // Testo principale
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Wrapping del testo
        int padding = 15;
        int startY = textBoxY + 45;
        int lineHeight = 16;
        int maxWidth = textBoxWidth - 2 * padding;
        
        FontMetrics fm = g.getFontMetrics();
        String[] lines = text.split("\n");
        int currentY = startY;
        
        for (String line : lines) {
            if (currentY > textBoxY + textBoxHeight - 20) break;
            
            // Controlla se è una linea speciale (scelte, suggerimenti)
            if (line.startsWith("🔘") || line.startsWith("💡")) {
                g.setColor(HIGHLIGHT_COLOR);
            } else if (line.startsWith("✅") || line.startsWith("❌")) {
                g.setColor(line.startsWith("✅") ? new Color(100, 255, 100) : new Color(255, 100, 100));
            } else {
                g.setColor(TEXT_COLOR);
            }
            
            // Word wrap
            if (fm.stringWidth(line) > maxWidth) {
                String[] words = line.split(" ");
                StringBuilder currentLine = new StringBuilder();
                
                for (String word : words) {
                    String test = currentLine.length() > 0 ? 
                        currentLine + " " + word : word;
                    
                    if (fm.stringWidth(test) > maxWidth) {
                        if (currentLine.length() > 0) {
                            g.drawString(currentLine.toString(), textBoxX + padding, currentY);
                            currentY += lineHeight;
                            currentLine = new StringBuilder(word);
                        } else {
                            g.drawString(word, textBoxX + padding, currentY);
                            currentY += lineHeight;
                        }
                    } else {
                        currentLine = new StringBuilder(test);
                    }
                }
                
                if (currentLine.length() > 0) {
                    g.drawString(currentLine.toString(), textBoxX + padding, currentY);
                    currentY += lineHeight;
                }
            } else {
                g.drawString(line, textBoxX + padding, currentY);
                currentY += lineHeight;
            }
        }
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
        
        // Testo status
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (statusInfo != null && !statusInfo.isEmpty()) {
            g.drawString(statusInfo, 20, barY + 30);
        }
        
        // Istruzioni a destra
        String instructions = "[A/B/C] Scegli  |  [AVANTI] Continua  |  [ESC] Esci";
        FontMetrics fm = g.getFontMetrics();
        int instWidth = fm.stringWidth(instructions);
        g.setColor(new Color(150, 150, 150));
        g.drawString(instructions, screenWidth - instWidth - 20, barY + 30);
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
