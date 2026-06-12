package com.lost.graphics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte testo puro in HTML colorizzato per JTextPane.
 * Regole di colorizzazione basate su parole chiave e dialoghi.
 */
public final class TextColorizer {

    private TextColorizer() {
    }

    // Colori tema LOST
    private static final String DEFAULT_COLOR = "#C8DCC8";
    private static final String GREEN_BRIGHT = "#66FF66";
    private static final String RED = "#FF6666";
    private static final String GREEN_LIGHT = "#88DD88";
    private static final String GOLD = "#FFDC64";
    private static final String AZURE = "#66CCFF";
    private static final String AMBER = "#FFB347";
    private static final String GREEN_DARK = "#558855";
    private static final String ORANGE_ITALIC = "#FFAA44";
    private static final String RED_LIGHT = "#FF8888";

    // Parole chiave
    private static final String[] GOLD_KEYWORDS = {"MAPPA", "DHARMA", "LOST", "JACOB", "HYDRA"};
    private static final String[] RED_KEYWORDS = {"MOSTRO", "FUMO", "BOOM"};

    // Pattern per dialoghi tra virgolette
    private static final Pattern QUOTE_PATTERN = Pattern.compile("\"([^\"]+)\"");

    /**
     * Converte testo puro in HTML colorato secondo le regole del tema.
     * @param plainText testo da colorare
     * @return documento HTML pronto per il JTextPane
     */
    public static String colorize(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return wrapHtml("");
        }

        StringBuilder body = new StringBuilder();
        String[] lines = plainText.split("\n", -1);

        for (String line : lines) {
            body.append(colorizeLine(line));
            body.append("<br>");
        }

        return wrapHtml(body.toString());
    }

    private static String colorizeLine(String line) {
        String trimmed = line.trim();

        // Separatori ═══
        if (trimmed.contains("═══")) {
            return span(escapeHtml(line), GREEN_DARK, false, false);
        }

        // Righe speciali - bold
        if (trimmed.contains("CORRETTO") ||
                trimmed.contains("COMPLETATA CON SUCCESSO") ||
                trimmed.contains("HAI COMPLETATO")) {
            return span(escapeHtml(line), GREEN_BRIGHT, true, false);
        }
        if (trimmed.contains("Risposta sbagliata") ||
                trimmed.contains("Direzione sbagliata") ||
                trimmed.contains("PREDA PERSA") ||
                trimmed.contains("SEI MORTO") ||
                trimmed.contains("Errore")) {
            return span(escapeHtml(line), RED, true, false);
        }
        if (trimmed.contains("CAP.")) {
            return span(escapeHtml(line), GOLD, true, false);
        }
        if (trimmed.contains("Cosa fai") ||
                trimmed.contains("Come ") ||
                trimmed.contains("Quale ") ||
                trimmed.contains("Quanti ") ||
                trimmed.contains("Scrivi la risposta")) {
            return span(escapeHtml(line), AZURE, true, false);
        }
        if (trimmed.contains("ATTENZIONE") ||
                trimmed.contains("Salute -") ||
                trimmed.contains("Sanita -") ||
                trimmed.contains("Penalita")) {
            return span(escapeHtml(line), AMBER, true, false);
        }

        // Scelte e suggerimenti - verde chiaro
        if (trimmed.contains("SCELTE") ||
                trimmed.contains("Scelte") ||
                trimmed.contains("Premi ") ||
                trimmed.contains("Suggerimento") ||
                trimmed.contains("Usa ")) {
            return span(escapeHtml(line), GREEN_LIGHT, false, false);
        }

        // Default: applica colorizzazione inline (parole chiave + dialoghi)
        return colorizeInline(line);
    }

    private static String colorizeInline(String line) {
        String escaped = escapeHtml(line);

        // Colora dialoghi tra virgolette -> arancione corsivo
        Matcher quoteMatcher = QUOTE_PATTERN.matcher(escaped);
        StringBuffer sb = new StringBuffer();
        while (quoteMatcher.find()) {
            String replacement = span("&quot;" + quoteMatcher.group(1) + "&quot;",
                    ORANGE_ITALIC, false, true);
            quoteMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        quoteMatcher.appendTail(sb);
        String result = sb.toString();

        // Colora parole chiave oro
        for (String kw : GOLD_KEYWORDS) {
            result = highlightKeyword(result, kw, GOLD);
        }

        // Colora parole chiave rosse
        for (String kw : RED_KEYWORDS) {
            result = highlightKeyword(result, kw, RED_LIGHT);
        }

        return span(result, DEFAULT_COLOR, false, false, true);
    }

    private static String highlightKeyword(String text, String keyword, String color) {
        // Case-insensitive replace ma preserva il case originale
        Pattern p = Pattern.compile("(?<![<\\w])(" + Pattern.quote(keyword) + ")(?![\\w>])",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String replacement = "<b><span style=\"color:" + color + "\">"
                    + m.group(1) + "</span></b>";
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String span(String content, String color, boolean bold, boolean italic) {
        return span(content, color, bold, italic, false);
    }

    private static String span(String content, String color, boolean bold, boolean italic, boolean raw) {
        StringBuilder sb = new StringBuilder();
        sb.append("<span style=\"color:").append(color).append("\">");
        if (bold) sb.append("<b>");
        if (italic) sb.append("<i>");
        sb.append(raw ? content : content);
        if (italic) sb.append("</i>");
        if (bold) sb.append("</b>");
        sb.append("</span>");
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String wrapHtml(String body) {
        return "<html><head><style>"
                + "body { font-family: 'VT323', 'Courier New', monospace; "
                + "font-size: 18pt; "
                + "color: " + DEFAULT_COLOR + "; "
                + "margin: 8px; "
                + "padding: 0; }"
                + "</style></head><body>"
                + body
                + "</body></html>";
    }
}
