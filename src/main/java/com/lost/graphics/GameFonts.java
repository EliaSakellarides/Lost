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

    public static Font retro(int style, float size) {
        return BASE_FONT.deriveFont(style, size);
    }

    public static Font retroPlain(float size) {
        return retro(Font.PLAIN, size);
    }

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
