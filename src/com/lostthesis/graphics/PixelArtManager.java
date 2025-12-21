package com.lostthesis.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Manager per il caricamento e la gestione delle immagini del gioco.
 * Carica le immagini da file (assets/images/) invece di generarle.
 */
public class PixelArtManager {
    
    private Map<String, BufferedImage> imageCache;
    private int imageWidth;
    private int imageHeight;
    private String assetsPath;
    
    // Mappatura location -> nome file immagine
    private static final Map<String, String> IMAGE_FILES = new HashMap<>();
    
    static {
        // Scene intro (già create con AI)
        IMAGE_FILES.put("hostess", "hostess.png");
        IMAGE_FILES.put("aereo_iniziale", "areo scena iniziale.jpg");
        IMAGE_FILES.put("turbolenza", "aereotrema.jpg");
        IMAGE_FILES.put("aereo_rompe", "aereo si rompe in volo.jpg");
        IMAGE_FILES.put("crash", "aereo distrutto su spiaggia.jpg");
        IMAGE_FILES.put("occhio", "occhio aperto -2.jpg");
        IMAGE_FILES.put("risveglio", "risveglio in giungla.jpg");
        IMAGE_FILES.put("vincent", "segui vincent.jpg");
        IMAGE_FILES.put("aiuto_sopravvissuti", "aiuto spravvissuti.jpg");
        IMAGE_FILES.put("curare_feriti", "curare feriti.jpg");
        IMAGE_FILES.put("curarsi_alcol", "curarsi con alchol.jpg");
        
        // Location principali del gioco (da creare con AI)
        IMAGE_FILES.put("spiaggia", "spiaggia.jpg");
        IMAGE_FILES.put("giungla", "giungla.jpg");
        IMAGE_FILES.put("botola", "botola.jpg");
        IMAGE_FILES.put("grotte", "grotte.jpg");
        IMAGE_FILES.put("campo", "campo.jpg");
        IMAGE_FILES.put("villaggio", "villaggio.jpg");
        IMAGE_FILES.put("tempio", "tempio.jpg");
        IMAGE_FILES.put("roccianera", "roccianera.jpg");
        IMAGE_FILES.put("faro", "faro.jpg");
        IMAGE_FILES.put("pista", "pista.jpg");
        
        // Scene Giorno 1 (da creare con AI)
        IMAGE_FILES.put("giorno1_mattino", "giorno1_mattino.jpg");
        IMAGE_FILES.put("riunione", "riunione.jpg");
        IMAGE_FILES.put("esplorazione", "esplorazione.jpg");
        IMAGE_FILES.put("prima_notte", "prima_notte.jpg");
        IMAGE_FILES.put("mostro_fumo", "mostro_fumo.jpg");
        
        // ═══════════════════════════════════════════════════════════
        // IMMAGINI CAPITOLI - MAPPATE ALLE IMMAGINI ESISTENTI
        // ═══════════════════════════════════════════════════════════
        IMAGE_FILES.put("cap1_crash", "aereo distrutto su spiaggia.jpg");     // 1. Lo Schianto
        IMAGE_FILES.put("cap2_survivors", "campo sopravvissuti.png");          // 2. I Sopravvissuti
        IMAGE_FILES.put("cap3_smoke", "mostro di fumo.jpg");                   // 3. Il Mostro di Fumo
        IMAGE_FILES.put("cap4_caves", "scoperta grotte.jpg");                  // 4. Le Grotte
        IMAGE_FILES.put("cap5_hunt", "caccia al cignhiale.jpg");               // 5. La Caccia
        IMAGE_FILES.put("cap6_hatch_discovery", "botola.jpg");                 // 6. Scoperta Botola
        IMAGE_FILES.put("cap7_blackrock", "black rock in lontananza.jpg");     // 7. La Roccia Nera
        IMAGE_FILES.put("cap8_open_hatch", "dinamite alla botola.png");        // 8. Aprire la Botola
        IMAGE_FILES.put("cap9_swan", "desmond .jpg");                          // 9. Il Cigno (Desmond)
        IMAGE_FILES.put("cap10_others", "arrivano gli altri per l agguaot.jpg"); // 10. Gli Altri
        IMAGE_FILES.put("cap11_flashback", "hostess.png");                     // 11. Flashback (aeroporto)
        IMAGE_FILES.put("cap12_raft", "spari e fuoco sulla spoaggia.jpg");     // 12. La Zattera
        IMAGE_FILES.put("cap13_thesis", "terminale numero.jpg");               // 13. La Scoperta
        IMAGE_FILES.put("cap14_runway", "l isola è nostra.jpg");               // 14. La Pista
        IMAGE_FILES.put("cap15_prep", "trasporto dinamite nella giugnla.png"); // 15. Preparazione
        IMAGE_FILES.put("cap16_escape", "scappando fumo nero prima degli altri.jpg"); // 16. La Fuga
        IMAGE_FILES.put("cap17_freedom", "circondati.jpg");                    // 17. Libertà (da sostituire)
        
        // Immagini extra disponibili
        IMAGE_FILES.put("black_rock_interno", "black rock da vicnino .jpg");
        IMAGE_FILES.put("interno_botola", "inteerno botola.jpg");
        IMAGE_FILES.put("discesa_botola", "discesa botola.jpg");
        IMAGE_FILES.put("scoperta_dinamite", "scoperta dinamite.jpg");
        IMAGE_FILES.put("cattura", "cattura del prigionieor.jpg");
        IMAGE_FILES.put("prigioniero_giungla", "giungla con prigionerio .jpg");
        IMAGE_FILES.put("scambio_prigioniero", "scambio prigioneiro .jpg");
        IMAGE_FILES.put("riunione_altri", "riunione per scoprire qualcosa sugli altri .jpg");
        IMAGE_FILES.put("barracks", "arrivo alle barracks.jpg");
        IMAGE_FILES.put("birre_blackrock", "traporto birre nelle casse dentro la black rock.png");
    }
    
    public PixelArtManager(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageCache = new HashMap<>();
        this.assetsPath = "assets/images/";
        
        // Pre-carica le immagini esistenti
        preloadImages();
    }
    
    /**
     * Pre-carica tutte le immagini disponibili
     */
    private void preloadImages() {
        for (Map.Entry<String, String> entry : IMAGE_FILES.entrySet()) {
            String key = entry.getKey();
            String filename = entry.getValue();
            BufferedImage img = loadImageFromFile(filename);
            if (img != null) {
                imageCache.put(key, img);
            }
        }
        System.out.println("🖼️ Caricate " + imageCache.size() + " immagini da assets/images/");
    }
    
    /**
     * Carica un'immagine da file
     */
    private BufferedImage loadImageFromFile(String filename) {
        try {
            File file = new File(assetsPath + filename);
            if (file.exists()) {
                BufferedImage original = ImageIO.read(file);
                // Ridimensiona all'aspect ratio corretto
                return resizeImage(original, imageWidth, imageHeight);
            }
        } catch (IOException e) {
            // Silently ignore - will use placeholder
        }
        return null;
    }
    
    /**
     * Ridimensiona un'immagine mantenendo l'aspect ratio
     */
    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resized;
    }
    
    /**
     * Ottiene un'immagine per una location.
     * Se non esiste, restituisce un placeholder.
     */
    public BufferedImage getImage(String locationKey) {
        // Controlla se è già in cache
        if (imageCache.containsKey(locationKey)) {
            return imageCache.get(locationKey);
        }
        
        // Prova a caricare da file
        String filename = IMAGE_FILES.get(locationKey);
        if (filename != null) {
            BufferedImage img = loadImageFromFile(filename);
            if (img != null) {
                imageCache.put(locationKey, img);
                return img;
            }
        }
        
        // Restituisci placeholder se non trovata
        return createPlaceholder(locationKey);
    }
    
    /**
     * Crea un'immagine placeholder per location mancanti
     */
    private BufferedImage createPlaceholder(String locationKey) {
        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Sfondo gradiente basato sulla location
        Color topColor, bottomColor;
        switch (locationKey) {
            case "giungla":
            case "esplorazione":
                topColor = new Color(20, 60, 20);
                bottomColor = new Color(40, 100, 40);
                break;
            case "spiaggia":
            case "crash":
            case "campo":
                topColor = new Color(135, 206, 235);
                bottomColor = new Color(238, 214, 175);
                break;
            case "botola":
            case "grotte":
                topColor = new Color(40, 40, 50);
                bottomColor = new Color(60, 60, 70);
                break;
            case "prima_notte":
                topColor = new Color(15, 20, 40);
                bottomColor = new Color(30, 35, 50);
                break;
            default:
                topColor = new Color(50, 80, 120);
                bottomColor = new Color(100, 130, 160);
        }
        
        // Gradiente
        GradientPaint gradient = new GradientPaint(
            0, 0, topColor,
            0, imageHeight, bottomColor
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Testo location
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        String text = locationKey.toUpperCase().replace("_", " ");
        FontMetrics fm = g.getFontMetrics();
        int textX = (imageWidth - fm.stringWidth(text)) / 2;
        int textY = imageHeight / 2;
        g.drawString(text, textX, textY);
        
        // Nota per creare immagine
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        String note = "(Immagine da creare)";
        int noteX = (imageWidth - g.getFontMetrics().stringWidth(note)) / 2;
        g.drawString(note, noteX, textY + 30);
        
        g.dispose();
        
        // Salva in cache per riuso
        imageCache.put(locationKey, img);
        return img;
    }
    
    /**
     * Aggiunge manualmente un'immagine alla cache
     */
    public void addImage(String key, BufferedImage image) {
        imageCache.put(key, resizeImage(image, imageWidth, imageHeight));
    }
    
    /**
     * Ricarica un'immagine da file
     */
    public void reloadImage(String key) {
        String filename = IMAGE_FILES.get(key);
        if (filename != null) {
            BufferedImage img = loadImageFromFile(filename);
            if (img != null) {
                imageCache.put(key, img);
            }
        }
    }
    
    /**
     * Ricarica tutte le immagini
     */
    public void reloadAllImages() {
        imageCache.clear();
        preloadImages();
    }
    
    /**
     * Verifica se un'immagine esiste
     */
    public boolean hasImage(String key) {
        return imageCache.containsKey(key) || 
               (IMAGE_FILES.containsKey(key) && 
                new File(assetsPath + IMAGE_FILES.get(key)).exists());
    }
    
    /**
     * Lista le immagini mancanti
     */
    public void printMissingImages() {
        System.out.println("\n📷 IMMAGINI MANCANTI (da creare con AI):");
        System.out.println("=========================================");
        for (Map.Entry<String, String> entry : IMAGE_FILES.entrySet()) {
            String filename = entry.getValue();
            File file = new File(assetsPath + filename);
            if (!file.exists()) {
                System.out.println("  ❌ " + entry.getKey() + " -> " + filename);
            }
        }
        System.out.println("=========================================\n");
    }
    
    /**
     * Imposta il percorso degli assets
     */
    public void setAssetsPath(String path) {
        this.assetsPath = path;
        reloadAllImages();
    }
}
