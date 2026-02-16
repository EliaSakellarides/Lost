package com.lostthesis.engine;

import java.util.*;

/**
 * Parser dei comandi con sistema di alias.
 * Mappa sinonimi, abbreviazioni e traduzioni inglesi
 * al CommandType canonico corrispondente.
 */
public class CommandParser {

    /** Risultato del parsing: tipo di comando + target (argomento) */
    public static class ParsedCommand {
        private final CommandType type;
        private final String target;
        private final String rawAction;

        public ParsedCommand(CommandType type, String target, String rawAction) {
            this.type = type;
            this.target = target;
            this.rawAction = rawAction;
        }

        public CommandType getType() { return type; }
        public String getTarget() { return target; }
        public String getRawAction() { return rawAction; }
    }

    private final Map<String, CommandType> aliasMap;

    public CommandParser() {
        aliasMap = new HashMap<>();
        registerAliases();
    }

    private void registerAliases() {
        // AVANTI - proseguire nella storia
        register(CommandType.AVANTI,
            "avanti", "continua", "prosegui", "avanza", "vai", "next", "n");

        // RISPONDI - rispondere a una domanda
        register(CommandType.RISPONDI,
            "rispondi", "risposta", "answer");

        // SCEGLI - scegliere un'opzione A/B/C
        register(CommandType.SCEGLI,
            "scegli", "scelta", "choose");

        // PRENDI - raccogliere oggetti
        register(CommandType.PRENDI,
            "prendi", "raccogli", "afferra", "piglia", "acquisisci",
            "take", "grab", "pick", "p");

        // LASCIA - lasciare oggetti
        register(CommandType.LASCIA,
            "lascia", "posa", "metti", "drop", "abbandona", "l");

        // GUARDA - esaminare oggetti o ambiente
        register(CommandType.GUARDA,
            "guarda", "osserva", "esamina", "ispeziona", "descrivi",
            "look", "examine", "inspect", "g", "x");

        // MANGIA - consumare cibo/bevande
        register(CommandType.MANGIA,
            "mangia", "bevi", "divora", "sgranocchia",
            "eat", "drink");

        // ATTIVA - attivare/accendere oggetti
        register(CommandType.ATTIVA,
            "attiva", "accendi", "carica", "innesca",
            "activate", "light");

        // USA - usare un oggetto generico
        register(CommandType.USA,
            "usa", "utilizza", "adopera", "impiega",
            "use", "u");

        // INVENTARIO - mostrare inventario
        register(CommandType.INVENTARIO,
            "inventario", "zaino", "borsa", "tasca", "oggetti",
            "inventory", "inv", "i");

        // STATO - mostrare salute/status
        register(CommandType.STATO,
            "stato", "status", "salute", "vita",
            "health", "hp", "st");

        // AIUTO - mostrare comandi
        register(CommandType.AIUTO,
            "aiuto", "help", "comandi", "h", "?");

        // SALVA - salvare partita
        register(CommandType.SALVA,
            "salva", "save", "salvataggio");

        // CARICA PARTITA - caricare partita
        register(CommandType.CARICA_PARTITA,
            "load", "caricapartita", "carica_partita", "ricarica");

        // MAPPA - mostrare la mappa dell'isola
        register(CommandType.MAPPA,
            "mappa", "map", "cartina", "m");
    }

    private void register(CommandType type, String... aliases) {
        for (String alias : aliases) {
            aliasMap.put(alias.toLowerCase(), type);
        }
    }

    /**
     * Parsa una stringa di input e restituisce il comando riconosciuto.
     * @param input l'input completo dell'utente
     * @return ParsedCommand con tipo e target
     */
    public ParsedCommand parse(String input) {
        String cmd = input.trim().toLowerCase();
        if (cmd.isEmpty()) {
            return new ParsedCommand(CommandType.AVANTI, "", "");
        }

        String[] parts = cmd.split("\\s+", 2);
        String action = parts[0];
        String target = parts.length > 1 ? parts[1] : "";

        CommandType type = aliasMap.getOrDefault(action, CommandType.SCONOSCIUTO);
        return new ParsedCommand(type, target, action);
    }

    /**
     * Restituisce tutti gli alias registrati per un dato CommandType.
     */
    public List<String> getAliases(CommandType type) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, CommandType> entry : aliasMap.entrySet()) {
            if (entry.getValue() == type) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Testo di aiuto completo con tutti gli alias raggruppati per comando.
     */
    public String getAliasHelpText() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("  ALIAS DISPONIBILI\n");
        sb.append("═══════════════════════════════════════\n");

        appendAliasLine(sb, CommandType.PRENDI,   "📦 Prendi");
        appendAliasLine(sb, CommandType.LASCIA,    "📤 Lascia");
        appendAliasLine(sb, CommandType.GUARDA,    "👁️ Guarda");
        appendAliasLine(sb, CommandType.USA,       "🔧 Usa");
        appendAliasLine(sb, CommandType.MANGIA,    "🍎 Mangia");
        appendAliasLine(sb, CommandType.ATTIVA,    "💣 Attiva");
        appendAliasLine(sb, CommandType.INVENTARIO,"🎒 Inventario");
        appendAliasLine(sb, CommandType.STATO,     "❤️ Stato");
        appendAliasLine(sb, CommandType.AVANTI,    "➡️ Avanti");
        appendAliasLine(sb, CommandType.SALVA,     "💾 Salva");
        appendAliasLine(sb, CommandType.CARICA_PARTITA, "📂 Carica");
        appendAliasLine(sb, CommandType.MAPPA,     "🗺️ Mappa");
        appendAliasLine(sb, CommandType.AIUTO,     "❓ Aiuto");

        sb.append("═══════════════════════════════════════\n");
        sb.append("💡 Abbreviazioni rapide:\n");
        sb.append("   p=prendi  g=guarda  l=lascia\n");
        sb.append("   i=inventario  u=usa  h=aiuto\n");
        sb.append("═══════════════════════════════════════");
        return sb.toString();
    }

    private void appendAliasLine(StringBuilder sb, CommandType type, String label) {
        List<String> aliases = getAliases(type);
        sb.append(label).append(": ").append(String.join(", ", aliases)).append("\n");
    }
}
