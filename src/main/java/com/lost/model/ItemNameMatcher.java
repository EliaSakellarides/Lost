package com.lost.model;

import java.util.Locale;

/**
 * Confronto tollerante tra nomi di oggetti: ignora articoli e
 * preposizioni, accetta corrispondenze parziali nei due sensi.
 * Usato da Player e Room per cercare gli oggetti per nome.
 */
final class ItemNameMatcher {

    private ItemNameMatcher() {
    }

    /**
     * Verifica se la query dell'utente corrisponde al nome reale.
     * @param actualName nome reale dell'oggetto
     * @param query nome (anche parziale) cercato dall'utente
     * @return true se i nomi corrispondono
     */
    static boolean matches(String actualName, String query) {
        String actual = normalize(actualName);
        String wanted = normalize(query);
        return !wanted.isEmpty()
            && (actual.equals(wanted) || actual.contains(wanted) || wanted.contains(actual));
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
            .replace("'", " ")
            .replaceAll("\\b(il|lo|la|i|gli|le|un|uno|una|con|sul|sulla|nel|nella|alla|allo)\\b", " ")
            .replaceAll("[^a-z0-9àèéìòù]+", " ")
            .trim()
            .replaceAll("\\s+", " ");
    }
}
