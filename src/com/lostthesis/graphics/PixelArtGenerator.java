package com.lostthesis.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Generatore di Pixel Art procedurale per Lost Thesis
 * Ispirato ai classici:
 * - King's Quest (Sierra, 1984)
 * - Monkey Island (LucasArts, 1990)
 * - Myst (Cyan, 1993)
 * - The Secret of Monkey Island
 * 
 * Tecniche usate:
 * - Palette limitata (stile 16/256 colori)
 * - Dithering per sfumature
 * - Forme geometriche base per composizione
 * - Noise per texture naturali
 */
public class PixelArtGenerator {
    
    private Random random;
    private int pixelSize; // Dimensione "pixel" visibile (es. 4x4 pixel reali = 1 pixel retro)
    
    // Palette colori stile LOST (isola tropicale)
    public static final Color[] PALETTE_BEACH = {
        new Color(30, 20, 10),      // Nero/marrone scuro
        new Color(194, 178, 128),   // Sabbia chiara
        new Color(160, 140, 100),   // Sabbia scura
        new Color(34, 139, 34),     // Verde foresta
        new Color(0, 100, 0),       // Verde scuro
        new Color(135, 206, 235),   // Cielo azzurro
        new Color(0, 105, 148),     // Oceano
        new Color(0, 70, 110),      // Oceano profondo
        new Color(139, 90, 43),     // Tronco palma
        new Color(85, 55, 25),      // Legno scuro
        new Color(255, 140, 0),     // Arancione tramonto
        new Color(255, 69, 0),      // Rosso fuoco
        new Color(100, 100, 100),   // Metallo/relitto
        new Color(60, 60, 60),      // Metallo scuro
        new Color(255, 255, 255),   // Bianco
        new Color(255, 220, 180)    // Pelle chiara
    };
    
    // Palette per giungla
    public static final Color[] PALETTE_JUNGLE = {
        new Color(10, 30, 10),      // Verde molto scuro
        new Color(34, 85, 34),      // Verde scuro
        new Color(50, 120, 50),     // Verde medio
        new Color(80, 160, 80),     // Verde chiaro
        new Color(139, 90, 43),     // Marrone tronco
        new Color(85, 55, 25),      // Marrone scuro
        new Color(60, 40, 20),      // Terra
        new Color(40, 30, 15),      // Terra scura
        new Color(180, 180, 100),   // Luce tra foglie
        new Color(255, 255, 200),   // Bagliore
        new Color(100, 70, 40),     // Roccia
        new Color(70, 50, 30)       // Roccia scura
    };
    
    // Palette per aereo
    public static final Color[] PALETTE_PLANE = {
        new Color(200, 200, 210),   // Fusoliera chiara
        new Color(150, 150, 160),   // Fusoliera media
        new Color(100, 100, 110),   // Fusoliera scura
        new Color(50, 50, 60),      // Ombre
        new Color(30, 80, 150),     // Blu Oceanic Airlines
        new Color(255, 100, 50),    // Arancione fuoco
        new Color(255, 200, 100),   // Fiamme
        new Color(80, 80, 90),      // Metallo bruciato
        new Color(40, 40, 50),      // Fumo
        new Color(20, 20, 30),      // Nero
        new Color(139, 90, 43),     // Sedili
        new Color(200, 180, 160)    // Interni
    };
    
    public PixelArtGenerator() {
        this(4); // Default: 4x4 pixel reali = 1 pixel retro
    }
    
    public PixelArtGenerator(int pixelSize) {
        this.pixelSize = pixelSize;
        this.random = new Random(42); // Seed fisso per riproducibilità
    }
    
    /**
     * Genera scena: Spiaggia con relitto aereo
     * Stile: King's Quest / Monkey Island
     */
    public BufferedImage generateBeachCrashScene(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        int retW = width / pixelSize;
        int retH = height / pixelSize;
        
        // === CIELO CON TRAMONTO ===
        for (int y = 0; y < retH / 2; y++) {
            float ratio = (float) y / (retH / 2);
            Color skyColor = blendColors(
                new Color(255, 150, 80),  // Arancione alto
                new Color(135, 206, 250), // Azzurro basso
                ratio
            );
            // Aggiungi dithering
            if (y % 3 == 0 && random.nextFloat() > 0.7) {
                skyColor = darken(skyColor, 0.1f);
            }
            drawPixelRow(g, 0, y, retW, skyColor);
        }
        
        // === SOLE AL TRAMONTO ===
        int sunX = retW - retW / 4;
        int sunY = retH / 6;
        drawPixelCircle(g, sunX, sunY, 8, new Color(255, 200, 100));
        drawPixelCircle(g, sunX, sunY, 6, new Color(255, 220, 150));
        
        // === OCEANO ===
        for (int y = retH / 2; y < retH * 2 / 3; y++) {
            float ratio = (float) (y - retH / 2) / (retH / 6);
            Color oceanColor = blendColors(
                new Color(100, 180, 220),
                new Color(30, 100, 150),
                ratio
            );
            // Onde con dithering
            for (int x = 0; x < retW; x++) {
                Color c = oceanColor;
                if ((x + y) % 4 == 0) {
                    c = lighten(oceanColor, 0.15f);
                }
                if ((x + y * 2) % 7 == 0) {
                    c = darken(oceanColor, 0.1f);
                }
                setPixel(g, x, y, c);
            }
        }
        
        // === SPIAGGIA ===
        for (int y = retH * 2 / 3; y < retH; y++) {
            for (int x = 0; x < retW; x++) {
                Color sandColor = PALETTE_BEACH[1]; // Sabbia base
                // Texture sabbia
                if (random.nextFloat() > 0.85) {
                    sandColor = PALETTE_BEACH[2]; // Sabbia scura
                }
                if (random.nextFloat() > 0.95) {
                    sandColor = darken(PALETTE_BEACH[2], 0.2f); // Sassolini
                }
                setPixel(g, x, y, sandColor);
            }
        }
        
        // === PALME ===
        drawPalmTree(g, 5, retH * 2 / 3 - 2);
        drawPalmTree(g, retW - 8, retH * 2 / 3 - 3);
        
        // === RELITTO AEREO ===
        drawPlaneCrash(g, retW / 3, retH * 2 / 3 - 10, retW / 2, retH / 3);
        
        // === FUMO ===
        drawSmoke(g, retW / 2, retH / 3, 15);
        
        // === DETRITI SULLA SPIAGGIA ===
        drawDebris(g, retW, retH);
        
        // === SOPRAVVISSUTI (silhouette) ===
        drawSurvivorSilhouette(g, retW / 5, retH * 3 / 4);
        drawSurvivorSilhouette(g, retW / 2 + 5, retH * 3 / 4 + 2);
        
        g.dispose();
        return img;
    }
    
    /**
     * Genera scena: Interno aereo (finestrino)
     */
    public BufferedImage generatePlaneInterior(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        int retW = width / pixelSize;
        int retH = height / pixelSize;
        
        // Sfondo interno aereo
        for (int y = 0; y < retH; y++) {
            for (int x = 0; x < retW; x++) {
                Color c = PALETTE_PLANE[11]; // Interni beige
                if (y < retH / 10 || y > retH * 9 / 10) {
                    c = PALETTE_PLANE[2]; // Soffitto/pavimento
                }
                setPixel(g, x, y, c);
            }
        }
        
        // Finestrino ovale
        int winX = retW / 2;
        int winY = retH / 2;
        int winW = retW / 4;
        int winH = retH / 3;
        
        // Bordo finestrino
        drawPixelOval(g, winX - 1, winY - 1, winW + 2, winH + 2, PALETTE_PLANE[3]);
        
        // Vista cielo attraverso finestrino
        for (int y = winY - winH / 2; y < winY + winH / 2; y++) {
            for (int x = winX - winW / 2; x < winX + winW / 2; x++) {
                // Verifica se dentro l'ovale
                float dx = (float)(x - winX) / (winW / 2);
                float dy = (float)(y - winY) / (winH / 2);
                if (dx * dx + dy * dy < 1) {
                    float ratio = (float)(y - (winY - winH / 2)) / winH;
                    Color skyC = blendColors(
                        new Color(100, 150, 255),
                        new Color(200, 220, 255),
                        ratio
                    );
                    // Nuvole
                    if (random.nextFloat() > 0.85) {
                        skyC = new Color(255, 255, 255);
                    }
                    setPixel(g, x, y, skyC);
                }
            }
        }
        
        // Sedile in primo piano
        drawSeat(g, retW / 6, retH / 2, PALETTE_PLANE[10]);
        drawSeat(g, retW * 5 / 6, retH / 2, PALETTE_PLANE[10]);
        
        g.dispose();
        return img;
    }
    
    /**
     * Genera scena: Giungla fitta
     */
    public BufferedImage generateJungleScene(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        int retW = width / pixelSize;
        int retH = height / pixelSize;
        
        // Sfondo giungla - stratificato
        for (int y = 0; y < retH; y++) {
            for (int x = 0; x < retW; x++) {
                // Base verde scuro
                Color c = PALETTE_JUNGLE[0];
                
                // Variazioni casuali per texture
                float noise = noise2D(x * 0.1f, y * 0.1f);
                if (noise > 0.6) {
                    c = PALETTE_JUNGLE[1];
                } else if (noise > 0.3) {
                    c = PALETTE_JUNGLE[2];
                }
                
                // Raggi di luce che filtrano
                if (y < retH / 3 && random.nextFloat() > 0.92) {
                    c = PALETTE_JUNGLE[8]; // Luce
                }
                
                setPixel(g, x, y, c);
            }
        }
        
        // Tronchi d'albero in primo piano
        drawJungleTrunk(g, 3, 0, retH);
        drawJungleTrunk(g, retW - 5, 0, retH);
        drawJungleTrunk(g, retW / 3, retH / 3, retH);
        
        // Foglie in primo piano (layer)
        drawJungleLeaves(g, retW, retH);
        
        // Sentiero
        drawJunglePath(g, retW / 2, retH);
        
        g.dispose();
        return img;
    }
    
    /**
     * Genera scena: Occhio che si apre (stile LOST)
     */
    public BufferedImage generateEyeScene(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        int retW = width / pixelSize;
        int retH = height / pixelSize;
        
        // Sfondo pelle
        for (int y = 0; y < retH; y++) {
            for (int x = 0; x < retW; x++) {
                Color skinColor = new Color(200, 160, 140);
                if (random.nextFloat() > 0.9) {
                    skinColor = darken(skinColor, 0.05f);
                }
                setPixel(g, x, y, skinColor);
            }
        }
        
        // Occhio - posizione centrale
        int eyeX = retW / 2;
        int eyeY = retH / 2;
        int eyeW = retW / 3;
        int eyeH = retH / 4;
        
        // Palpebra superiore (ombra)
        drawPixelOval(g, eyeX, eyeY - 2, eyeW + 4, eyeH / 2, new Color(150, 120, 100));
        
        // Bianco dell'occhio
        drawPixelOval(g, eyeX, eyeY, eyeW, eyeH, new Color(250, 245, 240));
        
        // Iride (marrone)
        int irisR = Math.min(eyeW, eyeH) / 3;
        drawPixelCircle(g, eyeX, eyeY, irisR, new Color(120, 80, 40));
        drawPixelCircle(g, eyeX, eyeY, irisR - 2, new Color(90, 60, 30));
        
        // Pupilla
        drawPixelCircle(g, eyeX, eyeY, irisR / 2, new Color(10, 10, 10));
        
        // Riflesso
        setPixel(g, eyeX - irisR / 3, eyeY - irisR / 3, Color.WHITE);
        setPixel(g, eyeX - irisR / 3 + 1, eyeY - irisR / 3, Color.WHITE);
        
        // Ciglia (linee pixel)
        for (int i = -eyeW / 2; i < eyeW / 2; i += 2) {
            if (random.nextFloat() > 0.3) {
                setPixel(g, eyeX + i, eyeY - eyeH / 2 - 1, new Color(40, 30, 20));
                if (random.nextFloat() > 0.5) {
                    setPixel(g, eyeX + i, eyeY - eyeH / 2 - 2, new Color(40, 30, 20));
                }
            }
        }
        
        g.dispose();
        return img;
    }
    
    /**
     * Genera scena: Vincent il cane
     */
    public BufferedImage generateVincentScene(int width, int height) {
        BufferedImage img = generateJungleScene(width, height);
        Graphics2D g = img.createGraphics();
        
        int retW = width / pixelSize;
        int retH = height / pixelSize;
        
        // Disegna Vincent (Labrador dorato stilizzato)
        int dogX = retW / 2;
        int dogY = retH * 2 / 3;
        
        // Corpo
        Color furColor = new Color(220, 180, 100);
        Color furDark = new Color(180, 140, 70);
        
        // Corpo ovale
        for (int dy = -4; dy <= 4; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                if (dx * dx / 36.0 + dy * dy / 16.0 < 1) {
                    setPixel(g, dogX + dx, dogY + dy, 
                        random.nextFloat() > 0.7 ? furDark : furColor);
                }
            }
        }
        
        // Testa
        for (int dy = -3; dy <= 2; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                if (dx * dx + dy * dy < 10) {
                    setPixel(g, dogX + 7 + dx, dogY - 2 + dy, furColor);
                }
            }
        }
        
        // Orecchie
        setPixel(g, dogX + 5, dogY - 5, furDark);
        setPixel(g, dogX + 9, dogY - 5, furDark);
        setPixel(g, dogX + 5, dogY - 4, furDark);
        setPixel(g, dogX + 9, dogY - 4, furDark);
        
        // Occhi
        setPixel(g, dogX + 6, dogY - 2, new Color(50, 30, 10));
        setPixel(g, dogX + 8, dogY - 2, new Color(50, 30, 10));
        
        // Naso
        setPixel(g, dogX + 10, dogY, new Color(30, 20, 10));
        
        // Coda
        setPixel(g, dogX - 7, dogY - 2, furColor);
        setPixel(g, dogX - 8, dogY - 3, furColor);
        
        // Zampe
        setPixel(g, dogX - 4, dogY + 5, furDark);
        setPixel(g, dogX - 2, dogY + 5, furDark);
        setPixel(g, dogX + 2, dogY + 5, furDark);
        setPixel(g, dogX + 4, dogY + 5, furDark);
        
        g.dispose();
        return img;
    }
    
    // ==================== HELPER METHODS ====================
    
    private void setPixel(Graphics2D g, int x, int y, Color c) {
        g.setColor(c);
        g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
    }
    
    private void drawPixelRow(Graphics2D g, int x, int y, int width, Color c) {
        g.setColor(c);
        g.fillRect(x * pixelSize, y * pixelSize, width * pixelSize, pixelSize);
    }
    
    private void drawPixelCircle(Graphics2D g, int cx, int cy, int r, Color c) {
        for (int y = -r; y <= r; y++) {
            for (int x = -r; x <= r; x++) {
                if (x * x + y * y <= r * r) {
                    setPixel(g, cx + x, cy + y, c);
                }
            }
        }
    }
    
    private void drawPixelOval(Graphics2D g, int cx, int cy, int w, int h, Color c) {
        for (int y = -h / 2; y <= h / 2; y++) {
            for (int x = -w / 2; x <= w / 2; x++) {
                float dx = (float) x / (w / 2);
                float dy = (float) y / (h / 2);
                if (dx * dx + dy * dy <= 1) {
                    setPixel(g, cx + x, cy + y, c);
                }
            }
        }
    }
    
    private void drawPalmTree(Graphics2D g, int x, int y) {
        // Tronco
        Color trunk = PALETTE_BEACH[8];
        Color trunkDark = PALETTE_BEACH[9];
        for (int i = 0; i < 15; i++) {
            setPixel(g, x, y + i, i % 2 == 0 ? trunk : trunkDark);
            setPixel(g, x + 1, y + i, i % 2 == 0 ? trunkDark : trunk);
        }
        
        // Foglie
        Color leaf = PALETTE_BEACH[3];
        Color leafDark = PALETTE_BEACH[4];
        int[] leafDirs = {-4, -3, -2, 2, 3, 4};
        for (int dir : leafDirs) {
            for (int i = 0; i < 6; i++) {
                int lx = x + (dir > 0 ? i : -i) + (dir > 0 ? 1 : 0);
                int ly = y - Math.abs(i - 3);
                setPixel(g, lx, ly, random.nextFloat() > 0.3 ? leaf : leafDark);
            }
        }
    }
    
    private void drawPlaneCrash(Graphics2D g, int x, int y, int w, int h) {
        // Fusoliera spezzata
        Color metal = PALETTE_PLANE[1];
        Color metalDark = PALETTE_PLANE[2];
        Color metalBurnt = PALETTE_PLANE[7];
        
        // Sezione principale
        for (int dy = 0; dy < h / 2; dy++) {
            for (int dx = 0; dx < w; dx++) {
                if (dy > 0 && dy < h / 2 - 1) {
                    Color c = metal;
                    if (dx < w / 4 || dx > w * 3 / 4) {
                        c = metalDark; // Ombre ai lati
                    }
                    // Danni e bruciature
                    if (random.nextFloat() > 0.85) {
                        c = metalBurnt;
                    }
                    setPixel(g, x + dx, y + dy, c);
                }
            }
        }
        
        // Finestrini
        for (int i = 0; i < 5; i++) {
            int wx = x + w / 6 + i * w / 7;
            setPixel(g, wx, y + h / 4, new Color(100, 150, 200));
            setPixel(g, wx + 1, y + h / 4, new Color(100, 150, 200));
        }
        
        // Logo Oceanic (semplificato)
        setPixel(g, x + w / 2, y + h / 3, PALETTE_PLANE[4]);
        setPixel(g, x + w / 2 + 1, y + h / 3, PALETTE_PLANE[4]);
        setPixel(g, x + w / 2, y + h / 3 + 1, PALETTE_PLANE[4]);
    }
    
    private void drawSmoke(Graphics2D g, int x, int y, int size) {
        Color[] smokeColors = {
            new Color(60, 60, 60),
            new Color(80, 80, 80),
            new Color(100, 100, 100),
            new Color(120, 120, 120)
        };
        
        for (int i = 0; i < size * 3; i++) {
            int sx = x + (int)(Math.sin(i * 0.5) * (size / 3));
            int sy = y - i;
            int r = 3 + (i / 5);
            Color c = smokeColors[i % smokeColors.length];
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 
                Math.max(50, 200 - i * 10));
            
            for (int dy = -r; dy <= r; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    if (dx * dx + dy * dy < r * r && random.nextFloat() > 0.3) {
                        setPixel(g, sx + dx, sy + dy, c);
                    }
                }
            }
        }
    }
    
    private void drawDebris(Graphics2D g, int retW, int retH) {
        Color[] debrisColors = {
            PALETTE_PLANE[2], PALETTE_PLANE[3], 
            PALETTE_BEACH[8], new Color(80, 80, 90)
        };
        
        for (int i = 0; i < 15; i++) {
            int dx = random.nextInt(retW);
            int dy = retH * 2 / 3 + random.nextInt(retH / 4);
            Color c = debrisColors[random.nextInt(debrisColors.length)];
            setPixel(g, dx, dy, c);
            if (random.nextFloat() > 0.5) {
                setPixel(g, dx + 1, dy, darken(c, 0.2f));
            }
        }
    }
    
    private void drawSurvivorSilhouette(Graphics2D g, int x, int y) {
        Color body = new Color(40, 30, 20);
        // Testa
        setPixel(g, x, y - 4, body);
        setPixel(g, x + 1, y - 4, body);
        // Corpo
        for (int i = -3; i <= 0; i++) {
            setPixel(g, x, y + i, body);
            setPixel(g, x + 1, y + i, body);
        }
        // Gambe
        setPixel(g, x, y + 1, body);
        setPixel(g, x + 1, y + 1, body);
    }
    
    private void drawJungleTrunk(Graphics2D g, int x, int startY, int endY) {
        for (int y = startY; y < endY; y++) {
            Color c = PALETTE_JUNGLE[4];
            if (random.nextFloat() > 0.7) {
                c = PALETTE_JUNGLE[5];
            }
            setPixel(g, x, y, c);
            setPixel(g, x + 1, y, darken(c, 0.2f));
        }
    }
    
    private void drawJungleLeaves(Graphics2D g, int retW, int retH) {
        for (int i = 0; i < 50; i++) {
            int lx = random.nextInt(retW);
            int ly = random.nextInt(retH / 2);
            Color c = PALETTE_JUNGLE[random.nextInt(4)];
            setPixel(g, lx, ly, c);
            if (random.nextFloat() > 0.5) {
                setPixel(g, lx + 1, ly, c);
            }
        }
    }
    
    private void drawJunglePath(Graphics2D g, int centerX, int retH) {
        for (int y = retH * 2 / 3; y < retH; y++) {
            int pathWidth = 3 + (y - retH * 2 / 3) / 3;
            for (int dx = -pathWidth; dx <= pathWidth; dx++) {
                Color c = PALETTE_JUNGLE[6]; // Terra
                if (random.nextFloat() > 0.8) {
                    c = PALETTE_JUNGLE[7];
                }
                setPixel(g, centerX + dx, y, c);
            }
        }
    }
    
    private void drawSeat(Graphics2D g, int x, int y, Color c) {
        // Schienale
        for (int dy = -8; dy < 0; dy++) {
            setPixel(g, x, y + dy, c);
            setPixel(g, x + 1, y + dy, darken(c, 0.1f));
            setPixel(g, x + 2, y + dy, c);
        }
        // Seduta
        for (int dx = -1; dx <= 3; dx++) {
            setPixel(g, x + dx, y, c);
            setPixel(g, x + dx, y + 1, darken(c, 0.2f));
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    private Color blendColors(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    }
    
    private Color lighten(Color c, float amount) {
        return blendColors(c, Color.WHITE, amount);
    }
    
    private Color darken(Color c, float amount) {
        return blendColors(c, Color.BLACK, amount);
    }
    
    /**
     * Perlin-like noise semplificato
     */
    private float noise2D(float x, float y) {
        int xi = (int) x;
        int yi = (int) y;
        float xf = x - xi;
        float yf = y - yi;
        
        float n00 = pseudoRandom(xi, yi);
        float n10 = pseudoRandom(xi + 1, yi);
        float n01 = pseudoRandom(xi, yi + 1);
        float n11 = pseudoRandom(xi + 1, yi + 1);
        
        float nx0 = lerp(n00, n10, xf);
        float nx1 = lerp(n01, n11, xf);
        
        return lerp(nx0, nx1, yf);
    }
    
    private float pseudoRandom(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / (float) 0x7fffffff;
    }
    
    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
    
    // ==================== MAIN TEST ====================
    
    public static void main(String[] args) {
        PixelArtGenerator gen = new PixelArtGenerator(4);
        
        // Test: genera e salva immagini
        try {
            BufferedImage beach = gen.generateBeachCrashScene(800, 600);
            javax.imageio.ImageIO.write(beach, "PNG", 
                new java.io.File("assets/images/gen_beach_crash.png"));
            
            BufferedImage plane = gen.generatePlaneInterior(800, 600);
            javax.imageio.ImageIO.write(plane, "PNG", 
                new java.io.File("assets/images/gen_plane_interior.png"));
            
            BufferedImage jungle = gen.generateJungleScene(800, 600);
            javax.imageio.ImageIO.write(jungle, "PNG", 
                new java.io.File("assets/images/gen_jungle.png"));
            
            BufferedImage eye = gen.generateEyeScene(800, 600);
            javax.imageio.ImageIO.write(eye, "PNG", 
                new java.io.File("assets/images/gen_eye.png"));
            
            BufferedImage vincent = gen.generateVincentScene(800, 600);
            javax.imageio.ImageIO.write(vincent, "PNG", 
                new java.io.File("assets/images/gen_vincent.png"));
            
            System.out.println("✅ Immagini pixel art generate con successo!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
