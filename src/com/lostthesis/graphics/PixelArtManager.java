package com.lostthesis.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Genera immagini pixel art per le location dell'isola LOST
 */
public class PixelArtManager {
    private Map<String, BufferedImage> imageCache;
    private int imageWidth;
    private int imageHeight;
    private Random random;
    
    public PixelArtManager(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageCache = new HashMap<>();
        this.random = new Random(42); // Seed fisso per consistenza
        generateAllImages();
    }
    
    private void generateAllImages() {
        // Genera le immagini per ogni location
        imageCache.put("spiaggia", generateBeachCrash());
        imageCache.put("giungla", generateJungle());
        imageCache.put("botola", generateHatch());
        imageCache.put("villaggio", generateOthersVillage());
        imageCache.put("tempio", generateTemple());
        imageCache.put("roccianera", generateBlackRock());
        imageCache.put("faro", generateLighthouse());
        imageCache.put("pista", generateRunway());
        imageCache.put("default", generateDefaultIsland());
    }
    
    public BufferedImage getImage(String locationKey) {
        return imageCache.getOrDefault(locationKey, imageCache.get("default"));
    }
    
    private BufferedImage generateBeachCrash() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo azzurro
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Mare
        g.setColor(new Color(0, 105, 148));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 4);
        
        // Spiaggia
        g.setColor(new Color(238, 214, 175));
        g.fillRect(0, 3 * imageHeight / 4, imageWidth, imageHeight / 4);
        
        // Sole
        g.setColor(new Color(255, 223, 0));
        g.fillOval(imageWidth - 80, 20, 60, 60);
        
        // Rottami aereo
        g.setColor(new Color(150, 150, 150));
        g.fillRect(imageWidth / 4, 3 * imageHeight / 4 - 40, 100, 50);
        g.fillOval(imageWidth / 4 + 80, 3 * imageHeight / 4 - 50, 40, 40);
        
        // Fumo
        g.setColor(new Color(100, 100, 100, 150));
        for (int i = 0; i < 5; i++) {
            int x = imageWidth / 4 + 40 + random.nextInt(30);
            int y = 3 * imageHeight / 4 - 60 - i * 20;
            g.fillOval(x, y, 20 + random.nextInt(10), 20 + random.nextInt(10));
        }
        
        // Palme
        drawPalmTree(g, 50, 3 * imageHeight / 4);
        drawPalmTree(g, imageWidth - 80, 3 * imageHeight / 4);
        
        // Persone piccole (sopravvissuti)
        g.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            int px = imageWidth / 2 + i * 30;
            g.fillRect(px, 3 * imageHeight / 4 - 15, 5, 15);
            g.fillOval(px - 1, 3 * imageHeight / 4 - 20, 7, 7);
        }
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateJungle() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo verde scuro
        g.setColor(new Color(20, 60, 20));
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Alberi
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(imageWidth);
            drawJungleTree(g, x, imageHeight - 20);
        }
        
        // Nebbia/Fumo nero (il Mostro!)
        g.setColor(new Color(30, 30, 30, 180));
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(imageWidth);
            int y = random.nextInt(imageHeight);
            g.fillOval(x, y, 40, 30);
        }
        
        // Terreno
        g.setColor(new Color(50, 30, 10));
        g.fillRect(0, imageHeight - 30, imageWidth, 30);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateHatch() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Interno bunker grigio
        g.setColor(new Color(80, 80, 80));
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Pavimento
        g.setColor(new Color(60, 60, 60));
        g.fillRect(0, 2 * imageHeight / 3, imageWidth, imageHeight / 3);
        
        // Logo DHARMA
        g.setColor(Color.WHITE);
        g.fillOval(imageWidth / 2 - 40, imageHeight / 4, 80, 80);
        g.setColor(Color.BLACK);
        g.drawOval(imageWidth / 2 - 40, imageHeight / 4, 80, 80);
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("DHARMA", imageWidth / 2 - 30, imageHeight / 4 + 45);
        
        // Computer
        g.setColor(new Color(30, 30, 30));
        g.fillRect(imageWidth / 4, imageHeight / 2, 100, 60);
        g.setColor(new Color(0, 200, 0)); // Schermo verde
        g.fillRect(imageWidth / 4 + 10, imageHeight / 2 + 10, 80, 30);
        
        // Numeri sullo schermo
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("4 8 15 16 23 42", imageWidth / 4 + 15, imageHeight / 2 + 30);
        
        // Pulsante rosso
        g.setColor(Color.RED);
        g.fillRect(imageWidth / 4 + 110, imageHeight / 2 + 20, 30, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 8));
        g.drawString("108", imageWidth / 4 + 113, imageHeight / 2 + 40);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateOthersVillage() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo
        g.setColor(new Color(200, 200, 220));
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Prato
        g.setColor(new Color(60, 130, 60));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 2);
        
        // Case bianche
        for (int i = 0; i < 3; i++) {
            int hx = 50 + i * 120;
            g.setColor(Color.WHITE);
            g.fillRect(hx, imageHeight / 2 - 40, 80, 60);
            g.setColor(new Color(139, 69, 19)); // Tetto marrone
            int[] xPoints = {hx - 10, hx + 40, hx + 90};
            int[] yPoints = {imageHeight / 2 - 40, imageHeight / 2 - 70, imageHeight / 2 - 40};
            g.fillPolygon(xPoints, yPoints, 3);
            // Porta
            g.setColor(new Color(100, 50, 20));
            g.fillRect(hx + 30, imageHeight / 2 - 10, 20, 30);
        }
        
        // Figura misteriosa (Ben)
        g.setColor(Color.BLACK);
        g.fillRect(imageWidth / 2 - 5, imageHeight / 2 + 20, 10, 30);
        g.fillOval(imageWidth / 2 - 7, imageHeight / 2 + 10, 14, 14);
        // Occhiali
        g.setColor(new Color(50, 50, 50));
        g.drawOval(imageWidth / 2 - 6, imageHeight / 2 + 13, 5, 4);
        g.drawOval(imageWidth / 2 + 1, imageHeight / 2 + 13, 5, 4);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateTemple() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo giungla
        g.setColor(new Color(30, 70, 30));
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Tempio di pietra
        g.setColor(new Color(120, 120, 100));
        g.fillRect(imageWidth / 4, imageHeight / 4, imageWidth / 2, imageHeight / 2);
        
        // Colonne
        g.setColor(new Color(100, 100, 80));
        g.fillRect(imageWidth / 4 + 20, imageHeight / 4, 20, imageHeight / 2);
        g.fillRect(imageWidth / 4 + imageWidth / 2 - 40, imageHeight / 4, 20, imageHeight / 2);
        
        // Ingresso scuro
        g.setColor(new Color(20, 20, 20));
        g.fillRect(imageWidth / 2 - 30, imageHeight / 2, 60, imageHeight / 4);
        
        // Simboli misteriosi
        g.setColor(new Color(200, 180, 100));
        g.setFont(new Font("Serif", Font.BOLD, 14));
        g.drawString("☥ ☯ ✡", imageWidth / 2 - 20, imageHeight / 4 + 30);
        
        // Vasca d'acqua
        g.setColor(new Color(50, 100, 100, 150));
        g.fillRect(imageWidth / 2 - 40, imageHeight - 60, 80, 30);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateBlackRock() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Giungla
        g.setColor(new Color(30, 60, 20));
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Nave (Black Rock)
        g.setColor(new Color(40, 25, 15)); // Legno scuro
        // Scafo
        int[] shipX = {imageWidth / 6, imageWidth / 6, 5 * imageWidth / 6, 5 * imageWidth / 6};
        int[] shipY = {imageHeight / 2, imageHeight - 50, imageHeight - 50, imageHeight / 2};
        g.fillPolygon(shipX, shipY, 4);
        
        // Alberi della nave
        g.setColor(new Color(60, 40, 20));
        g.fillRect(imageWidth / 3, imageHeight / 4, 10, imageHeight / 2);
        g.fillRect(2 * imageWidth / 3, imageHeight / 4, 10, imageHeight / 2);
        
        // Vele strappate
        g.setColor(new Color(200, 180, 150, 150));
        g.fillRect(imageWidth / 3 - 20, imageHeight / 4 + 20, 50, 40);
        g.fillRect(2 * imageWidth / 3 - 20, imageHeight / 4 + 20, 50, 40);
        
        // Catene
        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(3));
        g.drawLine(imageWidth / 4, imageHeight / 2 + 30, imageWidth / 4 + 50, imageHeight / 2 + 50);
        
        // Casse di dinamite
        g.setColor(Color.RED);
        g.fillRect(imageWidth / 2 - 20, imageHeight - 70, 40, 25);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 8));
        g.drawString("TNT", imageWidth / 2 - 10, imageHeight - 52);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateLighthouse() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo al tramonto
        GradientPaint sunset = new GradientPaint(0, 0, new Color(255, 150, 100),
            0, imageHeight, new Color(100, 50, 150));
        g.setPaint(sunset);
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Mare
        g.setColor(new Color(30, 50, 80));
        g.fillRect(0, 2 * imageHeight / 3, imageWidth, imageHeight / 3);
        
        // Scogliera
        g.setColor(new Color(80, 60, 40));
        g.fillRect(imageWidth / 4, imageHeight / 2, imageWidth / 2, imageHeight / 2);
        
        // Faro
        g.setColor(Color.WHITE);
        g.fillRect(imageWidth / 2 - 20, imageHeight / 4, 40, imageHeight / 2 - 30);
        g.setColor(Color.RED);
        g.fillRect(imageWidth / 2 - 20, imageHeight / 4, 40, 20);
        g.fillRect(imageWidth / 2 - 20, imageHeight / 4 + 60, 40, 20);
        
        // Luce del faro
        g.setColor(new Color(255, 255, 200, 150));
        int[] lightX = {imageWidth / 2, imageWidth / 2 - 100, imageWidth / 2 + 100};
        int[] lightY = {imageHeight / 4 - 10, 0, 0};
        g.fillPolygon(lightX, lightY, 3);
        
        // Cupola
        g.setColor(new Color(200, 200, 200));
        g.fillOval(imageWidth / 2 - 25, imageHeight / 4 - 20, 50, 30);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateRunway() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo
        g.setColor(new Color(150, 200, 255));
        g.fillRect(0, 0, imageWidth, imageHeight / 3);
        
        // Giungla sullo sfondo
        g.setColor(new Color(40, 100, 40));
        g.fillRect(0, imageHeight / 3, imageWidth, imageHeight / 6);
        
        // Pista
        g.setColor(new Color(60, 60, 60));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 3);
        
        // Linee pista
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{20, 20}, 0));
        g.drawLine(0, imageHeight / 2 + imageHeight / 6, imageWidth, imageHeight / 2 + imageHeight / 6);
        
        // Aereo Cessna
        g.setColor(Color.WHITE);
        // Fusoliera
        g.fillOval(imageWidth / 3, imageHeight / 2 + 20, 120, 40);
        // Ali
        g.fillRect(imageWidth / 3 + 40, imageHeight / 2 + 10, 40, 60);
        // Coda
        g.fillRect(imageWidth / 3 + 100, imageHeight / 2 + 15, 30, 20);
        // Elica
        g.setColor(new Color(50, 50, 50));
        g.fillRect(imageWidth / 3 - 5, imageHeight / 2 + 35, 10, 3);
        
        // Palme ai lati
        drawPalmTree(g, 20, imageHeight - 30);
        drawPalmTree(g, imageWidth - 50, imageHeight - 30);
        
        // Erba
        g.setColor(new Color(80, 140, 60));
        g.fillRect(0, 5 * imageHeight / 6, imageWidth, imageHeight / 6);
        
        g.dispose();
        return img;
    }
    
    private BufferedImage generateDefaultIsland() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Mare
        g.setColor(new Color(0, 100, 150));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 2);
        
        // Isola
        g.setColor(new Color(60, 120, 60));
        int[] islandX = {imageWidth / 4, imageWidth / 2, 3 * imageWidth / 4};
        int[] islandY = {imageHeight / 2, imageHeight / 3, imageHeight / 2};
        g.fillPolygon(islandX, islandY, 3);
        
        // Testo LOST
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.drawString("LOST", imageWidth / 2 - 50, imageHeight / 2 + 50);
        
        g.dispose();
        return img;
    }
    
    private void drawPalmTree(Graphics2D g, int x, int y) {
        // Tronco
        g.setColor(new Color(139, 90, 43));
        g.fillRect(x, y - 60, 10, 60);
        
        // Foglie
        g.setColor(new Color(34, 139, 34));
        for (int i = 0; i < 5; i++) {
            double angle = Math.PI / 6 + i * Math.PI / 10;
            int lx = (int) (x + 5 + Math.cos(angle) * 40);
            int ly = (int) (y - 60 - Math.sin(angle) * 20);
            g.fillOval(lx - 15, ly - 5, 30, 10);
        }
    }
    
    private void drawJungleTree(Graphics2D g, int x, int y) {
        // Tronco
        g.setColor(new Color(80, 50, 30));
        g.fillRect(x - 5, y - 80, 10, 80);
        
        // Chioma
        g.setColor(new Color(20 + random.nextInt(40), 80 + random.nextInt(40), 20));
        g.fillOval(x - 30, y - 120, 60, 50);
        g.fillOval(x - 25, y - 100, 50, 40);
    }
}
