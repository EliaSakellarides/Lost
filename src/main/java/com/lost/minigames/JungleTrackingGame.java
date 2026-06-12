package com.lost.minigames;

import java.util.*;

/**
 * Capitolo 5 - Caccia nella Giungla
 * Una fase di tracciamento: il giocatore raccoglie un indizio
 * e deve capire la direzione (NORD/SUD/EST/OVEST).
 * Bottoni: A = esamina terra, B = ascolta suoni, C = inserisci direzione
 * Fallimento: 3 direzioni sbagliate = preda persa
 */
public class JungleTrackingGame implements MiniGame {
    private MiniGameState state;
    private int currentStep;
    private int errors;
    private static final int MAX_ERRORS = 3;
    private static final int TOTAL_STEPS = 1;
    private boolean examinedGround;
    private boolean listenedSounds;
    private boolean waitingForDirection;

    private final String[] correctDirections = {"nord", "est", "sud", "ovest"};

    private final String[] baseClues = {
        "Vedi impronte fresche di cinghiale nel fango.\nI rami bassi sono spezzati verso una direzione.\nPrima raccogli un indizio con ESAMINA o ASCOLTA.",
        "Le tracce continuano. Trovi del pelo impigliato in un cespuglio.\nUn sentiero naturale si apre davanti a te.\nPrima raccogli un indizio con ESAMINA o ASCOLTA.",
        "Il terreno e' piu' umido. Il cinghiale sta cercando acqua.\nSenti un ruscello in lontananza.\nPrima raccogli un indizio con ESAMINA o ASCOLTA.",
        "Le impronte sono fresche! La preda e' vicina!\nVedi movimenti tra la vegetazione.\nPrima raccogli un indizio con ESAMINA o ASCOLTA."
    };

    private final String[] groundClues = {
        "Le impronte puntano chiaramente verso NORD.\nIl fango e' ancora fresco, la preda e' passata da poco.",
        "Segni di scavo nel terreno, il cinghiale ha cercato radici.\nLe tracce vanno verso EST, verso il sole che sorge.",
        "Trovi escrementi freschi. Il cinghiale si dirige verso SUD,\ndove senti il rumore dell'acqua.",
        "Le foglie calpestate formano un sentiero chiaro verso OVEST.\nLa preda sta tornando verso la sua tana."
    };

    private final String[] soundClues = {
        "Senti grugniti in lontananza, provenienti da NORD.\nGli uccelli volano via spaventati in quella direzione.",
        "Un fruscio di foglie verso EST. Qualcosa di grosso\nsi muove tra i cespugli da quella parte.",
        "Il rumore dell'acqua viene da SUD.\nSenti anche dei grugniti sommessi da quella direzione.",
        "Rami che si spezzano verso OVEST!\nLa preda e' molto vicina, puoi quasi sentirla respirare."
    };

    /** Crea il minigioco gia' pronto alla prima fase. */
    public JungleTrackingGame() {
        reset();
    }

    @Override
    public String getName() {
        return "Caccia nella Giungla";
    }

    @Override
    public String getInstructions() {
        return "========================================\n" +
               "  CACCIA NELLA GIUNGLA\n" +
               "========================================\n\n" +
               "Devi seguire le tracce del cinghiale!\n" +
               "Cerca un indizio per capire\n" +
               "la DIREZIONE giusta (NORD/SUD/EST/OVEST).\n\n" +
               "1. Premi ESAMINA oppure ASCOLTA.\n" +
               "2. Leggi l'indizio: dira' una direzione precisa.\n" +
               "3. Premi DIREZIONE e scrivi NORD, SUD, EST o OVEST.\n\n" +
               "A = Esamina il terreno\n" +
               "B = Ascolta i suoni\n" +
               "C = Inserisci la direzione\n\n" +
               "Hai 3 errori massimo prima di perdere la preda!\n" +
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
                if (waitingForDirection) return "Stai gia' inserendo una direzione! Scrivi NORD, SUD, EST o OVEST.";
                if (examinedGround) return "Hai gia' esaminato il terreno.\n" + groundClues[currentStep];
                examinedGround = true;
                return "Ti abbassi ad esaminare il terreno...\n\n" + groundClues[currentStep];
            case "B":
                if (waitingForDirection) return "Stai gia' inserendo una direzione! Scrivi NORD, SUD, EST o OVEST.";
                if (listenedSounds) return "Hai gia' ascoltato i suoni.\n" + soundClues[currentStep];
                listenedSounds = true;
                return "Ti fermi e tendi l'orecchio...\n\n" + soundClues[currentStep];
            case "C":
                if (!examinedGround && !listenedSounds) {
                    return "Prima cerca un indizio: premi ESAMINA o ASCOLTA.\n" +
                           "Poi premi DIREZIONE e scrivi NORD, SUD, EST o OVEST.";
                }
                waitingForDirection = true;
                return "In che direzione vai?\nScrivi: NORD, SUD, EST o OVEST";
            default:
                return "Usa i pulsanti A, B o C!";
        }
    }

    @Override
    public String handleTextInput(String text) {
        if (state != MiniGameState.IN_PROGRESS) return getCurrentDisplay();

        String dir = text.trim().toLowerCase();

        if (!dir.equals("nord") && !dir.equals("sud") && !dir.equals("est") && !dir.equals("ovest")) {
            return "Direzione non valida! Scrivi: NORD, SUD, EST o OVEST";
        }

        waitingForDirection = false;

        if (dir.equals(correctDirections[currentStep])) {
            currentStep++;
            examinedGround = false;
            listenedSounds = false;

            if (currentStep >= TOTAL_STEPS) {
                state = MiniGameState.WON;
                return "========================================\n" +
                       "  PRESO! Il cinghiale e' tuo!\n" +
                       "========================================\n\n" +
                       "Dopo un inseguimento nella giungla,\n" +
                       "riesci finalmente a catturare la preda!\n" +
                       "Stasera si mangia bene al campo!\n\n" +
                       "CACCIA COMPLETATA CON SUCCESSO!";
            }

            return "Giusto! Le tracce continuano...\n\n" +
                   "--- Fase " + (currentStep + 1) + "/" + TOTAL_STEPS + " ---\n\n" +
                   baseClues[currentStep];
        } else {
            errors++;
            if (errors >= MAX_ERRORS) {
                state = MiniGameState.LOST;
                return "========================================\n" +
                       "  PREDA PERSA!\n" +
                       "========================================\n\n" +
                       "Hai perso le tracce troppe volte.\n" +
                       "Il cinghiale e' scappato nella giungla.\n" +
                       "Oggi niente carne fresca...";
            }
            return "Direzione sbagliata! Il cinghiale non e' andato a " + dir.toUpperCase() + ".\n" +
                   "Errori: " + errors + "/" + MAX_ERRORS + "\n" +
                   "Cerca meglio gli indizi!";
        }
    }

    @Override
    public String getCurrentDisplay() {
        switch (state) {
            case PENDING:
                return getInstructions();
            case WON:
                return "Caccia completata! Il cinghiale e' stato catturato!";
            case LOST:
                return "La preda e' scappata...";
            default:
                String display = "--- Caccia nella Giungla ---\n" +
                       "Errori: " + errors + "/" + MAX_ERRORS + "\n\n" +
                       baseClues[currentStep];
                if (waitingForDirection) {
                    display += "\n\nIn che direzione vai? (NORD/SUD/EST/OVEST)";
                } else if (!examinedGround && !listenedSounds) {
                    display += "\n\nCosa fare adesso:\n" +
                               "1. Premi ESAMINA o ASCOLTA per trovare la direzione.\n" +
                               "2. Premi DIREZIONE.\n" +
                               "3. Scrivi NORD, SUD, EST o OVEST.";
                } else {
                    display += "\n\nHai un indizio. Ora premi DIREZIONE e scrivi la strada giusta.";
                }
                return display;
        }
    }

    @Override
    public String getButtonALabel() { return "Esamina"; }
    @Override
    public String getButtonBLabel() { return "Ascolta"; }
    @Override
    public String getButtonCLabel() { return "Direzione"; }

    @Override
    public void reset() {
        state = MiniGameState.IN_PROGRESS;
        currentStep = 0;
        errors = 0;
        examinedGround = false;
        listenedSounds = false;
        waitingForDirection = false;
    }
}
