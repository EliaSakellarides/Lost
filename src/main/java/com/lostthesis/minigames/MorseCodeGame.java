package com.lostthesis.minigames;

import java.util.*;

/**
 * Capitolo 14 - Codice Morse
 * Messaggio Morse diviso in 5 segmenti. Il giocatore decodifica ogni segmento.
 * Bottoni: A = mostra alfabeto Morse, B = prossimo segmento, C = verifica risposta
 * Messaggio finale: "COORDINATE PER PISTA"
 * Nessun limite tentativi, hint dopo 3 errori per segmento.
 */
public class MorseCodeGame implements MiniGame {
    private MiniGameState state;
    private int currentSegment;
    private int errorsThisSegment;
    private boolean waitingForAnswer;
    private boolean showingAlphabet;

    private static final String[] MORSE_SEGMENTS = {
        "-.-. --- --- .-. -..",     // COORD
        ".. -. .- - .",            // INATE
        ".--. . .-.",              // PER
        ".--. .. ... - .-",        // PISTA
    };

    private static final String[] SEGMENT_ANSWERS = {
        "coord", "inate", "per", "pista"
    };

    private static final String[] SEGMENT_HINTS = {
        "La prima parte indica una posizione... COORD...",
        "Completa la parola: COORD + ???ATE",
        "Una parola di 3 lettere che significa 'al fine di'",
        "Dove atterrano gli aerei? Una _ _ _ _ A"
    };

    private static final String MORSE_ALPHABET =
        "ALFABETO MORSE\n" +
        "========================================\n" +
        "A = .-      N = -.      0 = -----\n" +
        "B = -...    O = ---     1 = .----\n" +
        "C = -.-.    P = .--.    2 = ..---\n" +
        "D = -..     Q = --.-    3 = ...--\n" +
        "E = .       R = .-.     4 = ....-\n" +
        "F = ..-.    S = ...     5 = .....\n" +
        "G = --.     T = -       6 = -....\n" +
        "H = ....    U = ..-     7 = --...\n" +
        "I = ..      V = ...-    8 = ---..\n" +
        "J = .---    W = .--     9 = ----.\n" +
        "K = -.-     X = -..-\n" +
        "L = .-..    Y = -.--\n" +
        "M = --      Z = --..\n" +
        "========================================\n" +
        "Spazio tra lettere = spazio singolo\n" +
        "Punto = .   Linea = -";

    public MorseCodeGame() {
        reset();
    }

    @Override
    public String getName() {
        return "Codice Morse";
    }

    @Override
    public String getInstructions() {
        return "========================================\n" +
               "  CODICE MORSE - MESSAGGIO SEGRETO\n" +
               "========================================\n\n" +
               "La radio trasmette un messaggio in codice Morse!\n" +
               "Decodifica ogni segmento per scoprire il messaggio.\n\n" +
               "A = Mostra alfabeto Morse\n" +
               "B = Mostra segmento corrente\n" +
               "C = Verifica la tua risposta\n\n" +
               "Nessun limite di tentativi.\n" +
               "Dopo 3 errori per segmento, ricevi un suggerimento.\n" +
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
                showingAlphabet = true;
                waitingForAnswer = false;
                return MORSE_ALPHABET;
            case "B":
                showingAlphabet = false;
                waitingForAnswer = false;
                return getSegmentDisplay();
            case "C":
                showingAlphabet = false;
                waitingForAnswer = true;
                return "Scrivi la decodifica del segmento " + (currentSegment + 1) + ":";
            default:
                return "Usa i pulsanti A, B o C!";
        }
    }

    @Override
    public String handleTextInput(String text) {
        if (state != MiniGameState.IN_PROGRESS) return getCurrentDisplay();

        String answer = text.trim().toLowerCase();

        if (answer.equals(SEGMENT_ANSWERS[currentSegment])) {
            currentSegment++;
            errorsThisSegment = 0;
            waitingForAnswer = false;

            if (currentSegment >= MORSE_SEGMENTS.length) {
                state = MiniGameState.WON;
                return "========================================\n" +
                       "  MESSAGGIO DECODIFICATO!\n" +
                       "========================================\n\n" +
                       "Il messaggio completo e':\n\n" +
                       "  \"COORDINATE PER PISTA\"\n\n" +
                       "Le coordinate indicano la posizione\n" +
                       "della pista di atterraggio nascosta!\n" +
                       "Questa informazione e' fondamentale\n" +
                       "per la fuga dall'isola!\n\n" +
                       "DECODIFICA COMPLETATA!";
            }

            return "Corretto! Segmento decodificato!\n\n" +
                   "Messaggio finora: " + getDecodedSoFar() + "\n\n" +
                   "--- Segmento " + (currentSegment + 1) + "/" + MORSE_SEGMENTS.length + " ---\n\n" +
                   getSegmentDisplay();
        } else {
            errorsThisSegment++;
            waitingForAnswer = false;
            String response = "Sbagliato! '" + text.trim().toUpperCase() + "' non e' corretto.\n";
            if (errorsThisSegment >= 3) {
                response += "\nSuggerimento: " + SEGMENT_HINTS[currentSegment] + "\n";
            }
            response += "\n" + getSegmentDisplay();
            return response;
        }
    }

    private String getSegmentDisplay() {
        return "--- Segmento " + (currentSegment + 1) + "/" + MORSE_SEGMENTS.length + " ---\n\n" +
               "Codice Morse:\n" +
               MORSE_SEGMENTS[currentSegment] + "\n\n" +
               "Decodifica questo segmento!\n" +
               "Premi C e scrivi la risposta.";
    }

    private String getDecodedSoFar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentSegment; i++) {
            sb.append(SEGMENT_ANSWERS[i].toUpperCase());
            if (i < currentSegment - 1) sb.append(" ");
        }
        sb.append(" ???");
        return sb.toString();
    }

    @Override
    public String getCurrentDisplay() {
        switch (state) {
            case PENDING:
                return getInstructions();
            case WON:
                return "Messaggio decodificato: COORDINATE PER PISTA";
            case LOST:
                return ""; // Non puo' perdere
            default:
                if (showingAlphabet) return MORSE_ALPHABET;
                if (waitingForAnswer) return "Scrivi la decodifica del segmento " + (currentSegment + 1) + ":";
                return getSegmentDisplay();
        }
    }

    @Override
    public String getButtonALabel() { return "Alfabeto"; }
    @Override
    public String getButtonBLabel() { return "Segmento"; }
    @Override
    public String getButtonCLabel() { return "Verifica"; }

    @Override
    public void reset() {
        state = MiniGameState.IN_PROGRESS;
        currentSegment = 0;
        errorsThisSegment = 0;
        waitingForAnswer = false;
        showingAlphabet = false;
    }
}
