package com.lostthesis.minigames;

import java.util.*;

/**
 * Capitolo 7 - Disinnesco Dinamite
 * Simon-Says con fili colorati. Sequenza da 2 a 5.
 * Bottoni: A = taglia filo (conferma sequenza), B = ripeti sequenza (max 3), C = abbandona
 * Fallimento: sequenza sbagliata = esplosione (retry disponibile)
 */
public class DynamiteDefusalGame implements MiniGame {
    private MiniGameState state;
    private int currentRound;
    private static final int MAX_ROUNDS = 4; // round 0-3 = sequenze da 2 a 5
    private List<String> currentSequence;
    private int repeatsLeft;
    private boolean waitingForInput;
    private boolean showingSequence;
    private Random random;
    private int totalRetries;

    private static final String[] COLORS = {"R", "B", "G", "V"}; // Rosso, Blu, Verde, Viola
    private static final String[] COLOR_NAMES = {"ROSSO", "BLU", "VERDE", "VIOLA"};

    private static final String DYNAMITE_ART =
        "     _________\n" +
        "    |  DHARMA  |\n" +
        "    | DYNAMITE |\n" +
        "    |_________|\n" +
        "    |||||||||||\n" +
        "    |||||||||||\n" +
        "   ~^^^^^^^^^^^~\n" +
        "   FILI: R  B  G  V\n" +
        "         |  |  |  |\n";

    public DynamiteDefusalGame() {
        random = new Random(42); // seed fisso per riproducibilita'
        reset();
    }

    @Override
    public String getName() {
        return "Disinnesco Dinamite";
    }

    @Override
    public String getInstructions() {
        return "========================================\n" +
               "  DISINNESCO DINAMITE\n" +
               "========================================\n\n" +
               DYNAMITE_ART + "\n" +
               "La dinamite ha 4 fili colorati!\n" +
               "Devi tagliare i fili nella SEQUENZA GIUSTA.\n\n" +
               "Ti verra' mostrata una sequenza di colori.\n" +
               "Ripetila nell'ordine corretto!\n\n" +
               "Colori: R=Rosso B=Blu G=Verde V=Viola\n\n" +
               "A = Taglia fili (conferma la sequenza)\n" +
               "B = Ripeti sequenza (max 3 volte per round)\n" +
               "C = Abbandona (perdi oggetto bonus)\n\n" +
               "Sequenza sbagliata = BOOM! (ma puoi riprovare)\n" +
               "========================================";
    }

    @Override
    public MiniGameState getState() {
        return state;
    }

    @Override
    public String handleButtonInput(String button) {
        if (state != MiniGameState.IN_PROGRESS) return getCurrentDisplay();

        button = button.toUpperCase();
        switch (button) {
            case "A":
                if (showingSequence) {
                    showingSequence = false;
                    waitingForInput = true;
                    return "Inserisci la sequenza di colori!\n" +
                           "Usa le lettere: R (Rosso), B (Blu), G (Verde), V (Viola)\n" +
                           "Esempio: R B G\n" +
                           "Scrivi la sequenza separata da spazi:";
                }
                waitingForInput = true;
                return "Scrivi la sequenza di colori (es: R B G V):";
            case "B":
                if (repeatsLeft > 0) {
                    repeatsLeft--;
                    return showSequence();
                }
                return "Non puoi ripetere la sequenza! Tentativi ripetizione esauriti.";
            case "C":
                state = MiniGameState.LOST;
                return "========================================\n" +
                       "  ABBANDONATO!\n" +
                       "========================================\n\n" +
                       "Hai lasciato la dinamite.\n" +
                       "Non esplodera', ma non otterrai\n" +
                       "l'oggetto bonus...";
            default:
                return "Usa i pulsanti A, B o C!";
        }
    }

    @Override
    public String handleTextInput(String text) {
        if (state != MiniGameState.IN_PROGRESS) return getCurrentDisplay();

        String[] input = text.trim().toUpperCase().split("\\s+");
        List<String> inputList = new ArrayList<>();
        for (String s : input) {
            if (!s.isEmpty()) inputList.add(s);
        }

        if (inputList.size() != currentSequence.size()) {
            return "Sequenza di lunghezza sbagliata! Servono " + currentSequence.size() + " colori.\n" +
                   "Riprova (es: " + String.join(" ", currentSequence) + " ha " + currentSequence.size() + " elementi)";
        }

        boolean correct = true;
        for (int i = 0; i < currentSequence.size(); i++) {
            if (!inputList.get(i).equals(currentSequence.get(i))) {
                correct = false;
                break;
            }
        }

        waitingForInput = false;

        if (correct) {
            currentRound++;
            if (currentRound >= MAX_ROUNDS) {
                state = MiniGameState.WON;
                return "========================================\n" +
                       "  DINAMITE DISINNESCATA!\n" +
                       "========================================\n\n" +
                       DYNAMITE_ART + "\n" +
                       "Tutti i fili tagliati correttamente!\n" +
                       "La dinamite e' disinnescata.\n" +
                       "Sei un esperto artificiere!\n\n" +
                       "DISINNESCO COMPLETATO!";
            }

            generateSequence();
            repeatsLeft = 3;
            return "Corretto! Fili tagliati nel giusto ordine!\n\n" +
                   "--- Round " + (currentRound + 1) + "/" + MAX_ROUNDS + " ---\n" +
                   "La sequenza si allunga...\n\n" +
                   showSequence();
        } else {
            totalRetries++;
            generateSequence();
            repeatsLeft = 3;
            return "========================================\n" +
                   "  BOOM! Sequenza sbagliata!\n" +
                   "========================================\n\n" +
                   "Per fortuna era un filo di prova.\n" +
                   "La dinamite non e' esplosa... stavolta.\n\n" +
                   "Riproviamo! Round " + (currentRound + 1) + "/" + MAX_ROUNDS + "\n\n" +
                   showSequence();
        }
    }

    private void generateSequence() {
        currentSequence = new ArrayList<>();
        int length = currentRound + 2; // da 2 a 5
        for (int i = 0; i < length; i++) {
            currentSequence.add(COLORS[random.nextInt(COLORS.length)]);
        }
    }

    private String showSequence() {
        showingSequence = true;
        StringBuilder sb = new StringBuilder();
        sb.append("MEMORIZZA LA SEQUENZA:\n\n  ");
        for (int i = 0; i < currentSequence.size(); i++) {
            String color = currentSequence.get(i);
            int idx = Arrays.asList(COLORS).indexOf(color);
            sb.append(COLOR_NAMES[idx]);
            if (i < currentSequence.size() - 1) sb.append(" -> ");
        }
        sb.append("\n\n  ( ").append(String.join("  ", currentSequence)).append(" )\n\n");
        sb.append("Ripetizioni rimaste: ").append(repeatsLeft).append("/3\n");
        sb.append("Premi A per inserire la sequenza.");
        return sb.toString();
    }

    @Override
    public String getCurrentDisplay() {
        switch (state) {
            case PENDING:
                return getInstructions();
            case WON:
                return "Dinamite disinnescata!";
            case LOST:
                return "Hai abbandonato il disinnesco.";
            default:
                if (waitingForInput) {
                    return "Scrivi la sequenza di colori (es: R B G V):";
                }
                return "--- Disinnesco Dinamite ---\n" +
                       "Round " + (currentRound + 1) + "/" + MAX_ROUNDS + "\n\n" +
                       DYNAMITE_ART + "\n" +
                       "Premi B per vedere la sequenza.";
        }
    }

    @Override
    public String getButtonALabel() { return "Taglia"; }
    @Override
    public String getButtonBLabel() { return "Ripeti"; }
    @Override
    public String getButtonCLabel() { return "Abbandona"; }

    @Override
    public void reset() {
        state = MiniGameState.IN_PROGRESS;
        currentRound = 0;
        repeatsLeft = 3;
        waitingForInput = false;
        showingSequence = false;
        totalRetries = 0;
        random = new Random(42);
        generateSequence();
    }
}
