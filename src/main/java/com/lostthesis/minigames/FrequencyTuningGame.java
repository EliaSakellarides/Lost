package com.lostthesis.minigames;

/**
 * Capitolo 9 - Sintonizza la Radio (Stazione Cigno)
 * Trova la frequenza 108.4 MHz. Barra segnale ASCII.
 * Bottoni: A = scan su (+0.1), B = scan giu' (-0.1), C = inserisci frequenza
 * Max 20 tentativi prima che l'apparecchio si surriscaldi.
 * 108 = 4+8+15+16+23+42
 */
public class FrequencyTuningGame implements MiniGame {
    private MiniGameState state;
    private double currentFrequency;
    private int attempts;
    private static final int MAX_ATTEMPTS = 20;
    private static final double TARGET_FREQUENCY = 108.4;
    private boolean waitingForFrequency;

    public FrequencyTuningGame() {
        reset();
    }

    @Override
    public String getName() {
        return "Sintonizza la Radio";
    }

    @Override
    public String getInstructions() {
        return "========================================\n" +
               "  SINTONIZZA LA RADIO - STAZIONE CIGNO\n" +
               "========================================\n\n" +
               "La radio della stazione Il Cigno deve essere\n" +
               "sintonizzata sulla frequenza giusta!\n\n" +
               "A = Scan su (+0.1 MHz)\n" +
               "B = Scan giu' (-0.1 MHz)\n" +
               "C = Inserisci frequenza esatta\n\n" +
               "Suggerimento: i numeri DHARMA sommano a...\n" +
               "4 + 8 + 15 + 16 + 23 + 42 = ???\n\n" +
               "Hai " + MAX_ATTEMPTS + " tentativi prima che\n" +
               "l'apparecchio si surriscaldi!\n" +
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
                if (waitingForFrequency) return "Stai inserendo una frequenza! Scrivi il valore.";
                currentFrequency = Math.round((currentFrequency + 0.1) * 10.0) / 10.0;
                attempts++;
                return checkFrequency();
            case "B":
                if (waitingForFrequency) return "Stai inserendo una frequenza! Scrivi il valore.";
                currentFrequency = Math.round((currentFrequency - 0.1) * 10.0) / 10.0;
                attempts++;
                return checkFrequency();
            case "C":
                waitingForFrequency = true;
                return "Inserisci la frequenza (es: 108.4):";
            default:
                return "Usa i pulsanti A, B o C!";
        }
    }

    @Override
    public String handleTextInput(String text) {
        if (state != MiniGameState.IN_PROGRESS) return getCurrentDisplay();

        try {
            String cleaned = text.trim().replace(",", ".");
            double freq = Double.parseDouble(cleaned);
            currentFrequency = Math.round(freq * 10.0) / 10.0;
            attempts++;
            waitingForFrequency = false;
            return checkFrequency();
        } catch (NumberFormatException e) {
            return "Frequenza non valida! Inserisci un numero (es: 108.4)";
        }
    }

    private String checkFrequency() {
        if (Math.abs(currentFrequency - TARGET_FREQUENCY) < 0.05) {
            state = MiniGameState.WON;
            return "========================================\n" +
                   "  SEGNALE TROVATO! 108.4 MHz!\n" +
                   "========================================\n\n" +
                   getSignalBar(0) + "\n\n" +
                   "KRRR... *voce nella radio*\n" +
                   "'Qui stazione Cigno... le coordinate sono...'\n\n" +
                   "108 = 4+8+15+16+23+42\n" +
                   "I numeri DHARMA ti hanno guidato!\n\n" +
                   "SINTONIZZAZIONE COMPLETATA!";
        }

        if (attempts >= MAX_ATTEMPTS) {
            state = MiniGameState.LOST;
            return "========================================\n" +
                   "  APPARECCHIO SURRISCALDATO!\n" +
                   "========================================\n\n" +
                   "Troppi tentativi! La radio emette fumo\n" +
                   "e si spegne con un sibilo.\n" +
                   "La frequenza era 108.4 MHz...";
        }

        double distance = Math.abs(currentFrequency - TARGET_FREQUENCY);
        return getFrequencyDisplay(distance);
    }

    private String getFrequencyDisplay(double distance) {
        String display = "--- Sintonizza la Radio ---\n" +
               "Frequenza: " + String.format("%.1f", currentFrequency) + " MHz\n" +
               "Tentativi: " + attempts + "/" + MAX_ATTEMPTS + "\n\n" +
               getSignalBar(distance) + "\n\n";

        if (distance > 20) {
            display += "Nessun segnale... solo statica.";
        } else if (distance > 10) {
            display += "KRRR... senti qualcosa in lontananza...";
        } else if (distance > 5) {
            display += "...voce... frammentata... quasi...";
        } else if (distance > 2) {
            display += "La voce e' piu' chiara! Ci sei quasi!";
        } else if (distance > 0.5) {
            display += "MOLTO VICINO! Il segnale e' forte!";
        } else {
            display += "VICINISSIMO! Aggiusta di poco!";
        }

        return display;
    }

    private String getSignalBar(double distance) {
        int bars;
        if (distance < 0.2) bars = 20;
        else if (distance < 0.5) bars = 18;
        else if (distance < 1) bars = 15;
        else if (distance < 2) bars = 12;
        else if (distance < 5) bars = 8;
        else if (distance < 10) bars = 5;
        else if (distance < 20) bars = 2;
        else bars = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("Segnale: [");
        for (int i = 0; i < 20; i++) {
            sb.append(i < bars ? "|" : " ");
        }
        sb.append("] ");
        sb.append(bars * 5).append("%");
        return sb.toString();
    }

    @Override
    public String getCurrentDisplay() {
        switch (state) {
            case PENDING:
                return getInstructions();
            case WON:
                return "Radio sintonizzata su 108.4 MHz!";
            case LOST:
                return "L'apparecchio si e' surriscaldato...";
            default:
                if (waitingForFrequency) return "Inserisci la frequenza (es: 108.4):";
                double distance = Math.abs(currentFrequency - TARGET_FREQUENCY);
                return getFrequencyDisplay(distance);
        }
    }

    @Override
    public String getButtonALabel() { return "Scan +"; }
    @Override
    public String getButtonBLabel() { return "Scan -"; }
    @Override
    public String getButtonCLabel() { return "Frequenza"; }

    @Override
    public void reset() {
        state = MiniGameState.IN_PROGRESS;
        currentFrequency = 88.0;
        attempts = 0;
        waitingForFrequency = false;
    }
}
