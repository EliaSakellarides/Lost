package com.lost.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Carica il font retro del gioco da classpath e offre fallback sicuri.
 */
public final class GameFonts {
    private static final String FONT_RESOURCE = "/fonts/VT323-Regular.ttf";
    private static final Font BASE_FONT = loadBaseFont();

    private GameFonts() {
    }

    /**
     * Deriva il font retro con stile e dimensione richiesti.
     * @param style stile AWT (es. Font.PLAIN, Font.BOLD)
     * @param size dimensione in punti
     * @return font derivato
     */
    public static Font retro(int style, float size) {
        return BASE_FONT.deriveFont(style, size);
    }

    /**
     * Font retro in stile normale.
     * @param size dimensione in punti
     * @return font derivato
     */
    public static Font retroPlain(float size) {
        return retro(Font.PLAIN, size);
    }

    /**
     * Font retro in grassetto.
     * @param size dimensione in punti
     * @return font derivato
     */
    public static Font retroBold(float size) {
        return retro(Font.BOLD, size);
    }

    private static Font loadBaseFont() {
        try (InputStream input = GameFonts.class.getResourceAsStream(FONT_RESOURCE)) {
            if (input != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, input);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                return font;
            }
        } catch (Exception e) {
            // Se il font non viene caricato, il gioco resta comunque avviabile.
        }

        return new Font("Monospaced", Font.PLAIN, 16);
    }
}
