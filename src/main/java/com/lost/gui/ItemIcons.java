package com.lost.gui;

import com.lost.model.Item;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Associa a ogni oggetto del gioco una piccola icona PNG per la barra
 * di stato. Le icone sono caricate dal classpath (/images/icon_*.png),
 * ridimensionate e tenute in cache. Se l'icona di un oggetto non esiste
 * ancora, il metodo restituisce null e la barra mostra il nome testuale.
 */
final class ItemIcons {

    private ItemIcons() {
    }

    /** Lato in pixel delle icone nella barra di stato. */
    static final int ICON_SIZE = 28;

    // Parola chiave nel nome dell'oggetto -> file icona. L'ordine conta:
    // le voci piu' specifiche vanno prima di quelle generiche.
    private static final String[][] ICON_MAP = {
        {"trasmettitore", "icon_trasmettitore.png"},
        {"radio",         "icon_radio.png"},
        {"cavo",          "icon_cavo.png"},
        {"antenna",       "icon_cavo.png"},
        {"batteria",      "icon_batteria.png"},
        {"fusibile",      "icon_fusibile.png"},
        {"dinamite",      "icon_dinamite.png"},
        {"bussola",       "icon_bussola.png"},
        {"diario",        "icon_diario.png"},
        {"pista",         "icon_mappa_pista.png"},
        {"mappa",         "icon_mappa_dharma.png"},
        {"kit",           "icon_kit.png"},
        {"medico",        "icon_kit.png"},
        {"acqua",         "icon_acqua.png"},
        {"cibo",          "icon_cibo.png"},
        {"whisky",        "icon_whisky.png"},
    };

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    /**
     * Icona scalata per un oggetto, o null se non esiste un file icona.
     * @param item oggetto di cui ottenere l'icona
     * @return l'icona pronta per una JLabel, oppure null
     */
    static ImageIcon iconFor(Item item) {
        if (item == null || item.getName() == null) {
            return null;
        }
        String name = item.getName().toLowerCase(Locale.ROOT);
        for (String[] entry : ICON_MAP) {
            if (name.contains(entry[0])) {
                return load(entry[1]);
            }
        }
        return null;
    }

    private static ImageIcon load(String filename) {
        if (CACHE.containsKey(filename)) {
            return CACHE.get(filename);
        }
        ImageIcon icon = null;
        try (InputStream is = ItemIcons.class.getResourceAsStream("/images/" + filename)) {
            if (is != null) {
                Image raw = ImageIO.read(is);
                if (raw != null) {
                    Image scaled = raw.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaled);
                }
            }
        } catch (Exception ignored) {
            // icona non disponibile: la barra mostrera' il nome testuale
        }
        CACHE.put(filename, icon);
        return icon;
    }
}
