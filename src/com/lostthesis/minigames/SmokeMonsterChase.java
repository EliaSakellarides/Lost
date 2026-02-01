package com.lostthesis.minigames;

import java.util.*;

/**
 * Capitolo 3 - Fuga dal Mostro di Fumo
 * 5 round di scelte rapide via bottoni A/B/C.
 * Ogni round: una scelta sbagliata (verso il mostro), una sicura, una rischiosa ma con bonus.
 * Timer: ~10 secondi per scelta (gestito dal GameEngine).
 * Fallimento: 3 scelte sbagliate = catturato.
 * Input: solo bottoni A/B/C.
 */
public class SmokeMonsterChase implements MiniGame {
    private MiniGameState state;
    private int currentRound;
    private int errors;
    private int bonusCollected;
    private long roundStartTime;
    private static final int MAX_ERRORS = 3;
    private static final int TOTAL_ROUNDS = 5;
    private static final long TIMEOUT_MS = 10000; // 10 secondi

    // Per ogni round: indice 0=A, 1=B, 2=C
    // Valori: 0=sbagliata, 1=sicura, 2=rischiosa+bonus
    private final int[][] roundChoices = {
        {0, 1, 2},  // Round 1: A=sbagliata, B=sicura, C=rischiosa
        {2, 0, 1},  // Round 2: A=rischiosa, B=sbagliata, C=sicura
        {1, 2, 0},  // Round 3: A=sicura, B=rischiosa, C=sbagliata
        {0, 2, 1},  // Round 4: A=sbagliata, B=rischiosa, C=sicura
        {2, 1, 0},  // Round 5: A=rischiosa, B=sicura, C=sbagliata
    };

    private final String[][] roundLabels = {
        {"Verso il rumore", "Dietro le rocce", "Attraverso il ruscello"},
        {"Nel tronco cavo", "Dritto avanti", "Sali sull'albero"},
        {"Nasconditi nel fango", "Attraversa il ponte", "Torna indietro"},
        {"Corri nel buio", "Scivola nella grotta", "Segui le lucciole"},
        {"Salta nel burrone", "Corri verso la luce", "Verso la spiaggia"},
    };

    private final String[][] roundDescriptions = {
        {
            "TICK... TICK... Il Mostro e' davanti a te!\n" +
            "Tre vie di fuga nella giungla oscura...",

            "Il rumore viene da quella direzione!\n" +
            "Ti sei avvicinato al Mostro di Fumo!",

            "Ti nascondi dietro le rocce.\n" +
            "Il Mostro passa senza vederti. Sicuro!",

            "Attraversi il ruscello di corsa!\n" +
            "Rischiata, ma trovi una torcia abbandonata!"
        },
        {
            "Il Mostro ruggisce! Gli alberi si piegano!\n" +
            "Devi muoverti ORA!",

            "Il tronco cavo ti nasconde!\n" +
            "Rischiata, ma trovi delle bende dentro!",

            "Corri dritto e il Mostro ti vede!\n" +
            "Riesci a scappare per un pelo!",

            "Ti arrampichi sull'albero. Il Mostro\n" +
            "non puo' raggiungerti quassu'!"
        },
        {
            "CRACK! Rami spezzati tutto intorno!\n" +
            "Il fumo nero si avvicina...",

            "Il fango ti copre l'odore.\n" +
            "Il Mostro non ti rileva!",

            "Il ponte di corde oscilla!\n" +
            "Rischiata, ma trovi una mappa dall'altra parte!",

            "Torni indietro dritto nelle fauci\n" +
            "del Mostro di Fumo!"
        },
        {
            "Il ruggito e' assordante!\n" +
            "La giungla trema sotto i tuoi piedi!",

            "Il buio e' totale e inciampi!\n" +
            "Il Mostro ti sfiora!",

            "Scivoli nella grotta stretta!\n" +
            "Rischiata, ma trovi acqua potabile!",

            "Le lucciole ti guidano verso\n" +
            "un sentiero sicuro!"
        },
        {
            "L'ULTIMO TRATTO! La spiaggia e' vicina!\n" +
            "Il Mostro lancia l'attacco finale!",

            "Salti nel burrone e vedi le stelle!\n" +
            "Rischiata, ma atterri su un cespuglio morbido\n" +
            "e trovi un coltello arrugginito!",

            "Corri verso la luce della spiaggia!\n" +
            "Il Mostro non esce dalla giungla!",

            "Corri verso la spiaggia ma e' la\n" +
            "direzione sbagliata! Vicolo cieco!"
        },
    };

    public SmokeMonsterChase() {
        reset();
    }

    @Override
    public String getName() {
        return "Fuga dal Mostro di Fumo";
    }

    @Override
    public String getInstructions() {
        return "========================================\n" +
               "  FUGA DAL MOSTRO DI FUMO!\n" +
               "========================================\n\n" +
               "Il Mostro di Fumo ti insegue!\n" +
               "Hai " + TOTAL_ROUNDS + " round per fuggire.\n\n" +
               "Ogni round: scegli A, B o C\n" +
               "- Una scelta e' SBAGLIATA (verso il mostro)\n" +
               "- Una e' SICURA\n" +
               "- Una e' RISCHIOSA ma da' un bonus!\n\n" +
               "3 scelte sbagliate = CATTURATO!\n" +
               "Hai 10 secondi per decidere!\n\n" +
               "Premi A, B o C per scegliere!\n" +
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
        int choiceIdx;
        switch (button) {
            case "A": choiceIdx = 0; break;
            case "B": choiceIdx = 1; break;
            case "C": choiceIdx = 2; break;
            default: return "Scegli A, B o C! VELOCE!";
        }

        // Controlla timeout
        long elapsed = System.currentTimeMillis() - roundStartTime;
        if (elapsed > TIMEOUT_MS) {
            // Timeout = scelta peggiore
            errors++;
            if (errors >= MAX_ERRORS) {
                state = MiniGameState.LOST;
                return getLosingMessage();
            }
            currentRound++;
            if (currentRound >= TOTAL_ROUNDS) {
                state = MiniGameState.WON;
                return getWinningMessage();
            }
            roundStartTime = System.currentTimeMillis();
            return "TEMPO SCADUTO! Il Mostro ti ha quasi preso!\n" +
                   "Errori: " + errors + "/" + MAX_ERRORS + "\n\n" +
                   getRoundDisplay();
        }

        int result = roundChoices[currentRound][choiceIdx];
        String response;

        switch (result) {
            case 0: // Sbagliata
                errors++;
                response = roundDescriptions[currentRound][1]; // testo sbagliato
                response += "\n\nERRORE! " + errors + "/" + MAX_ERRORS;
                break;
            case 1: // Sicura
                response = roundDescriptions[currentRound][2]; // testo sicuro
                response += "\n\nBuona scelta! Sei al sicuro.";
                break;
            case 2: // Rischiosa + bonus
                bonusCollected++;
                response = roundDescriptions[currentRound][3]; // testo rischioso
                response += "\n\nBonus raccolto! (Totale: " + bonusCollected + ")";
                break;
            default:
                response = "";
        }

        if (errors >= MAX_ERRORS) {
            state = MiniGameState.LOST;
            return response + "\n\n" + getLosingMessage();
        }

        currentRound++;
        if (currentRound >= TOTAL_ROUNDS) {
            state = MiniGameState.WON;
            return response + "\n\n" + getWinningMessage();
        }

        roundStartTime = System.currentTimeMillis();
        return response + "\n\n" + getRoundDisplay();
    }

    @Override
    public String handleTextInput(String text) {
        // Solo bottoni in questo mini gioco
        String t = text.trim().toUpperCase();
        if (t.equals("A") || t.equals("B") || t.equals("C")) {
            return handleButtonInput(t);
        }
        return "Usa solo i pulsanti A, B o C! Non c'e' tempo per scrivere!";
    }

    private String getRoundDisplay() {
        return "--- Round " + (currentRound + 1) + "/" + TOTAL_ROUNDS + " ---\n" +
               "Errori: " + errors + "/" + MAX_ERRORS + "  |  Bonus: " + bonusCollected + "\n\n" +
               roundDescriptions[currentRound][0] + "\n\n" +
               "A = " + roundLabels[currentRound][0] + "\n" +
               "B = " + roundLabels[currentRound][1] + "\n" +
               "C = " + roundLabels[currentRound][2] + "\n\n" +
               "SCEGLI VELOCE!";
    }

    private String getWinningMessage() {
        return "========================================\n" +
               "  SEI SALVO!\n" +
               "========================================\n\n" +
               "Hai seminato il Mostro di Fumo!\n" +
               "Sei arrivato alla spiaggia, al sicuro.\n\n" +
               "Bonus raccolti durante la fuga: " + bonusCollected + "\n" +
               "Errori commessi: " + errors + "/" + MAX_ERRORS + "\n\n" +
               "FUGA COMPLETATA CON SUCCESSO!";
    }

    private String getLosingMessage() {
        return "========================================\n" +
               "  CATTURATO!\n" +
               "========================================\n\n" +
               "Il Mostro di Fumo ti ha raggiunto!\n" +
               "Ti solleva in aria e ti studia...\n\n" +
               "Per qualche ragione, ti lascia andare.\n" +
               "Ma sei ferito e spaventato.";
    }

    @Override
    public String getCurrentDisplay() {
        switch (state) {
            case PENDING:
                return getInstructions();
            case WON:
                return getWinningMessage();
            case LOST:
                return getLosingMessage();
            default:
                return getRoundDisplay();
        }
    }

    @Override
    public String getButtonALabel() {
        if (state == MiniGameState.IN_PROGRESS && currentRound < TOTAL_ROUNDS) {
            return roundLabels[currentRound][0].length() > 12 ?
                   roundLabels[currentRound][0].substring(0, 12) : roundLabels[currentRound][0];
        }
        return "A";
    }

    @Override
    public String getButtonBLabel() {
        if (state == MiniGameState.IN_PROGRESS && currentRound < TOTAL_ROUNDS) {
            return roundLabels[currentRound][1].length() > 12 ?
                   roundLabels[currentRound][1].substring(0, 12) : roundLabels[currentRound][1];
        }
        return "B";
    }

    @Override
    public String getButtonCLabel() {
        if (state == MiniGameState.IN_PROGRESS && currentRound < TOTAL_ROUNDS) {
            return roundLabels[currentRound][2].length() > 12 ?
                   roundLabels[currentRound][2].substring(0, 12) : roundLabels[currentRound][2];
        }
        return "C";
    }

    @Override
    public void reset() {
        state = MiniGameState.IN_PROGRESS;
        currentRound = 0;
        errors = 0;
        bonusCollected = 0;
        roundStartTime = System.currentTimeMillis();
    }
}
