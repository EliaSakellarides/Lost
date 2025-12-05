package com.lostthesis.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Genera immagini pixel art per le location dell'isola LOST
 * 
 * Ispirato ai classici giochi avventura:
 * - King's Quest (Sierra, 1984)
 * - Monkey Island (LucasArts, 1990)
 * - Myst (Cyan, 1993)
 * 
 * Tecniche usate:
 * - Palette limitata (stile 16/256 colori)
 * - Dithering per sfumature
 * - Forme geometriche base per composizione
 */
public class PixelArtManager {
    private Map<String, BufferedImage> imageCache;
    private int imageWidth;
    private int imageHeight;
    private Random random;
    private int pixelSize = 4; // Dimensione "pixel" visibile (retro look)
    
    // Effetti opzionali
    private boolean enableScanlines = false;
    private boolean enableCRTEffect = false;
    
    public PixelArtManager(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageCache = new HashMap<>();
        this.random = new Random(42); // Seed fisso per consistenza
        generateAllImages();
    }
    
    private void generateAllImages() {
        // Genera le immagini per ogni location principale
        imageCache.put("spiaggia", generateBeachCrash());
        imageCache.put("giungla", generateJungle());
        imageCache.put("botola", generateHatch());
        imageCache.put("villaggio", generateOthersVillage());
        imageCache.put("tempio", generateTemple());
        imageCache.put("roccianera", generateBlackRock());
        imageCache.put("faro", generateLighthouse());
        imageCache.put("pista", generateRunway());
        imageCache.put("default", generateDefaultIsland());
        
        // Scene intro (post-curarsi)
        imageCache.put("campo_base", generateCampBase());
        imageCache.put("esplorazione_giungla", generateJungleExploration());
        imageCache.put("notte_spiaggia", generateBeachNight());
        imageCache.put("fuoco_campo", generateCampfire());
        imageCache.put("relitto_ala", generateWingWreckage());
        imageCache.put("grotte", generateCaves());
        imageCache.put("cascata", generateWaterfall());
        imageCache.put("scogliera", generateCliff());
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
    
    // ==================== NUOVE SCENE POST-CURA ====================
    
    /**
     * Campo base sulla spiaggia con tende improvvisate
     */
    private BufferedImage generateCampBase() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo tramonto
        GradientPaint sky = new GradientPaint(0, 0, new Color(255, 150, 100),
            0, imageHeight / 2, new Color(255, 200, 150));
        g.setPaint(sky);
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Mare
        g.setColor(new Color(50, 100, 150));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 4);
        
        // Spiaggia
        g.setColor(new Color(230, 210, 170));
        g.fillRect(0, 3 * imageHeight / 4, imageWidth, imageHeight / 4);
        
        // Tende improvvisate (teloni blu/grigi)
        Color tarpBlue = new Color(60, 80, 140);
        Color tarpGray = new Color(100, 100, 110);
        
        // Tenda 1 (triangolare)
        int[] tent1X = {60, 100, 140};
        int[] tent1Y = {3 * imageHeight / 4, 3 * imageHeight / 4 - 50, 3 * imageHeight / 4};
        g.setColor(tarpBlue);
        g.fillPolygon(tent1X, tent1Y, 3);
        
        // Tenda 2
        int[] tent2X = {200, 240, 280};
        int[] tent2Y = {3 * imageHeight / 4, 3 * imageHeight / 4 - 45, 3 * imageHeight / 4};
        g.setColor(tarpGray);
        g.fillPolygon(tent2X, tent2Y, 3);
        
        // Tenda 3
        int[] tent3X = {imageWidth - 150, imageWidth - 110, imageWidth - 70};
        int[] tent3Y = {3 * imageHeight / 4, 3 * imageHeight / 4 - 55, 3 * imageHeight / 4};
        g.setColor(new Color(80, 120, 80)); // Verde militare
        g.fillPolygon(tent3X, tent3Y, 3);
        
        // Fuoco centrale
        drawCampfire(g, imageWidth / 2, 3 * imageHeight / 4 - 20);
        
        // Sopravvissuti intorno al fuoco
        drawPixelPerson(g, imageWidth / 2 - 60, 3 * imageHeight / 4 - 25, new Color(50, 50, 80));
        drawPixelPerson(g, imageWidth / 2 + 40, 3 * imageHeight / 4 - 25, new Color(150, 100, 80));
        drawPixelPerson(g, imageWidth / 2 - 20, 3 * imageHeight / 4 - 35, new Color(200, 180, 150));
        
        // Detriti sparsi
        g.setColor(new Color(120, 120, 130));
        for (int i = 0; i < 10; i++) {
            int dx = random.nextInt(imageWidth);
            int dy = 3 * imageHeight / 4 + random.nextInt(imageHeight / 5);
            g.fillRect(dx, dy, 5 + random.nextInt(10), 3);
        }
        
        // Palme
        drawPalmTree(g, 30, 3 * imageHeight / 4);
        drawPalmTree(g, imageWidth - 40, 3 * imageHeight / 4);
        
        // Relitto sullo sfondo (nell'acqua)
        g.setColor(new Color(100, 100, 110, 180));
        g.fillRect(imageWidth / 3, imageHeight / 2, 80, 30);
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Esplorazione nella giungla (sentiero)
     */
    private BufferedImage generateJungleExploration() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo giungla molto verde
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int green = 40 + random.nextInt(60);
                int red = 10 + random.nextInt(30);
                g.setColor(new Color(red, green, red / 2));
                g.fillRect(x, y, 1, 1);
            }
        }
        
        // Sentiero che va verso il centro (prospettiva)
        for (int y = imageHeight - 1; y > imageHeight / 3; y--) {
            float progress = (float)(imageHeight - y) / (imageHeight * 2 / 3);
            int pathWidth = (int)(80 * (1 - progress * 0.7));
            int pathX = imageWidth / 2 - pathWidth / 2;
            
            g.setColor(new Color(70 + random.nextInt(20), 50 + random.nextInt(15), 30));
            g.fillRect(pathX, y, pathWidth, 1);
        }
        
        // Tronchi ai lati
        drawJungleTrunk(g, 20, imageHeight);
        drawJungleTrunk(g, imageWidth - 30, imageHeight);
        drawJungleTrunk(g, 80, imageHeight);
        drawJungleTrunk(g, imageWidth - 90, imageHeight);
        
        // Foglie in primo piano (sovrapposte)
        drawJungleLeavesForeground(g);
        
        // Raggi di luce
        g.setColor(new Color(255, 255, 200, 30));
        for (int i = 0; i < 5; i++) {
            int rx = imageWidth / 4 + random.nextInt(imageWidth / 2);
            g.fillRect(rx, 0, 20, imageHeight / 2);
        }
        
        // Figure in lontananza (Jack che esplora)
        g.setColor(new Color(30, 30, 30));
        g.fillRect(imageWidth / 2 - 3, imageHeight / 2, 6, 15);
        g.fillOval(imageWidth / 2 - 4, imageHeight / 2 - 5, 8, 8);
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Spiaggia di notte
     */
    private BufferedImage generateBeachNight() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo notturno
        g.setColor(new Color(15, 20, 40));
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Stelle
        g.setColor(Color.WHITE);
        for (int i = 0; i < 50; i++) {
            int sx = random.nextInt(imageWidth);
            int sy = random.nextInt(imageHeight / 2);
            int size = random.nextInt(2) + 1;
            g.fillRect(sx, sy, size, size);
        }
        
        // Luna
        g.setColor(new Color(255, 255, 220));
        g.fillOval(imageWidth - 100, 30, 50, 50);
        // Crateri luna
        g.setColor(new Color(220, 220, 190));
        g.fillOval(imageWidth - 90, 45, 10, 10);
        g.fillOval(imageWidth - 70, 55, 8, 8);
        
        // Mare scuro con riflesso luna
        g.setColor(new Color(10, 30, 50));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 4);
        // Riflesso
        g.setColor(new Color(100, 120, 150, 100));
        g.fillRect(imageWidth - 120, imageHeight / 2, 60, imageHeight / 4);
        
        // Spiaggia scura
        g.setColor(new Color(60, 55, 45));
        g.fillRect(0, 3 * imageHeight / 4, imageWidth, imageHeight / 4);
        
        // Fuochi lontani
        for (int i = 0; i < 3; i++) {
            int fx = 100 + i * 150;
            drawCampfireGlow(g, fx, 3 * imageHeight / 4 - 10);
        }
        
        // Sagome di persone
        g.setColor(new Color(20, 20, 25));
        drawPixelPerson(g, 80, 3 * imageHeight / 4 - 15, new Color(20, 20, 25));
        drawPixelPerson(g, 250, 3 * imageHeight / 4 - 15, new Color(20, 20, 25));
        drawPixelPerson(g, imageWidth - 100, 3 * imageHeight / 4 - 15, new Color(20, 20, 25));
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Scena focolare (primo piano)
     */
    private BufferedImage generateCampfire() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo notte
        g.setColor(new Color(20, 25, 35));
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Bagliore arancione del fuoco
        RadialGradientPaint glow = new RadialGradientPaint(
            imageWidth / 2, imageHeight / 2 + 50,
            200,
            new float[]{0f, 0.5f, 1f},
            new Color[]{
                new Color(100, 60, 20),
                new Color(40, 25, 15),
                new Color(20, 25, 35)
            }
        );
        g.setPaint(glow);
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Terreno
        g.setColor(new Color(50, 40, 30));
        g.fillRect(0, imageHeight * 2 / 3, imageWidth, imageHeight / 3);
        
        // Cerchio di pietre
        g.setColor(new Color(80, 80, 90));
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            int rx = imageWidth / 2 + (int)(Math.cos(angle) * 60);
            int ry = imageHeight * 2 / 3 + (int)(Math.sin(angle) * 25);
            g.fillOval(rx - 8, ry - 5, 16, 10);
        }
        
        // Fuoco grande
        Color[] flames = {
            new Color(255, 200, 50),
            new Color(255, 150, 30),
            new Color(255, 100, 20),
            new Color(200, 50, 10)
        };
        for (int i = 0; i < 30; i++) {
            int fx = imageWidth / 2 + random.nextInt(40) - 20;
            int fy = imageHeight * 2 / 3 - random.nextInt(60);
            Color c = flames[random.nextInt(flames.length)];
            g.setColor(c);
            g.fillOval(fx - 5, fy - 5, 10, 15);
        }
        
        // Legna
        g.setColor(new Color(60, 40, 20));
        g.fillRect(imageWidth / 2 - 40, imageHeight * 2 / 3 - 10, 80, 12);
        g.fillRect(imageWidth / 2 - 30, imageHeight * 2 / 3 - 5, 10, 40);
        
        // Persone intorno (silhouette illuminate)
        drawFirelitPerson(g, imageWidth / 4, imageHeight * 2 / 3 - 30);
        drawFirelitPerson(g, imageWidth * 3 / 4, imageHeight * 2 / 3 - 30);
        drawFirelitPerson(g, imageWidth / 2 - 100, imageHeight * 2 / 3 - 25);
        
        // Scintille
        g.setColor(new Color(255, 200, 100));
        for (int i = 0; i < 20; i++) {
            int sx = imageWidth / 2 + random.nextInt(60) - 30;
            int sy = imageHeight / 3 + random.nextInt(imageHeight / 3);
            g.fillRect(sx, sy, 2, 2);
        }
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Ala del relitto sulla spiaggia
     */
    private BufferedImage generateWingWreckage() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo
        g.setColor(new Color(150, 180, 210));
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Spiaggia
        g.setColor(new Color(220, 200, 160));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 2);
        
        // Ala enorme in primo piano
        g.setColor(new Color(180, 180, 190));
        // Forma ala
        int[] wingX = {0, imageWidth / 3, imageWidth * 2 / 3, imageWidth};
        int[] wingY = {imageHeight / 3, imageHeight / 4, imageHeight / 3, imageHeight / 2};
        g.fillPolygon(wingX, wingY, 4);
        
        // Dettagli ala (pannelli)
        g.setColor(new Color(150, 150, 160));
        for (int i = 0; i < 5; i++) {
            int lx = i * imageWidth / 5;
            g.drawLine(lx, imageHeight / 3, lx + 50, imageHeight / 4);
        }
        
        // Motore
        g.setColor(new Color(100, 100, 110));
        g.fillOval(imageWidth / 3, imageHeight / 3, 80, 50);
        g.setColor(new Color(50, 50, 60));
        g.fillOval(imageWidth / 3 + 10, imageHeight / 3 + 10, 60, 30);
        
        // Fumo dal motore
        g.setColor(new Color(80, 80, 80, 150));
        for (int i = 0; i < 10; i++) {
            int sx = imageWidth / 3 + 40 + random.nextInt(30);
            int sy = imageHeight / 3 - 20 - i * 15;
            g.fillOval(sx, sy, 25, 20);
        }
        
        // Detriti
        g.setColor(new Color(120, 120, 130));
        for (int i = 0; i < 15; i++) {
            int dx = random.nextInt(imageWidth);
            int dy = imageHeight / 2 + random.nextInt(imageHeight / 3);
            g.fillRect(dx, dy, 8 + random.nextInt(15), 4);
        }
        
        // Valigie sparse
        Color[] luggageColors = {
            new Color(150, 50, 50), new Color(50, 50, 150),
            new Color(50, 100, 50), new Color(100, 100, 100)
        };
        for (int i = 0; i < 6; i++) {
            int lx = random.nextInt(imageWidth - 30);
            int ly = imageHeight / 2 + 50 + random.nextInt(imageHeight / 4);
            g.setColor(luggageColors[random.nextInt(luggageColors.length)]);
            g.fillRect(lx, ly, 25, 15);
        }
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Grotte misteriose
     */
    private BufferedImage generateCaves() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo grotta scuro
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int shade = 25 + random.nextInt(20);
                g.setColor(new Color(shade, shade - 5, shade - 10));
                g.fillRect(x, y, 1, 1);
            }
        }
        
        // Apertura grotta (luce dall'esterno)
        int openingX = imageWidth / 2;
        int openingY = imageHeight / 2 - 50;
        
        // Luce che entra
        RadialGradientPaint light = new RadialGradientPaint(
            openingX, openingY,
            150,
            new float[]{0f, 0.5f, 1f},
            new Color[]{
                new Color(180, 200, 180),
                new Color(80, 100, 80),
                new Color(30, 35, 25)
            }
        );
        g.setPaint(light);
        g.fillOval(openingX - 100, openingY - 80, 200, 160);
        
        // Vegetazione all'ingresso
        g.setColor(new Color(30, 80, 30));
        for (int i = 0; i < 20; i++) {
            int vx = openingX - 80 + random.nextInt(160);
            int vy = openingY - 60 + random.nextInt(40);
            g.fillRect(vx, vy, 5, 15 + random.nextInt(10));
        }
        
        // Stalattiti
        g.setColor(new Color(70, 65, 60));
        for (int i = 0; i < 20; i++) {
            int sx = random.nextInt(imageWidth);
            int sLen = 20 + random.nextInt(40);
            int[] stX = {sx - 5, sx, sx + 5};
            int[] stY = {0, sLen, 0};
            g.fillPolygon(stX, stY, 3);
        }
        
        // Stalagmiti
        for (int i = 0; i < 15; i++) {
            int sx = random.nextInt(imageWidth);
            int sLen = 15 + random.nextInt(30);
            int[] stX = {sx - 4, sx, sx + 4};
            int[] stY = {imageHeight, imageHeight - sLen, imageHeight};
            g.fillPolygon(stX, stY, 3);
        }
        
        // Pozza d'acqua
        g.setColor(new Color(40, 60, 80, 180));
        g.fillOval(imageWidth / 4, imageHeight - 80, imageWidth / 3, 40);
        
        // Riflesso nella pozza
        g.setColor(new Color(100, 120, 100, 100));
        g.fillOval(imageWidth / 4 + 20, imageHeight - 75, 30, 15);
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Cascata nella giungla
     */
    private BufferedImage generateWaterfall() {
        BufferedImage img = generateJungle(); // Base giungla
        Graphics2D g = img.createGraphics();
        
        // Roccia della cascata
        g.setColor(new Color(80, 70, 60));
        g.fillRect(imageWidth / 3, 0, imageWidth / 3, imageHeight * 2 / 3);
        
        // Texture roccia
        g.setColor(new Color(60, 55, 45));
        for (int i = 0; i < 50; i++) {
            int rx = imageWidth / 3 + random.nextInt(imageWidth / 3);
            int ry = random.nextInt(imageHeight * 2 / 3);
            g.fillRect(rx, ry, 10, 5);
        }
        
        // Cascata (acqua)
        for (int y = 0; y < imageHeight * 2 / 3; y++) {
            for (int x = imageWidth / 2 - 30; x < imageWidth / 2 + 30; x++) {
                int blue = 150 + random.nextInt(50);
                int alpha = 180 + random.nextInt(75);
                g.setColor(new Color(200, 220, 255, Math.min(255, alpha)));
                g.fillRect(x, y, 1, 1);
            }
        }
        
        // Schiuma alla base
        g.setColor(new Color(255, 255, 255, 200));
        for (int i = 0; i < 30; i++) {
            int fx = imageWidth / 2 - 50 + random.nextInt(100);
            int fy = imageHeight * 2 / 3 - 10 + random.nextInt(30);
            g.fillOval(fx, fy, 10, 8);
        }
        
        // Pozza
        g.setColor(new Color(60, 100, 120));
        g.fillOval(imageWidth / 4, imageHeight * 2 / 3, imageWidth / 2, imageHeight / 4);
        
        // Riflessi nell'acqua
        g.setColor(new Color(150, 180, 200, 100));
        for (int i = 0; i < 10; i++) {
            int rx = imageWidth / 4 + 20 + random.nextInt(imageWidth / 2 - 40);
            int ry = imageHeight * 2 / 3 + 20 + random.nextInt(imageHeight / 5);
            g.fillRect(rx, ry, 20, 3);
        }
        
        // Vegetazione ai lati
        g.setColor(new Color(30, 100, 30));
        for (int i = 0; i < 15; i++) {
            int vx = random.nextInt(imageWidth / 4);
            int vy = imageHeight / 3 + random.nextInt(imageHeight / 2);
            g.fillOval(vx, vy, 20, 15);
        }
        for (int i = 0; i < 15; i++) {
            int vx = imageWidth * 3 / 4 + random.nextInt(imageWidth / 4);
            int vy = imageHeight / 3 + random.nextInt(imageHeight / 2);
            g.fillOval(vx, vy, 20, 15);
        }
        
        g.dispose();
        return applyEffects(img);
    }
    
    /**
     * Scogliera con vista sull'oceano
     */
    private BufferedImage generateCliff() {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Cielo (alba/tramonto)
        GradientPaint sky = new GradientPaint(
            0, 0, new Color(255, 180, 100),
            0, imageHeight / 2, new Color(100, 150, 200)
        );
        g.setPaint(sky);
        g.fillRect(0, 0, imageWidth, imageHeight / 2);
        
        // Sole all'orizzonte
        g.setColor(new Color(255, 220, 150));
        g.fillOval(imageWidth / 2 - 40, imageHeight / 2 - 60, 80, 80);
        g.setColor(new Color(255, 255, 200));
        g.fillOval(imageWidth / 2 - 30, imageHeight / 2 - 50, 60, 60);
        
        // Oceano
        g.setColor(new Color(40, 80, 120));
        g.fillRect(0, imageHeight / 2, imageWidth, imageHeight / 4);
        
        // Riflesso del sole sull'acqua
        g.setColor(new Color(255, 200, 100, 100));
        for (int i = 0; i < 20; i++) {
            int rx = imageWidth / 2 - 30 + random.nextInt(60);
            int ry = imageHeight / 2 + random.nextInt(imageHeight / 4);
            g.fillRect(rx, ry, 5, 2);
        }
        
        // Scogliera
        g.setColor(new Color(70, 60, 50));
        int[] cliffX = {0, imageWidth / 3, imageWidth / 2, imageWidth};
        int[] cliffY = {imageHeight * 3 / 4, imageHeight * 3 / 4 - 50, imageHeight * 3 / 4, imageHeight};
        g.fillPolygon(cliffX, cliffY, 4);
        g.fillRect(0, imageHeight * 3 / 4, imageWidth, imageHeight / 4);
        
        // Texture scogliera
        g.setColor(new Color(50, 45, 35));
        for (int i = 0; i < 30; i++) {
            int rx = random.nextInt(imageWidth);
            int ry = imageHeight * 3 / 4 + random.nextInt(imageHeight / 4);
            g.fillRect(rx, ry, 15, 8);
        }
        
        // Vegetazione sulla scogliera
        g.setColor(new Color(40, 90, 40));
        for (int i = 0; i < 20; i++) {
            int vx = random.nextInt(imageWidth);
            int vy = imageHeight * 3 / 4 - 30 + random.nextInt(20);
            g.fillOval(vx, vy, 15, 10);
        }
        
        // Figura solitaria (guardando l'orizzonte)
        g.setColor(new Color(30, 30, 35));
        g.fillRect(imageWidth / 2 - 5, imageHeight * 3 / 4 - 60, 10, 35);
        g.fillOval(imageWidth / 2 - 7, imageHeight * 3 / 4 - 70, 14, 14);
        
        // Onde che si infrangono
        g.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 5; i++) {
            int wx = random.nextInt(imageWidth);
            g.fillRect(wx, imageHeight * 3 / 4 - 5, 30, 3);
        }
        
        g.dispose();
        return applyEffects(img);
    }
    
    // ==================== HELPER METHODS ====================
    
    private void drawCampfire(Graphics2D g, int x, int y) {
        // Cerchio pietre
        g.setColor(new Color(80, 80, 90));
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            int rx = x + (int)(Math.cos(angle) * 20);
            int ry = y + (int)(Math.sin(angle) * 10);
            g.fillOval(rx - 4, ry - 3, 8, 6);
        }
        
        // Fiamme
        Color[] flames = {
            new Color(255, 200, 50),
            new Color(255, 150, 30),
            new Color(255, 100, 20)
        };
        for (int i = 0; i < 8; i++) {
            int fx = x + random.nextInt(16) - 8;
            int fy = y - random.nextInt(20);
            g.setColor(flames[random.nextInt(flames.length)]);
            g.fillOval(fx - 3, fy - 3, 6, 10);
        }
    }
    
    private void drawCampfireGlow(Graphics2D g, int x, int y) {
        // Bagliore
        g.setColor(new Color(255, 150, 50, 50));
        g.fillOval(x - 30, y - 30, 60, 40);
        
        // Fiamme piccole
        g.setColor(new Color(255, 200, 100));
        for (int i = 0; i < 4; i++) {
            int fx = x + random.nextInt(10) - 5;
            int fy = y - random.nextInt(10);
            g.fillRect(fx, fy, 3, 5);
        }
    }
    
    private void drawPixelPerson(Graphics2D g, int x, int y, Color clothesColor) {
        // Testa
        g.setColor(new Color(220, 180, 150));
        g.fillOval(x - 4, y - 12, 8, 8);
        
        // Corpo
        g.setColor(clothesColor);
        g.fillRect(x - 4, y - 4, 8, 15);
        
        // Gambe
        g.setColor(new Color(50, 50, 80));
        g.fillRect(x - 3, y + 11, 3, 8);
        g.fillRect(x, y + 11, 3, 8);
    }
    
    private void drawFirelitPerson(Graphics2D g, int x, int y) {
        // Silhouette scura
        g.setColor(new Color(40, 35, 30));
        g.fillOval(x - 5, y - 12, 10, 10);
        g.fillRect(x - 5, y - 2, 10, 20);
        
        // Bordo illuminato dal fuoco
        g.setColor(new Color(150, 100, 50));
        g.drawOval(x - 5, y - 12, 10, 10);
        g.drawRect(x + 3, y - 2, 2, 20);
    }
    
    private void drawJungleTrunk(Graphics2D g, int x, int endY) {
        g.setColor(new Color(60, 40, 25));
        g.fillRect(x, 0, 12, endY);
        
        // Texture corteccia
        g.setColor(new Color(40, 30, 15));
        for (int i = 0; i < endY; i += 10) {
            g.drawLine(x, i, x + 12, i + 5);
        }
    }
    
    private void drawJungleLeavesForeground(Graphics2D g) {
        // Foglie grandi in primo piano (bordi)
        Color[] leafColors = {
            new Color(20, 80, 20),
            new Color(30, 100, 30),
            new Color(40, 120, 40)
        };
        
        // Lato sinistro
        for (int i = 0; i < 8; i++) {
            int lx = random.nextInt(imageWidth / 4);
            int ly = random.nextInt(imageHeight);
            g.setColor(leafColors[random.nextInt(leafColors.length)]);
            g.fillOval(lx, ly, 50, 30);
        }
        
        // Lato destro
        for (int i = 0; i < 8; i++) {
            int lx = imageWidth - random.nextInt(imageWidth / 4) - 50;
            int ly = random.nextInt(imageHeight);
            g.setColor(leafColors[random.nextInt(leafColors.length)]);
            g.fillOval(lx, ly, 50, 30);
        }
        
        // Top
        for (int i = 0; i < 5; i++) {
            int lx = random.nextInt(imageWidth);
            int ly = random.nextInt(imageHeight / 4);
            g.setColor(leafColors[random.nextInt(leafColors.length)]);
            g.fillOval(lx, ly, 40, 25);
        }
    }
    
    /**
     * Applica effetti retro opzionali (scanlines, CRT)
     */
    private BufferedImage applyEffects(BufferedImage img) {
        if (!enableScanlines && !enableCRTEffect) {
            return img;
        }
        
        BufferedImage result = new BufferedImage(
            img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(img, 0, 0, null);
        
        if (enableScanlines) {
            // Linee orizzontali ogni 2 pixel
            g.setColor(new Color(0, 0, 0, 30));
            for (int y = 0; y < img.getHeight(); y += 2) {
                g.drawLine(0, y, img.getWidth(), y);
            }
        }
        
        if (enableCRTEffect) {
            // Vignette ai bordi
            int w = img.getWidth();
            int h = img.getHeight();
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float distX = Math.abs(x - w / 2f) / (w / 2f);
                    float distY = Math.abs(y - h / 2f) / (h / 2f);
                    float dist = (float) Math.sqrt(distX * distX + distY * distY);
                    
                    if (dist > 0.7) {
                        int alpha = (int) ((dist - 0.7f) / 0.3f * 80);
                        g.setColor(new Color(0, 0, 0, Math.min(alpha, 80)));
                        g.fillRect(x, y, 1, 1);
                    }
                }
            }
        }
        
        g.dispose();
        return result;
    }
    
    // ==================== CONFIGURAZIONE ====================
    
    public void setEnableScanlines(boolean enable) {
        this.enableScanlines = enable;
        imageCache.clear();
        generateAllImages(); // Rigenera con nuovi effetti
    }
    
    public void setEnableCRTEffect(boolean enable) {
        this.enableCRTEffect = enable;
        imageCache.clear();
        generateAllImages();
    }
    
    public void setPixelSize(int size) {
        this.pixelSize = size;
        imageCache.clear();
        generateAllImages();
    }
    
    /**
     * Rigenera una singola immagine con effetti attuali
     */
    public BufferedImage regenerateImage(String key) {
        BufferedImage img = null;
        switch (key) {
            case "campo_base": img = generateCampBase(); break;
            case "esplorazione_giungla": img = generateJungleExploration(); break;
            case "notte_spiaggia": img = generateBeachNight(); break;
            case "fuoco_campo": img = generateCampfire(); break;
            case "relitto_ala": img = generateWingWreckage(); break;
            case "grotte": img = generateCaves(); break;
            case "cascata": img = generateWaterfall(); break;
            case "scogliera": img = generateCliff(); break;
            default: img = getImage(key);
        }
        if (img != null) {
            imageCache.put(key, img);
        }
        return img;
    }
}
