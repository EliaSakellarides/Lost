package com.lost.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Manager per il caricamento e la gestione delle immagini del gioco.
 * Carica le immagini da src/main/resources/images tramite classpath.
 */
public class PixelArtManager {
    
    private Map<String, BufferedImage> imageCache;
    private int imageWidth;
    private int imageHeight;
    
    // Mappatura location -> nome file immagine
    private static final Map<String, String> IMAGE_FILES = new HashMap<>();
    
    static {
        // Scene intro
        IMAGE_FILES.put("hostess", "hostess.png");
        IMAGE_FILES.put("aereo_iniziale", "aereo_scena_iniziale.jpg");
        IMAGE_FILES.put("turbolenza", "aereotrema.jpg");
        IMAGE_FILES.put("aereo_rompe", "aereo_si_rompe_in_volo.jpg");
        IMAGE_FILES.put("crash", "aereo_distrutto_su_spiaggia.jpg");
        IMAGE_FILES.put("occhio", "occhio_aperto.jpg");
        IMAGE_FILES.put("risveglio", "risveglio_in_giungla.jpg");
        IMAGE_FILES.put("vincent", "segui_vincent.jpg");
        IMAGE_FILES.put("aiuto_sopravvissuti", "aiuto_sopravvissuti.jpg");
        IMAGE_FILES.put("curare_feriti", "curare_feriti.jpg");
        IMAGE_FILES.put("curarsi_alcol", "curarsi_con_alcol.jpg");

        // Location principali del gioco
        IMAGE_FILES.put("spiaggia", "spiaggia.jpeg");
        IMAGE_FILES.put("giungla", "risveglio_in_giungla.jpg");
        IMAGE_FILES.put("botola", "botola.jpg");
        IMAGE_FILES.put("grotte", "scoperta_grotte.jpg");
        IMAGE_FILES.put("campo", "campo_sopravvissuti.png");
        IMAGE_FILES.put("villaggio", "arrivo_alle_barracks.jpg");
        IMAGE_FILES.put("tempio", "mappa_isola.jpg");
        IMAGE_FILES.put("roccianera", "black_rock_in_lontananza.jpg");
        IMAGE_FILES.put("faro", "l_isola_e_nostra.jpg");
        IMAGE_FILES.put("pista", "cap15_pista_nascosta.png");

        // Scene Giorno 1
        IMAGE_FILES.put("giorno1_mattino", "spiaggia.jpeg");
        IMAGE_FILES.put("riunione", "campo_sopravvissuti.png");
        IMAGE_FILES.put("esplorazione", "risveglio_in_giungla.jpg");
        IMAGE_FILES.put("prima_notte", "cap1_prima_notte.png");
        IMAGE_FILES.put("mostro_fumo", "mostro_di_fumo.jpg");
        
        // ═══════════════════════════════════════════════════════════
        // IMMAGINI CAPITOLI - MAPPATE ALLE IMMAGINI ESISTENTI
        // ═══════════════════════════════════════════════════════════
        IMAGE_FILES.put("cap1_firstnight", "cap1_prima_notte.png");
        IMAGE_FILES.put("cap2_survivors", "cap2_sopravvissuti.png");
        IMAGE_FILES.put("cap3_smoke", "cap3_mostro_fumo_davanti.png");
        IMAGE_FILES.put("cap4_caves", "cap4_grotte.png");
        IMAGE_FILES.put("cap5_hunt", "cap5_caccia.png");
        IMAGE_FILES.put("cap6_hatch", "cap6_botola_scoperta.png");
        IMAGE_FILES.put("cap7_blackrock", "cap7_roccia_nera.png");
        IMAGE_FILES.put("cap8_openhatch", "dinamite_alla_botola.png");
        IMAGE_FILES.put("cap9_swan", "cap9_cigno_radio.png");
        IMAGE_FILES.put("cap10_henrygale", "cap10_prigioniero.png");
        IMAGE_FILES.put("cap11_others", "cap11_altri.png");
        IMAGE_FILES.put("cap11_escape_others", "cap11_fuga.png");
        IMAGE_FILES.put("cap12_raft", "cap12_zattera.png");
        IMAGE_FILES.put("cap13_walt", "cap13_mare_aperto.png");
        IMAGE_FILES.put("cap13_flashback", "cap13_flashback_aeroporto.png");
        IMAGE_FILES.put("cap14_map", "cap14_scoperta_mappa.png");
        IMAGE_FILES.put("cap15_runway", "cap15_pista_nascosta.png");
        IMAGE_FILES.put("cap16_prep", "cap16_preparazione_volo.png");
        IMAGE_FILES.put("cap17_escape", "cap17_fuga_finale.png");
        IMAGE_FILES.put("cap18_freedom", "cap18_liberta.png");
        
        // Immagini-evento: momenti chiave che cambiano la scena
        IMAGE_FILES.put("botola_aperta", "cap8_botola_aperta.png");
        IMAGE_FILES.put("caccia_al_cinghiale", "caccia_al_cinghiale.jpg");
        IMAGE_FILES.put("terminale_numero", "terminale_numero.jpg");
        IMAGE_FILES.put("circondati", "circondati.jpg");
        IMAGE_FILES.put("fuga_dal_fumo_nero", "fuga_dal_fumo_nero.jpg");
        IMAGE_FILES.put("spari_e_fuoco", "spari_e_fuoco_sulla_spiaggia.jpg");
        IMAGE_FILES.put("desmond", "desmond.jpg");

        // Immagini extra disponibili
        IMAGE_FILES.put("black_rock_interno", "black_rock_da_vicino.jpg");
        IMAGE_FILES.put("interno_botola", "interno_botola.jpg");
        IMAGE_FILES.put("discesa_botola", "discesa_botola.jpg");
        IMAGE_FILES.put("scoperta_dinamite", "scoperta_dinamite.jpg");
        IMAGE_FILES.put("cattura", "cattura_del_prigioniero.jpg");
        IMAGE_FILES.put("prigioniero_giungla", "giungla_con_prigioniero.jpg");
        IMAGE_FILES.put("scambio_prigioniero", "scambio_prigioniero.jpg");
        IMAGE_FILES.put("riunione_altri", "riunione_sugli_altri.jpg");
        IMAGE_FILES.put("barracks", "arrivo_alle_barracks.jpg");
        IMAGE_FILES.put("birre_blackrock", "trasporto_birre_black_rock.png");
        IMAGE_FILES.put("item_enigmi_radio", "item_enigmi_radio.png");
    }
    
    /**
     * Crea il gestore e pre-carica le immagini disponibili.
     * @param width larghezza di destinazione delle immagini
     * @param height altezza di destinazione delle immagini
     */
    public PixelArtManager(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageCache = new HashMap<>();

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
        System.out.println(" Caricate " + imageCache.size() + " immagini dal classpath");
    }
    
    /**
     * Carica un'immagine dal classpath
     */
    private BufferedImage loadImageFromFile(String filename) {
        try (InputStream is = getClass().getResourceAsStream("/images/" + filename)) {
            if (is != null) {
                BufferedImage original = ImageIO.read(is);
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
     * @param locationKey chiave della location
     * @return immagine della location o placeholder
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
        
        // Placeholder volutamente nero: se manca un'immagine non deve cambiare atmosfera.
        Color topColor = Color.BLACK;
        Color bottomColor = new Color(8, 8, 8);
        
        // Gradiente
        GradientPaint gradient = new GradientPaint(
            0, 0, topColor,
            0, imageHeight, bottomColor
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, imageWidth, imageHeight);
        
        // Testo location
        g.setColor(Color.WHITE);
        g.setFont(GameFonts.retroBold(30f));
        String text = locationKey.toUpperCase().replace("_", " ");
        FontMetrics fm = g.getFontMetrics();
        int textX = (imageWidth - fm.stringWidth(text)) / 2;
        int textY = imageHeight / 2;
        g.drawString(text, textX, textY);
        
        // Nota per creare immagine
        g.setFont(GameFonts.retroPlain(20f));
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
     * @param key chiave con cui registrare l'immagine
     * @param image immagine da ridimensionare e memorizzare
     */
    public void addImage(String key, BufferedImage image) {
        imageCache.put(key, resizeImage(image, imageWidth, imageHeight));
    }
    
    /**
     * Ricarica un'immagine da file
     * @param key chiave dell'immagine da ricaricare
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
     * @param key chiave dell'immagine da cercare
     * @return true se l'immagine e' in cache o disponibile su file
     */
    public boolean hasImage(String key) {
        if (imageCache.containsKey(key)) return true;
        if (IMAGE_FILES.containsKey(key)) {
            return getClass().getResource("/images/" + IMAGE_FILES.get(key)) != null;
        }
        return false;
    }
    
    /**
     * Lista le immagini mancanti
     */
    public void printMissingImages() {
        System.out.println("\n IMMAGINI MANCANTI:");
        System.out.println("=========================================");
        for (Map.Entry<String, String> entry : IMAGE_FILES.entrySet()) {
            String filename = entry.getValue();
            if (getClass().getResource("/images/" + filename) == null) {
                System.out.println("   " + entry.getKey() + " -> " + filename);
            }
        }
        System.out.println("=========================================\n");
    }
}
