package com.lostthesis.engine;

import com.lostthesis.model.*;
import com.lostthesis.audio.AudioManager;
import com.lostthesis.minigames.*;
import com.lostthesis.save.GameState;
import com.lostthesis.save.GameSave;
import com.lostthesis.save.GameSaveInstance;
import com.lostthesis.save.ItemData;
import java.util.*;

/**
 * Motore di gioco principale per Lost Thesis
 * Gestisce la storia, i capitoli e le interazioni
 */
public class GameEngine {
    private Player player;
    private Map<String, Room> allRooms;
    private Room startRoom;
    private boolean gameRunning;
    private boolean gameWon;
    private List<String> gameLog;
    
    // Modalità narrativa LOST
    private boolean narrativeMode;
    private List<Level> storyChapters;
    private int currentChapter;
    private boolean currentChapterCompleted;
    private boolean currentChapterStarted;
    
    // Audio manager
    private AudioManager audioManager;
    
    // I numeri misteriosi di LOST
    private static final int[] NUMBERS = {4, 8, 15, 16, 23, 42};

    // Parser comandi con alias
    private final CommandParser commandParser;
    
    // TIMER per eventi temporizzati (stile guida Colombini)
    private int smokeMonsterTimer = 0;      // Mostro di fumo si avvicina
    private int dynamiteTimer = 0;          // Dinamite attivata
    private int othersTimer = 0;            // Gli Altri ti cercano
    private boolean dynamiteActive = false;
    private boolean smokeMonsterNearby = false;
    
    // Variabili di stato per eventi (v1...v9 dalla guida)
    private boolean hatchOpened = false;
    private boolean blackRockExplored = false;
    private boolean jacobMet = false;
    private boolean templeBathed = false;

    // Mini giochi
    private MiniGame activeMiniGame;
    private Map<String, MiniGame> miniGames;
    private boolean miniGameIntroShown;
    private boolean miniGameOutroShown;
    
    public GameEngine() {
        this.allRooms = new HashMap<>();
        this.gameLog = new ArrayList<>();
        this.gameRunning = false;
        this.gameWon = false;
        this.narrativeMode = true;
        this.storyChapters = new ArrayList<>();
        this.currentChapter = 0;
        this.currentChapterCompleted = false;
        this.currentChapterStarted = false;
        this.audioManager = new AudioManager();
        this.commandParser = new CommandParser();
        this.activeMiniGame = null;
        this.miniGameIntroShown = false;
        this.miniGameOutroShown = false;
        this.miniGames = new HashMap<>();
        miniGames.put("smoke_chase", new SmokeMonsterChase());
        miniGames.put("jungle_tracking", new JungleTrackingGame());
        miniGames.put("dynamite_defusal", new DynamiteDefusalGame());
        miniGames.put("frequency_tuning", new FrequencyTuningGame());
        miniGames.put("morse_code", new MorseCodeGame());
    }
    
    /**
     * Inizializza una nuova partita: crea il mondo, i capitoli,
     * posiziona il giocatore sulla spiaggia e avvia la colonna sonora.
     */
    public void initializeGame(String playerName) {
        player = new Player(playerName);
        createWorld();
        createStoryChapters();
        player.setCurrentRoom(startRoom);
        gameRunning = true;
        
        // 🎵 Avvia la sigla di LOST! (suona per 15 secondi, poi fade out)
        audioManager.playBackgroundMusic("lost___opening_titles.wav", false, 15000);
        
        addLog("═══════════════════════════════════════════════════");
        addLog("   OCEANIC FLIGHT 815 - GIORNO 1");
        addLog("═══════════════════════════════════════════════════");
        addLog("");
        addLog("Sei vivo, " + playerName + ". Non tutti lo sono.");
        addLog("");
        addLog("Il mondo sa che siamo spariti.");
        addLog("L'aereo e' atterrato su quest'isola.");
        addLog("E' solo questione di ore prima che qualcuno venga a salvarci.");
        addLog("");
        addLog("Devi solo resistere.");
        addLog("═══════════════════════════════════════════════════");
    }

    private void createStoryChapters() {
        storyChapters.clear();
        
        // ═══════════════════════════════════════════════════════════════
        // CAPITOLI RIVISITATI - Focus su SOPRAVVIVENZA e cronologia LOST
        // ═══════════════════════════════════════════════════════════════
        
        // CAPITOLO 1: LA PRIMA NOTTE
        Map<String, String> cap1Choices = new HashMap<>();
        cap1Choices.put("A", "Accendere un fuoco");
        cap1Choices.put("B", "Costruire un riparo");
        cap1Choices.put("C", "Esplorare i dintorni");
        storyChapters.add(new Level(
            "cap1_firstnight",
            "La Prima Notte",
            "\uD83C\uDF19 LA PRIMA NOTTE SULL'ISOLA\n\n" +
            "Il sole sta tramontando. La spiaggia si fa buia.\n" +
            "I sopravvissuti si guardano intorno, spaventati.\n" +
            "Dalla giungla arrivano suoni inquietanti...\n\n" +
            "\u2753 Come vi preparate per la notte?",
            cap1Choices,
            "A",
            "Il fuoco tiene lontani i predatori e solleva il morale!"
        ));
        
        // CAPITOLO 2: I SOPRAVVISSUTI - Organizzare il campo
        Map<String, String> cap2Choices = new HashMap<>();
        cap2Choices.put("A", "48");
        cap2Choices.put("B", "23");
        cap2Choices.put("C", "108");
        storyChapters.add(new Level(
            "cap2_survivors",
            "I Sopravvissuti",
            "🏝️ Sei sulla spiaggia con gli altri sopravvissuti.\n\n" +
            player.getName() + ", il medico, sta organizzando il campo.\n" +
            "Kate raccoglie provviste dai rottami.\n" +
            "Sawyer sta già litigando con qualcuno...\n\n" +
            "Qualcuno chiede: 'Quanti siamo sopravvissuti?'\n\n" +
            "❓ Quanti passeggeri sono sopravvissuti allo schianto?",
            cap2Choices,
            "A",
            "48 sopravvissuti iniziali!"
        ));
        
        // CAPITOLO 3: IL MOSTRO DI FUMO - Cabina di pilotaggio + morte del pilota
        Map<String, String> cap3Choices = new HashMap<>();
        cap3Choices.put("A", "Corri via!");
        cap3Choices.put("B", "Resta immobile");
        cap3Choices.put("C", "Arrampicati su un albero");
        storyChapters.add(new Level(
            "cap3_smoke",
            "Il Mostro di Fumo",
            "🌴 GIORNO 1 - LA GIUNGLA\n\n" +
            "Ti avventuri nella giungla con Jack e Kate.\n" +
            "Trovate la cabina di pilotaggio schiantata tra gli alberi.\n" +
            "Il pilota e' ancora vivo!\n\n" +
            "Tenta di chiamare i soccorsi con la radio danneggiata.\n" +
            "Tra i disturbi, una voce inquietante:\n" +
            "\"...esistono altri su quest'isola...\n" +
            "ci vivono da molto prima di voi...\"\n\n" +
            "Poi il TICK... TICK... TICK... riempie la giungla.\n" +
            "Il Mostro di Fumo attacca. Il pilota viene trascinato via.\n" +
            "La radio e' distrutta. Gli Altri esistono.\n\n" +
            "❓ Cosa fai quando senti arrivare il Mostro?",
            cap3Choices,
            "B",
            "Chi si muove attira il mostro. Restare immobili e' l'unica via."
        ));
        
        // CAPITOLO 4: LE GROTTE - Trovare acqua (NUOVO)
        Map<String, String> cap4Choices = new HashMap<>();
        cap4Choices.put("A", "Trasferirsi alle grotte");
        cap4Choices.put("B", "Restare sulla spiaggia");
        cap4Choices.put("C", "Dividere il gruppo");
        storyChapters.add(new Level(
            "cap4_caves",
            "Le Grotte",
            "💧 GIORNO 3 - EMERGENZA ACQUA\n\n" +
            "L'acqua delle bottiglie sta finendo.\n" +
            player.getName() + " ha trovato delle GROTTE con acqua dolce!\n\n" +
            "Ma sono nella giungla, lontano dalla spiaggia...\n" +
            "Lontano dai possibili soccorsi.\n\n" +
            "Il gruppo è diviso. Cosa proponi?\n\n" +
            "❓ Qual è la scelta migliore per sopravvivere?",
            cap4Choices,
            "C",
            "Dividere il gruppo: alcuni alle grotte per l'acqua, altri sulla spiaggia per i soccorsi!"
        ));
        
        // CAPITOLO 5: LA CACCIA - Trovare cibo + MINI GIOCO
        Map<String, String> cap5Choices = new HashMap<>();
        cap5Choices.put("A", "Cacciare i cinghiali");
        cap5Choices.put("B", "Pescare nel mare");
        cap5Choices.put("C", "Raccogliere frutta");
        Level cap5 = new Level(
            "cap5_hunt",
            "La Caccia",
            "🐗 GIORNO 5 - IL CIBO SCARSEGGIA\n\n" +
            "Le provviste dell'aereo sono quasi finite.\n" +
            "Locke ha visto dei CINGHIALI nella giungla.\n" +
            "Jin sa pescare. Hurley ha trovato alberi di frutta.\n\n" +
            "Servono proteine per sopravvivere a lungo termine.\n\n" +
            "❓ Come procurarsi il cibo?",
            cap5Choices,
            "A",
            "I cinghiali sono la fonte di proteine più affidabile sull'isola!"
        );
        cap5.setMiniGameKey("jungle_tracking");
        storyChapters.add(cap5);
        
        // CAPITOLO 6: LA BOTOLA - Scoperta (non aperta)
        Map<String, String> cap6Choices = new HashMap<>();
        cap6Choices.put("A", "Provare ad aprirla");
        cap6Choices.put("B", "Lasciare perdere");
        cap6Choices.put("C", "Cercare un altro modo");
        storyChapters.add(new Level(
            "cap6_hatch",
            "La Botola",
            "🚪 GIORNO 8 - LA SCOPERTA DI LOCKE\n\n" +
            "Locke e Boone hanno trovato qualcosa nella giungla!\n" +
            "Una BOTOLA di metallo sepolta nel terreno.\n\n" +
            "C'è scritto 'QUARANTINE' e un simbolo: DHARMA.\n" +
            "È sigillata, impossibile aprirla a mani nude.\n\n" +
            "Locke è ossessionato. Vuole sapere cosa c'è sotto.\n\n" +
            "❓ Cosa consigli?",
            cap6Choices,
            "C",
            "Serve qualcosa di potente per aprirla... come degli ESPLOSIVI!"
        ));
        
        // CAPITOLO 7: LA ROCCIA NERA - Trovare la dinamite nella stiva
        Map<String, String> cap7Choices = new HashMap<>();
        cap7Choices.put("A", "Aprire la cassa nella stiva");
        cap7Choices.put("B", "Lasciare perdere e tornare indietro");
        cap7Choices.put("C", "Cercare qualcosa sul ponte");
        storyChapters.add(new Level(
            "cap7_blackrock",
            "La Roccia Nera",
            "⚓ LA NAVE NELLA GIUNGLA\n\n" +
            "Rousseau vi ha parlato della ROCCIA NERA.\n" +
            "Una nave del 1800 arenata nel mezzo dell'isola!\n\n" +
            "Nella stiva senti odore di polvere e salsedine.\n" +
            "Tra casse marce e catene arrugginite, noti un baule inchiodato.\n\n" +
            "Locke ti guarda: 'Quello non e' finito qui per caso.'\n" +
            "Potrebbe esserci proprio l'esplosivo che vi serve per la botola.\n\n" +
            "❓ Cosa fai?",
            cap7Choices,
            "A",
            "La cosa piu' promettente e' quella cassa chiusa nella stiva."
        ));
        
        // CAPITOLO 8: APRIRE LA BOTOLA
        Map<String, String> cap8Choices = new HashMap<>();
        cap8Choices.put("A", "Spegnere subito la miccia");
        cap8Choices.put("B", "Allontanarsi e cercare riparo");
        cap8Choices.put("C", "Restare vicino alla botola per osservare");
        storyChapters.add(new Level(
            "cap8_openhatch",
            "Aprire la Botola",
            "💥 IL MOMENTO DELLA VERITÀ\n\n" +
            "Siete tornati alla botola con la dinamite.\n" +
            "Locke sistema la carica sulla botola e accende la miccia.\n" +
            "Hurley impallidisce vedendo i numeri incisi sul metallo:\n" +
            "4 8 15 16 23 42.\n\n" +
            "'Non mi piacciono per niente...'\n" +
            "La miccia sfrigola. Hai solo pochi secondi.\n\n" +
            "❓ Cosa fai?",
            cap8Choices,
            "B",
            "Con della dinamite accesa, la scelta giusta e' mettersi al riparo."
        ));
        
        // CAPITOLO 9: IL CIGNO - Desmond + MINI GIOCO
        Map<String, String> cap9Choices = new HashMap<>();
        cap9Choices.put("A", "Premere il pulsante");
        cap9Choices.put("B", "Non premere");
        cap9Choices.put("C", "Chiedere spiegazioni");
        Level cap9 = new Level(
            "cap9_swan",
            "La Stazione Il Cigno",
            "🦢 DENTRO LA BOTOLA\n\n" +
            "Scendete nella stazione sotterranea.\n" +
            "Trovi un uomo, DESMOND, che vive qui da 3 anni!\n\n" +
            "'Brother! Finalmente qualcuno!'\n" +
            "'Devo premere il pulsante ogni 108 minuti!'\n" +
            "'Se non lo faccio... il mondo finisce!'\n\n" +
            "Un timer sta per scadere: 00:01:30\n" +
            "Il computer mostra: 4 8 15 16 23 42\n\n" +
            "❓ Il timer sta per scadere! Cosa fai?",
            cap9Choices,
            "A",
            "Meglio non rischiare... per ora!"
        );
        cap9.setMiniGameKey("frequency_tuning");
        storyChapters.add(cap9);

        // CAPITOLO 10: HENRY GALE - Il prigioniero misterioso
        Map<String, String> cap10Choices = new HashMap<>();
        cap10Choices.put("A", "Si, dice la verita'");
        cap10Choices.put("B", "No, sta mentendo");
        cap10Choices.put("C", "Non sei sicuro");
        storyChapters.add(new Level(
            "cap10_henrygale",
            "Il Prigioniero",
            "🎭 UN UOMO NELLA TRAPPOLA\n\n" +
            "Trovate un uomo catturato nella trappola di Rousseau.\n" +
            "Si presenta con calma:\n\n" +
            "\"Mi chiamo Henry Gale.\n" +
            "Sono un pallonista del Minnesota.\n" +
            "Il mio pallone si e' schiantato sull'isola mesi fa.\"\n\n" +
            "La storia sembra plausibile. E' ferito, sembra spaventato.\n" +
            "Ma i suoi occhi... non corrispondono alle parole.\n\n" +
            "❓ Ti fidi di lui?",
            cap10Choices,
            "B",
            "Henry Gale non esiste. Quest'uomo e' Ben Linus, il leader degli Altri."
        ));

        // CAPITOLO 11: GLI ALTRI - Cattura
        Map<String, String> cap11aChoices = new HashMap<>();
        cap11aChoices.put("A", "Collaborare");
        cap11aChoices.put("B", "Resistere");
        cap11aChoices.put("C", "Cercare di fuggire");
        storyChapters.add(new Level(
            "cap11_others",
            "Gli Altri",
            "👥 CATTURATO!\n\n" +
            "Durante una spedizione, vieni catturato dagli ALTRI!\n" +
            "Ti portano in un villaggio nascosto.\n\n" +
            "Ben ti guarda con calma - lo stesso uomo del bunker.\n" +
            "'Sappiamo tutto di te.'\n" +
            "'Sappiamo perche' sei su quest'isola.'\n" +
            "'La domanda e': tu lo sai?'\n\n" +
            "❓ Come reagisci?",
            cap11aChoices,
            "C",
            "Mai fidarsi degli Altri... cerca un modo per scappare!"
        ));
        
        // CAPITOLO 11: LA FUGA DAGLI ALTRI
        Map<String, String> cap11Choices = new HashMap<>();
        cap11Choices.put("A", "Attraverso la giungla");
        cap11Choices.put("B", "Seguire il fiume");
        cap11Choices.put("C", "Verso la costa");
        storyChapters.add(new Level(
            "cap11_escape_others",
            "La Fuga",
            "🏃 DEVI SCAPPARE!\n\n" +
            "Riesci a liberarti dalle corde durante la notte.\n" +
            "Il villaggio è silenzioso, le guardie distratte.\n\n" +
            "Hai tre vie di fuga possibili:\n" +
            "• La giungla - pericolosa ma diretta\n" +
            "• Il fiume - più lungo ma facile da seguire\n" +
            "• La costa - esposto ma familiare\n\n" +
            "Il Mostro di Fumo è stato visto nella giungla...\n\n" +
            "❓ Quale strada prendi?",
            cap11Choices,
            "B",
            "Il fiume ti riporterà al campo... e l'acqua copre le tue tracce!"
        ));
        
        // CAPITOLO 13: LA ZATTERA - Partenza
        Map<String, String> cap12Choices = new HashMap<>();
        cap12Choices.put("A", "Partire con la zattera");
        cap12Choices.put("B", "Restare sull'isola");
        cap12Choices.put("C", "Aspettare i soccorsi");
        storyChapters.add(new Level(
            "cap12_raft",
            "La Zattera",
            "⛵ IL PIANO DI MICHAEL\n\n" +
            "Michael ha costruito una zattera in poche settimane.\n" +
            "Jin, Sawyer e Walt partiranno con lui.\n\n" +
            "E' l'unica via concreta per uscire dall'isola.\n" +
            "L'oceano e' immenso, ma restare non e' piu' un'opzione.\n\n" +
            "❓ Cosa fai?",
            cap12Choices,
            "A",
            "Meglio rischiare il mare che restare prigionieri sull'isola!"
        ));

        // CAPITOLO 14: IL RAPIMENTO DI WALT
        storyChapters.add(new Level(
            "cap13_walt",
            "In Mare Aperto",
            "🌊 NOTTE - OCEANO APERTO\n\n" +
            "La zattera naviga da ore nel buio.\n" +
            "Poi una barca si avvicina a tutta velocita'.\n\n" +
            "\"Avete visto un bambino?\"\n\n" +
            "Prima che possiate rispondere, sparano.\n" +
            "La zattera esplode.\n\n" +
            "Walt viene RAPITO dagli Altri.\n" +
            "Michael urla il suo nome nel buio.\n" +
            "Sawyer e' ferito. Siete in acqua.\n\n" +
            "❓ Come tornate a riva?",
            Arrays.asList("nuotare", "nuoto", "a nuoto", "nuotiamo", "a"),
            "L'unica via e' tornare a nuoto verso l'isola."
        ));

        // CAPITOLO 15: I FLASHBACK - Ricordi
        Map<String, String> cap13Choices = new HashMap<>();
        cap13Choices.put("A", "815");
        cap13Choices.put("B", "316");
        cap13Choices.put("C", "777");
        storyChapters.add(new Level(
            "cap13_flashback",
            "Flashback",
            "💭 QUELLA NOTTE... UN SOGNO\n\n" +
            "Ti ricordi di quando sei salito sull'aereo.\n" +
            "L'aeroporto di Sydney era affollato.\n\n" +
            "Avevi con te una TESI importante...\n" +
            "Dovevi consegnarla a Los Angeles per la laurea.\n" +
            "Era il tuo lavoro di anni!\n\n" +
            "La tesi... DOVE L'HAI MESSA?\n" +
            "Forse è ancora nei rottami dell'aereo?\n\n" +
            "❓ Qual era il numero del volo Oceanic?",
            cap13Choices,
            "A",
            "Oceanic Flight 815!"
        ));
        
        // CAPITOLO 14: LA SCOPERTA DELLA TESI + MINI GIOCO
        Level cap14 = new Level(
            "cap14_thesis",
            "La Scoperta",
            "📜 NEL BUNKER DHARMA...\n\n" +
            "Esplori la stazione Il Cigno più a fondo.\n" +
            "Trovi una stanza segreta dietro una parete!\n\n" +
            "All'interno... documenti DHARMA!\n" +
            "E tra questi... una MAPPA!\n\n" +
            "📖 'COORDINATE: PISTA DI ATTERRAGGIO HYDRA'\n" +
            "'Per emergenze. Aereo funzionante.'\n\n" +
            "C'è un AEREO nascosto sull'isola!\n\n" +
            "❓ Digita 'prendi' per prendere la mappa!",
            Arrays.asList("prendi", "raccogli", "ok", "si", "a"),
            "Prendila!"
        );
        cap14.setMiniGameKey("morse_code");
        storyChapters.add(cap14);
        
        // CAPITOLO 15: LA PISTA NASCOSTA
        Map<String, String> cap15Choices = new HashMap<>();
        cap15Choices.put("A", "Andare subito");
        cap15Choices.put("B", "Prepararsi bene");
        cap15Choices.put("C", "Portare tutti");
        storyChapters.add(new Level(
            "cap15_runway",
            "La Pista Nascosta",
            "🛬 LA SPERANZA!\n\n" +
            "Segui le coordinate della mappa.\n" +
            "Attraversi territori pericolosi.\n" +
            "Il Mostro di Fumo ruggisce in lontananza.\n\n" +
            "Ma finalmente... LA VEDI!\n" +
            "Una pista di atterraggio nascosta!\n" +
            "E un piccolo AEREO Cessna sotto un telo!\n\n" +
            "❓ Come procedi?",
            cap15Choices,
            "B",
            "Meglio prepararsi: carburante, provviste, e verificare l'aereo!"
        ));
        
        // CAPITOLO 16: PREPARAZIONE AL VOLO
        storyChapters.add(new Level(
            "cap16_prep",
            "Preparazione al Volo",
            "✈️ L'AEREO!\n\n" +
            "È un Cessna 172, danneggiato ma riparabile.\n" +
            "Trovi un manuale con le istruzioni di avvio.\n\n" +
            "Serve un codice per sbloccare l'accensione.\n" +
            "Sul manuale c'è scritto:\n" +
            "'Codice: somma DHARMA diviso 2'\n\n" +
            "4 + 8 + 15 + 16 + 23 + 42 = 108\n" +
            "108 / 2 = ?\n\n" +
            "❓ Qual è il codice di accensione?",
            Arrays.asList("54", "cinquantaquattro"),
            "108 diviso 2..."
        ));
        
        // CAPITOLO 17: LA FUGA FINALE
        Map<String, String> cap17Choices = new HashMap<>();
        cap17Choices.put("A", "Decollare ORA!");
        cap17Choices.put("B", "Aspettare gli altri");
        cap17Choices.put("C", "Tornare indietro");
        storyChapters.add(new Level(
            "cap17_escape",
            "La Fuga",
            "🛫 IL MOMENTO È ARRIVATO!\n\n" +
            "Il motore si accende! L'elica gira!\n" +
            "Ma qualcosa non va...\n\n" +
            "Il MOSTRO DI FUMO appare dalla giungla!\n" +
            "Gli ALTRI corrono verso la pista!\n" +
            "Ben grida: 'NON PUOI ANDARTENE!'\n\n" +
            "Hai solo pochi secondi per decidere!\n\n" +
            "❓ Cosa fai?",
            cap17Choices,
            "A",
            "Non c'è tempo! DECOLLA!"
        ));
        
        // CAPITOLO 18: LIBERTÀ - FINALE
        storyChapters.add(new Level(
            "cap18_freedom",
            "Libertà",
            "🌅 CE L'HAI FATTA!\n\n" +
            "L'aereo decolla, lasciandoti alle spalle l'isola.\n" +
            "Il Mostro di Fumo ruggisce impotente sotto di te.\n" +
            "Gli Altri diventano puntini sulla pista.\n\n" +
            "Sotto di te, l'isola diventa sempre più piccola...\n" +
            "Finché non scompare all'orizzonte.\n\n" +
            "🌊 L'oceano infinito si stende davanti a te.\n" +
            "Sei LIBERO. Finalmente LIBERO!\n\n" +
            "🎓 E la tua TESI? Ce l'hai fatta!\n\n" +
            "❓ Digita 'fine' per concludere.",
            Arrays.asList("fine", "finito", "ok", "si", "a"),
            "È finita... o forse no?"
        ));
    }

    private void createWorld() {
        // SPIAGGIA - Punto di partenza
        Room spiaggia = new Room("spiaggia", "🏖️ Spiaggia dello Schianto",
            "La spiaggia è coperta di rottami dell'aereo. " +
            "Il fumo sale ancora dalla fusoliera. " +
            "Il mare è calmo, ma la giungla dietro di te sembra ostile.");
        
        // GIUNGLA
        Room giungla = new Room("giungla", "🌴 Giungla Oscura",
            "Alberi altissimi bloccano la luce del sole. " +
            "Senti strani rumori... ticchettii meccanici. " +
            "Qualcosa di grosso si muove tra gli alberi.");
        giungla.setDangerous(true, "Il Mostro di Fumo potrebbe essere qui...");
        
        // BOTOLA
        Room botola = new Room("botola", "🚪 La Botola (Il Cigno)",
            "Una stazione sotterranea della DHARMA Initiative. " +
            "Computer antiquati, un pulsante misterioso, e molto cibo in scatola. " +
            "Desmond viveva qui, premendo il pulsante ogni 108 minuti.");
        
        // VILLAGGIO DEGLI ALTRI
        Room villaggio = new Room("villaggio", "🏘️ Villaggio degli Altri",
            "Un villaggio apparentemente normale nel mezzo dell'isola. " +
            "Case bianche, giardini curati... ma qualcosa non quadra. " +
            "Gli abitanti ti osservano con sospetto.");
        
        // TEMPIO
        Room tempio = new Room("tempio", "🏛️ Il Tempio",
            "Un antico tempio nascosto nella giungla. " +
            "I guardiani proteggono questo luogo sacro. " +
            "Al centro c'è una vasca con acqua misteriosa.");
        
        // ROCCIA NERA
        Room rocciaNera = new Room("roccianera", "⚓ La Roccia Nera",
            "Una nave schiavista del 1800 in mezzo alla giungla! " +
            "Come ci è arrivata? È piena di dinamite instabile. " +
            "Nei diari trovi mappe dell'isola.");
        
        // FARO
        Room faro = new Room("faro", "🗼 Il Faro",
            "Un faro antico sulla scogliera. " +
            "All'interno, uno specchio magico mostra luoghi lontani. " +
            "Jacob usava questo posto per osservare i candidati.");
        
        // PISTA
        Room pista = new Room("pista", "🛬 Pista di Atterraggio",
            "Una pista nascosta tra le palme! " +
            "C'è un piccolo aereo Cessna parzialmente coperto. " +
            "Con le giuste istruzioni... potresti farlo volare.");
        
        // Connessioni
        spiaggia.setExit("nord", giungla);
        giungla.setExit("sud", spiaggia);
        giungla.setExit("est", botola);
        giungla.setExit("ovest", rocciaNera);
        giungla.setExit("nord", villaggio);
        botola.setExit("ovest", giungla);
        villaggio.setExit("sud", giungla);
        villaggio.setExit("est", tempio);
        tempio.setExit("ovest", villaggio);
        rocciaNera.setExit("est", giungla);
        rocciaNera.setExit("nord", faro);
        faro.setExit("sud", rocciaNera);
        faro.setExit("nord", pista);
        pista.setExit("sud", faro);
        
        // Aggiungi stanze alla mappa
        allRooms.put("spiaggia", spiaggia);
        allRooms.put("giungla", giungla);
        allRooms.put("botola", botola);
        allRooms.put("villaggio", villaggio);
        allRooms.put("tempio", tempio);
        allRooms.put("roccianera", rocciaNera);
        allRooms.put("faro", faro);
        allRooms.put("pista", pista);
        
        // Oggetti
        spiaggia.addItem(new Item("Acqua", "Bottiglia d'acqua dai rottami", true, 
            Item.ItemType.CIBO, 20, 3));
        spiaggia.addItem(new Item("Kit Medico", "Kit di pronto soccorso", true,
            Item.ItemType.MEDICINA, 50, 2));
        
        botola.addItem(new Item("Cibo DHARMA", "Scatolette con logo DHARMA", true,
            Item.ItemType.CIBO, 30, 5));
        botola.addItem(new Item("Mappa DHARMA", "Mappa delle stazioni sull'isola", true,
            Item.ItemType.DOCUMENTO, 0, -1));
        
        rocciaNera.addItem(new Item("Dinamite", "ATTENZIONE: Altamente instabile!", true,
            Item.ItemType.STRUMENTO, 0, 1));
        rocciaNera.addItem(new Item("Diario", "Diario del capitano con mappe", true,
            Item.ItemType.DOCUMENTO, 0, -1));
        
        faro.addItem(new Item("Bussola", "Una vecchia bussola che punta sempre a nord", true,
            Item.ItemType.STRUMENTO, 0, -1));
        
        // La TESI sarà aggiunta durante il capitolo 13
        
        startRoom = spiaggia;
    }
    
    /**
     * Processa un comando testuale del giocatore e restituisce
     * il testo di risposta da mostrare nella GUI.
     * Delega al mini gioco attivo se presente, altrimenti
     * parsa il comando tramite {@link CommandParser}.
     */
    public String processCommand(String command) {
        if (!gameRunning) {
            return gameWon
                ? "Hai gia' completato l'avventura su LOST."
                : "Il gioco e' terminato!";
        }

        if (command == null) {
            command = "";
        }

        // Se c'è un mini gioco attivo, delega l'input
        if (activeMiniGame != null) {
            return finalizeTurn(processMiniGameInput(command.trim()), true);
        }

        String cmd = command.trim().toLowerCase();
        String response;
        boolean advanceTurn = false;

        // Modalità narrativa
        if (narrativeMode) {
            // Gestione pulsanti rapidi A, B, C (prima del parser)
            if (cmd.equals("a") || cmd.equals("b") || cmd.equals("c")) {
                return finalizeTurn(processChoice(cmd.toUpperCase()), true);
            }

            // Comando speciale: mostra tutti gli alias
            if (cmd.equals("alias") || cmd.equals("aliases") || cmd.equals("sinonimi")) {
                return commandParser.getAliasHelpText();
            }

            // Parsing con alias
            CommandParser.ParsedCommand parsed = commandParser.parse(cmd);
            String target = parsed.getTarget();

            switch (parsed.getType()) {
                case AVANTI:
                    response = startNextChapter();
                    advanceTurn = true;
                    break;

                case RISPONDI:
                    if (target.isEmpty()) {
                        return "Devi scrivere una risposta!";
                    }
                    response = answerChapter(target);
                    advanceTurn = true;
                    break;

                case SCEGLI:
                    if (target.isEmpty()) {
                        return "Devi scegliere A, B o C!";
                    }
                    response = processChoice(target.trim().toUpperCase());
                    advanceTurn = true;
                    break;

                case PRENDI:
                    if (target.isEmpty()) {
                        return "❓ Cosa vuoi prendere?";
                    }
                    if (currentChapter == 12) {
                        response = answerChapter("prendi");
                    } else {
                        response = takeItemFromRoom(target);
                    }
                    advanceTurn = true;
                    break;

                case LASCIA:
                    response = dropItem(target);
                    advanceTurn = true;
                    break;

                case GUARDA:
                    response = lookAt(target);
                    advanceTurn = true;
                    break;

                case MANGIA:
                    response = eatOrDrink(target);
                    advanceTurn = true;
                    break;

                case ATTIVA:
                    response = activateItem(target);
                    advanceTurn = true;
                    break;

                case USA:
                    if (target.isEmpty()) {
                        return "❓ Cosa vuoi usare?";
                    }
                    response = player.useItem(target);
                    advanceTurn = true;
                    break;

                case INVENTARIO:
                    return player.getInventoryString();

                case STATO:
                    return player.getStatus();

                case AIUTO:
                    return getHelpText();

                case MAPPA:
                    return "##MAPPA##";

                case SALVA:
                    return saveGame(target.isEmpty() ? "salvataggio1" : target);

                case CARICA_PARTITA:
                    if (target.isEmpty()) {
                        return listSaves();
                    }
                    return loadGame(target);

                case SCONOSCIUTO:
                default:
                    // Prova come risposta diretta al capitolo
                    response = answerChapter(cmd);
                    advanceTurn = true;
                    break;
            }

            return finalizeTurn(response, advanceTurn);
        }

        return "Comando non riconosciuto. Scrivi 'aiuto' per i comandi.";
    }
    
    private String processChoice(String choice) {
        if (!choice.matches("[ABC]")) {
            return "❌ Scegli A, B o C!";
        }
        
        if (currentChapter < storyChapters.size()) {
            Level chapter = storyChapters.get(currentChapter);
            if (chapter.hasChoices()) {
                return answerChapter(choice);
            }
        }
        
        return "❌ Non ci sono scelte in questo momento.";
    }
    
    /** Forza l'avvio del primo capitolo. Usato dalla GUI dopo l'intro. */
    public String forceStartFirstChapter() {
        currentChapterStarted = false;
        currentChapterCompleted = false;
        return startNextChapter();
    }
    
    private String startNextChapter() {
        if (currentChapterStarted && !currentChapterCompleted && currentChapter < storyChapters.size()) {
            Level chapter = storyChapters.get(currentChapter);
            String msg = "📖 CAP. " + (currentChapter + 1) + "/" + storyChapters.size() +
                         ": " + chapter.getTitle() + "\n\n" +
                         chapter.getPrompt() + "\n\n";
            if (chapter.hasChoices()) {
                Map<String, String> choices = chapter.getChoices();
                msg += "🔘 SCELTE: ";
                if (choices.containsKey("A")) msg += "A=" + choices.get("A") + "  ";
                if (choices.containsKey("B")) msg += "B=" + choices.get("B") + "  ";
                if (choices.containsKey("C")) msg += "C=" + choices.get("C");
                msg += "\n\n💡 Premi A, B o C";
            } else {
                msg += "💡 Scrivi la risposta";
            }
            return msg;
        }
        
        if (currentChapter >= storyChapters.size()) {
            gameWon = true;
            gameRunning = false;
            return "🎉 HAI COMPLETATO LOST THESIS! 🎉\n\n" +
                   "Sei fuggito dall'isola!\n" +
                   "La TESI ti ha salvato!\n" +
                   "Ora puoi laurearti! 🎓\n\n" +
                   "CONGRATULAZIONI!";
        }
        
        Level chapter = storyChapters.get(currentChapter);
        currentChapterCompleted = false;
        currentChapterStarted = true;
        
        updateRoomByChapter(currentChapter);
        
        String msg = "📖 CAP. " + (currentChapter + 1) + "/" + storyChapters.size() + 
                     ": " + chapter.getTitle() + "\n\n" +
                     chapter.getPrompt() + "\n\n";
        
        if (chapter.hasChoices()) {
            Map<String, String> choices = chapter.getChoices();
            msg += "🔘 SCELTE: ";
            if (choices.containsKey("A")) msg += "A=" + choices.get("A") + "  ";
            if (choices.containsKey("B")) msg += "B=" + choices.get("B") + "  ";
            if (choices.containsKey("C")) msg += "C=" + choices.get("C");
            msg += "\n\n💡 Premi A, B o C";
        } else {
            msg += "💡 Scrivi la risposta";
        }
        
        addLog(msg);
        return msg;
    }
    
    private String answerChapter(String answer) {
        if (currentChapter >= storyChapters.size()) {
            return "Hai già completato il gioco!";
        }
        
        Level chapter = storyChapters.get(currentChapter);
        boolean correct = chapter.checkAnswer(answer);
        
        if (correct) {
            String success = "✅ CORRETTO!\n\n";

            // Se il capitolo ha un mini gioco, avvialo PRIMA di avanzare
            if (chapter.hasMiniGame()) {
                // Non avanzare il capitolo, il mini gioco lo fara' al termine
                currentChapterCompleted = false;
                String miniGameResult = startMiniGame(chapter.getMiniGameKey());
                return success + miniGameResult;
            }

            currentChapter++;
            currentChapterCompleted = true;
            currentChapterStarted = false;

            // Ricompense narrative dei capitoli chiave
            if (currentChapter == 7 && !player.hasItem("Dinamite")) {
                Item dinamite = new Item("Dinamite",
                    "🧨 Candelotti trovati nella Roccia Nera. Vecchi, ma ancora pericolosi.",
                    true, Item.ItemType.STRUMENTO, 0, 1);
                player.addItem(dinamite);
                blackRockExplored = true;
                success += "🧨 Hai trovato la dinamite nella cassa della Roccia Nera!\n\n";
            }

            // Aggiungi la TESI all'inventario nel capitolo giusto
            if (currentChapter == 15) {
                Item tesi = new Item("TESI",
                    "📜 La TESI perduta! Contiene le coordinate per fuggire dall'isola!",
                    true, Item.ItemType.TESI, 0, -1);
                player.addItem(tesi);
                success += "📜 Hai ottenuto la TESI!\n\n";
            }

            if (currentChapter >= storyChapters.size()) {
                gameWon = true;
                gameRunning = false;
                success += getEpicEnding();
            } else {
                success += "Premi AVANTI per continuare...";
            }

            return success;
        } else {
            String msg = "Risposta sbagliata. Suggerimento: " + chapter.getHint() + "\n\n";
            msg += chapter.getPrompt() + "\n\n";
            if (chapter.hasChoices()) {
                Map<String, String> choices = chapter.getChoices();
                msg += "Scelte: ";
                if (choices.containsKey("A")) msg += "A=" + choices.get("A") + "  ";
                if (choices.containsKey("B")) msg += "B=" + choices.get("B") + "  ";
                if (choices.containsKey("C")) msg += "C=" + choices.get("C");
                msg += "\n\nPremi A, B o C";
            } else {
                msg += "Scrivi la risposta";
            }
            return msg;
        }
    }
    
    private String takeItemFromRoom(String itemName) {
        Room room = player.getCurrentRoom();
        if (room == null) return "Errore!";
        
        Item item = room.removeItem(itemName);
        if (item == null) {
            return "❌ Non vedo '" + itemName + "' qui.";
        }
        
        if (!item.isTakeable()) {
            room.addItem(item);
            return "❌ Non puoi prendere " + item.getName() + ".";
        }
        
        if (player.addItem(item)) {
            return "✅ Hai preso: " + item.getName();
        } else {
            room.addItem(item);
            return "❌ Inventario pieno!";
        }
    }
    
    private void updateRoomByChapter(int chapter) {
        String roomKey;
        switch (chapter) {
            case 0: case 1: roomKey = "spiaggia"; break;
            case 2: roomKey = "giungla"; break;
            case 3: case 4: roomKey = "botola"; break;
            case 5: case 6: roomKey = "villaggio"; break;
            case 7: roomKey = "tempio"; break;
            case 8: roomKey = "roccianera"; break;
            case 9: case 10: case 11: roomKey = "faro"; break;
            case 12: roomKey = "botola"; break;
            case 13: case 14: case 15: case 16: roomKey = "pista"; break;
            default: roomKey = "spiaggia";
        }
        
        if (allRooms.containsKey(roomKey)) {
            player.setCurrentRoom(allRooms.get(roomKey));
        }
    }
    
    // ═══════════════════════════════════════════════════════════════
    // SISTEMA MINI GIOCHI
    // ═══════════════════════════════════════════════════════════════

    private String processMiniGameInput(String input) {
        if (activeMiniGame == null) return "";

        String result;
        String upper = input.toUpperCase().trim();

        // Bottoni A/B/C vanno come button input
        if (upper.equals("A") || upper.equals("B") || upper.equals("C")) {
            result = activeMiniGame.handleButtonInput(upper);
        } else if (upper.equals("AVANTI") || upper.equals("CONTINUA")) {
            // Se il mini gioco è finito, avanti chiude il mini gioco
            MiniGameState mgState = activeMiniGame.getState();
            if (mgState == MiniGameState.WON || mgState == MiniGameState.LOST) {
                return endMiniGame();
            }
            result = activeMiniGame.getCurrentDisplay();
        } else if (upper.equals("SKIP") || upper.equals("SALTA")) {
            // Skip con penalità
            activeMiniGame = null;
            miniGameIntroShown = false;
            player.removeHealth(10);
            return "Hai saltato il mini gioco.\n" +
                   "Penalita': -10 salute.\n\n" +
                   "Premi AVANTI per continuare la storia...";
        } else {
            // Testo libero
            result = activeMiniGame.handleTextInput(input);
        }

        // Controlla se il mini gioco è terminato
        MiniGameState mgState = activeMiniGame.getState();
        if (mgState == MiniGameState.WON || mgState == MiniGameState.LOST) {
            result += "\n\nPremi AVANTI per continuare...";
        }

        return result;
    }

    /** Avvia il mini gioco identificato dalla chiave (es. "smoke_chase"). */
    public String startMiniGame(String miniGameKey) {
        MiniGame game = miniGames.get(miniGameKey);
        if (game == null) return "Mini gioco non trovato: " + miniGameKey;

        game.reset();
        activeMiniGame = game;
        miniGameIntroShown = true;

        return getMiniGameIntroText(miniGameKey) + "\n\n" +
               game.getInstructions() + "\n\n" +
               game.getCurrentDisplay();
    }

    private String endMiniGame() {
        if (activeMiniGame == null) return "";

        MiniGameState mgState = activeMiniGame.getState();
        String miniGameKey = null;
        for (Map.Entry<String, MiniGame> entry : miniGames.entrySet()) {
            if (entry.getValue() == activeMiniGame) {
                miniGameKey = entry.getKey();
                break;
            }
        }

        String result;
        if (mgState == MiniGameState.WON) {
            result = getMiniGameVictoryText(miniGameKey);
            // Completa il capitolo corrente
            currentChapter++;
            currentChapterCompleted = true;
            currentChapterStarted = false;
        } else {
            result = getMiniGameDefeatText(miniGameKey);
            // Offri retry o skip
            result += "\n\nIl gioco continua comunque.\n";
            currentChapter++;
            currentChapterCompleted = true;
            currentChapterStarted = false;
        }

        activeMiniGame = null;
        miniGameIntroShown = false;

        if (currentChapter >= storyChapters.size()) {
            gameWon = true;
            gameRunning = false;
            result += getEpicEnding();
        } else {
            result += "\nPremi AVANTI per continuare la storia...";
        }

        return result;
    }

    private String getMiniGameIntroText(String key) {
        switch (key) {
            case "smoke_chase":
                return "========================================\n" +
                       "  MINI GIOCO: FUGA DAL MOSTRO!\n" +
                       "========================================\n\n" +
                       "Il Mostro di Fumo ti ha trovato!\n" +
                       "TICK... TICK... TICK...\n" +
                       "Devi fuggire ADESSO!";
            case "jungle_tracking":
                return "========================================\n" +
                       "  MINI GIOCO: CACCIA NELLA GIUNGLA!\n" +
                       "========================================\n\n" +
                       "Locke ha visto le tracce di un cinghiale.\n" +
                       "Segui le tracce per catturare la preda!";
            case "dynamite_defusal":
                return "========================================\n" +
                       "  MINI GIOCO: DISINNESCO DINAMITE!\n" +
                       "========================================\n\n" +
                       "La dinamite della Roccia Nera e' instabile!\n" +
                       "Devi disinnescarne una per usarla in sicurezza.";
            case "frequency_tuning":
                return "========================================\n" +
                       "  MINI GIOCO: SINTONIZZA LA RADIO!\n" +
                       "========================================\n\n" +
                       "Nella stazione Il Cigno c'e' una radio.\n" +
                       "Trova la frequenza giusta per ricevere il messaggio!";
            case "morse_code":
                return "========================================\n" +
                       "  MINI GIOCO: CODICE MORSE!\n" +
                       "========================================\n\n" +
                       "La radio trasmette un messaggio in codice Morse!\n" +
                       "Decodificalo per scoprire le coordinate segrete!";
            default:
                return "MINI GIOCO!";
        }
    }

    private String getMiniGameVictoryText(String key) {
        switch (key) {
            case "smoke_chase":
                return "Sei riuscito a sfuggire al Mostro di Fumo!\n" +
                       "Il tuo cuore batte all'impazzata, ma sei salvo.\n" +
                       "Ora sai che il Mostro non e' invincibile.";
            case "jungle_tracking":
                return "Il cinghiale e' stato catturato!\n" +
                       "Stasera il campo avra' carne fresca.\n" +
                       "Locke e' impressionato dalle tue abilita'.";
            case "dynamite_defusal":
                return "La dinamite e' stata disinnescata!\n" +
                       "Ora puoi trasportarla in sicurezza.\n" +
                       "Con questa potrai aprire la botola!";
            case "frequency_tuning":
                return "La radio e' sintonizzata!\n" +
                       "Il messaggio rivela informazioni preziose\n" +
                       "sulla stazione e sui numeri DHARMA.";
            case "morse_code":
                return "Il messaggio Morse e' stato decodificato!\n" +
                       "COORDINATE PER PISTA - ora sai dove\n" +
                       "si trova la pista di atterraggio nascosta!";
            default:
                return "Mini gioco completato!";
        }
    }

    private String getMiniGameDefeatText(String key) {
        switch (key) {
            case "smoke_chase":
                return "Il Mostro di Fumo ti ha raggiunto...\n" +
                       "Ma per qualche ragione ti ha lasciato andare.\n" +
                       "Sei ferito, ma vivo. (-10 salute)";
            case "jungle_tracking":
                return "La preda e' scappata nella giungla.\n" +
                       "Oggi niente carne fresca al campo.\n" +
                       "Dovrete accontentarvi di frutta.";
            case "dynamite_defusal":
                return "La dinamite non e' stata disinnescata.\n" +
                       "Dovrai trasportarla con molta cautela.\n" +
                       "Ogni movimento potrebbe essere l'ultimo...";
            case "frequency_tuning":
                return "La radio si e' surriscaldata e si e' spenta.\n" +
                       "Non sei riuscito a sintonizzarla.\n" +
                       "Ma forse troverai le informazioni altrove.";
            case "morse_code":
                return "Non sei riuscito a decodificare il messaggio.\n" +
                       "Pero' hai capito qualcosa sulle coordinate.\n" +
                       "Forse la mappa DHARMA aiutera'.";
            default:
                return "Mini gioco fallito.";
        }
    }

    public MiniGame getActiveMiniGame() {
        return activeMiniGame;
    }

    public boolean hasMiniGameActive() {
        return activeMiniGame != null;
    }

    private String getHelpText() {
        return "═══════════════════════════════════════\n" +
               "  ✈️ LOST THESIS - COMANDI ✈️\n" +
               "═══════════════════════════════════════\n" +
               "🔘 A, B, C      - Scegli un'opzione\n" +
               "➡️ avanti        - Continua la storia\n" +
               "📦 prendi [obj]  - Raccogli oggetto (p)\n" +
               "📤 lascia [obj]  - Lascia oggetto (l)\n" +
               "👁️ guarda [obj]  - Osserva oggetto (g/x)\n" +
               "🔧 usa [obj]     - Usa un oggetto (u)\n" +
               "🍎 mangia [obj]  - Mangia/bevi\n" +
               "💣 attiva [obj]  - Attiva oggetto\n" +
               "🎒 inventario    - Vedi oggetti (i)\n" +
               "❤️ stato         - Vedi salute (st/hp)\n" +
               "💾 salva [nome]  - Salva partita\n" +
               "📂 carica/load [nome] - Carica partita\n" +
               "🗺️ mappa         - Mappa dell'isola (m)\n" +
               "❓ aiuto         - Questo messaggio (h)\n" +
               "═══════════════════════════════════════\n" +
               "💡 Scrivi 'alias' per tutti i sinonimi\n" +
               "═══════════════════════════════════════";
    }
    
    // ═══════════════════════════════════════════════════════════════
    // NUOVI METODI ISPIRATI ALLA GUIDA COLOMBINI
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Lascia un oggetto nella stanza corrente
     */
    private String dropItem(String itemName) {
        if (itemName.isEmpty()) {
            return "❌ Cosa vuoi lasciare?";
        }
        Item item = player.removeItem(itemName);
        if (item == null) {
            return "❌ Non hai '" + itemName + "' nell'inventario.";
        }
        player.getCurrentRoom().addItem(item);
        return "✅ Hai lasciato: " + item.getName();
    }
    
    /**
     * Guarda/esamina un oggetto - risposte dettagliate!
     */
    private String lookAt(String target) {
        if (target.isEmpty()) {
            // Guarda la stanza
            return player.getCurrentRoom().getFullDescription();
        }
        
        // Cerca nell'inventario
        Item item = player.getItem(target);
        if (item == null) {
            // Cerca nella stanza
            item = player.getCurrentRoom().getItem(target);
        }
        
        if (item != null) {
            return getDetailedDescription(item);
        }
        
        // Risposte speciali per elementi dell'ambiente
        return lookAtEnvironment(target);
    }
    
    /**
     * Descrizioni dettagliate oggetti (come consigliato dalla guida)
     */
    private String getDetailedDescription(Item item) {
        String name = item.getName().toLowerCase();
        
        if (name.contains("dinamite")) {
            return "🧨 DINAMITE INSTABILE\n" +
                   "Vecchi candelotti dalla Roccia Nera.\n" +
                   "ATTENZIONE: Potrebbero esplodere!\n" +
                   "💡 Usa 'attiva dinamite' per innescarla.";
        }
        if (name.contains("bussola")) {
            return "🧭 UNA VECCHIA BUSSOLA\n" +
                   "L'ago punta sempre a Nord... o forse no?\n" +
                   "Sull'isola, le bussole impazziscono.\n" +
                   "C'è un'incisione: '4 8 15 16 23 42'";
        }
        if (name.contains("mappa") && name.contains("dharma")) {
            return "🗺️ MAPPA DHARMA INITIATIVE\n" +
                   "Mostra le stazioni segrete dell'isola:\n" +
                   "• IL CIGNO (The Swan) - Pulsante\n" +
                   "• LA PERLA (The Pearl) - Osservazione\n" +
                   "• LA FIAMMA (The Flame) - Comunicazioni\n" +
                   "• L'IDRA (Hydra) - Esperimenti";
        }
        if (name.contains("chiave")) {
            return "🔑 CHIAVE DI SICUREZZA\n" +
                   "Una chiave metallica con il logo DHARMA.\n" +
                   "Potrebbe aprire qualcosa di importante...";
        }
        if (name.contains("cibo") && name.contains("dharma")) {
            return "🥫 SCATOLETTE DHARMA\n" +
                   "Cibo in scatola degli anni '70.\n" +
                   "Etichetta: 'DHARMA Initiative - Ranch Composite'\n" +
                   "Scadenza: 1977 (gulp!)\n" +
                   "💡 Usa 'mangia cibo' per recuperare salute.";
        }
        if (name.contains("diario")) {
            return "📖 DIARIO DEL CAPITANO\n" +
                   "Dalla nave Roccia Nera, anno 1867.\n" +
                   "'...un'onda gigantesca ci ha portato\n" +
                   "nell'entroterra dell'isola. Questo luogo\n" +
                   "è maledetto. Ho visto il fumo nero...'";
        }
        if (name.contains("tesi")) {
            return "📜 LA TESI PERDUTA\n" +
                   "Il documento più importante dell'isola!\n" +
                   "Contiene:\n" +
                   "• Coordinate della pista nascosta\n" +
                   "• Istruzioni per l'aereo\n" +
                   "• Il codice: 108 / 2 = 54\n" +
                   "🎓 Con questa puoi FUGGIRE e LAUREARTI!";
        }
        if (name.contains("kit") || name.contains("medico")) {
            return "🏥 KIT DI PRONTO SOCCORSO\n" +
                   "Recuperato dai rottami dell'Oceanic 815.\n" +
                   "Contiene bende, disinfettante e antidolorifici.\n" +
                   "💡 Usa 'usa kit' per curarti.";
        }
        if (name.contains("acqua")) {
            return "💧 BOTTIGLIA D'ACQUA\n" +
                   "Acqua potabile dai rottami dell'aereo.\n" +
                   "Essenziale per sopravvivere sull'isola.\n" +
                   "💡 Usa 'bevi acqua' per idratarti.";
        }
        
        // Descrizione generica
        return "👁️ " + item.getName().toUpperCase() + "\n" + item.getDescription();
    }
    
    /**
     * Guarda elementi dell'ambiente (non oggetti)
     */
    private String lookAtEnvironment(String target) {
        target = target.toLowerCase();
        
        // Risposte atmosferiche per l'ambiente
        if (target.contains("cielo") || target.contains("sky")) {
            return "☁️ Il cielo è stranamente luminoso.\n" +
                   "A volte sembra che l'isola sia... fuori dal tempo.";
        }
        if (target.contains("mare") || target.contains("oceano")) {
            return "🌊 L'oceano si estende all'infinito.\n" +
                   "Nessuna nave all'orizzonte. Nessun aereo.\n" +
                   "Sei davvero solo qui.";
        }
        if (target.contains("giungla") || target.contains("alberi")) {
            return "🌴 La giungla è fitta e ostile.\n" +
                   "Senti strani rumori... ticchettii meccanici.\n" +
                   "Qualcosa di GROSSO si muove là dentro.";
        }
        if (target.contains("mostro") || target.contains("fumo")) {
            return "🌫️ Non vedi nulla... ma lo SENTI.\n" +
                   "TICK... TICK... TICK...\n" +
                   "Il Mostro di Fumo è sempre in agguato.";
        }
        if (target.contains("numeri") || target.contains("4 8 15")) {
            return "🔢 I NUMERI MALEDETTI\n" +
                   "4 - 8 - 15 - 16 - 23 - 42\n" +
                   "Somma: 108\n" +
                   "Sono ovunque sull'isola...";
        }
        if (target.contains("jacob")) {
            return "👤 Jacob è il protettore dell'isola.\n" +
                   "Vive al Faro e osserva i candidati.\n" +
                   "'L'isola ti ha scelto.'";
        }
        if (target.contains("altri") || target.contains("others")) {
            return "👥 Gli Altri vivono sull'isola da anni.\n" +
                   "Guidati da Ben Linus.\n" +
                   "Non fidarti di loro.";
        }
        
        // Risposte ironiche per comandi strani (come suggerito dalla guida!)
        if (target.contains("me") || target.contains("stesso")) {
            return "🪞 Ti guardi: sei un sopravvissuto.\n" +
                   "Sporco, stanco, ma ancora vivo.\n" +
                   "Ce la farai!";
        }
        
        return "❓ Non noti nulla di particolare riguardo a '" + target + "'.";
    }
    
    /**
     * Mangia o bevi qualcosa
     */
    private String eatOrDrink(String target) {
        if (target.isEmpty()) {
            return getIronicResponse("mangia");
        }
        Item item = player.getItem(target);
        if (item == null) {
            return "❌ Non hai '" + target + "' nell'inventario.";
        }
        if (item.getType() != Item.ItemType.CIBO && item.getType() != Item.ItemType.MEDICINA) {
            return getIronicResponse("mangia " + target);
        }
        return player.useItem(target);
    }
    
    /**
     * Attiva un oggetto (es. dinamite)
     */
    private String activateItem(String target) {
        if (target.isEmpty()) {
            return "❓ Cosa vuoi attivare?";
        }
        
        if (target.toLowerCase().contains("dinamite")) {
            if (!player.hasItem("dinamite")) {
                return "❌ Non hai dinamite!";
            }
            if (dynamiteActive) {
                return "⚠️ La dinamite è già innescata!\n" +
                       "TICK... TICK... TICK...\n" +
                       "Lasciala da qualche parte, VELOCE!";
            }
            dynamiteActive = true;
            dynamiteTimer = 5; // 5 turni prima dell'esplosione
            return "🧨💥 HAI INNESCATO LA DINAMITE!\n" +
                   "TICK... TICK... TICK...\n" +
                   "Hai 5 turni per metterti al sicuro!\n" +
                   "💡 Lasciala con 'lascia dinamite' e SCAPPA!";
        }
        
        return "❓ Non puoi attivare '" + target + "'.";
    }
    
    /**
     * Risposte ironiche per comandi impossibili (come suggerisce la guida!)
     */
    private String getIronicResponse(String command) {
        command = command.toLowerCase();
        
        if (command.contains("mangia") && command.contains("roccia")) {
            return "🪨 Hmm, no. Non sei COSÌ affamato... ancora.";
        }
        if (command.contains("mangia") && command.contains("sabbia")) {
            return "🏖️ La sabbia non è nel menu oggi.";
        }
        if (command.contains("mangia") && command.contains("dinamite")) {
            return "🧨 Pessima idea. PESSIMA.";
        }
        if (command.contains("mangia")) {
            return "🤔 Non puoi mangiare quello.\n" +
                   "Prova con il cibo DHARMA!";
        }
        if (command.contains("vola") || command.contains("fly")) {
            return "🦅 Sei un sopravvissuto, non un uccello.\n" +
                   "Ma c'è un aereo sulla pista nascosta...";
        }
        if (command.contains("nuota") && command.contains("via")) {
            return "🌊 L'oceano è infinito.\n" +
                   "Moriresti prima di vedere terra.";
        }
        if (command.contains("uccidi") && command.contains("mostro")) {
            return "🌫️ Non puoi uccidere il Mostro di Fumo.\n" +
                   "Puoi solo SCAPPARE.";
        }
        if (command.contains("parla") && command.contains("albero")) {
            return "🌴 L'albero non risponde.\n" +
                   "(Forse la sanità mentale sta calando...)";
        }
        
        return "❓ Non capisco cosa vuoi fare.";
    }
    
    /**
     * Processa i timer ad ogni turno (come nella guida Colombini)
     */
    private String processTimers() {
        StringBuilder events = new StringBuilder();

        // Timer dinamite
        if (dynamiteTimer > 0) {
            dynamiteTimer--;
            if (dynamiteTimer == 0 && dynamiteActive) {
                appendEvent(events, explodeDynamite());
            }
        }

        // Timer mostro di fumo (casuale)
        if (smokeMonsterTimer > 0) {
            smokeMonsterTimer--;
            if (smokeMonsterTimer == 0) {
                smokeMonsterNearby = true;
                appendEvent(events,
                    "🌫️ Il Mostro di Fumo e' vicino.\n" +
                    "TICK... TICK... TICK...");
            }
        }

        // Timer degli Altri
        if (othersTimer > 0) {
            othersTimer--;
            if (othersTimer == 0) {
                appendEvent(events,
                    "👥 Senti voci nella giungla.\n" +
                    "Gli Altri ti stanno cercando.");
            }
        }

        return events.toString();
    }
    
    /**
     * Esplosione dinamite
     */
    private String explodeDynamite() {
        // Trova dove è la dinamite
        Item dinamite = player.getItem("dinamite");
        if (dinamite != null) {
            // Se ce l'hai in mano... BOOM!
            player.removeHealth(100);
            String message = "💥💥💥 BOOM! 💥💥💥\n" +
                             "La dinamite e' esplosa TRA LE TUE MANI!\n" +
                             "Non avresti dovuto tenerla...\n\n" +
                             "☠️ SEI MORTO ☠️";
            addLog(message);
            dynamiteActive = false;
            gameRunning = false;
            return message;
        } else {
            // Esplode nella stanza dove l'hai lasciata
            String message = "💥 BOOM! 💥\n" +
                             "Senti un'esplosione in lontananza.\n" +
                             "Qualcosa e' stato distrutto...";
            addLog(message);
            dynamiteActive = false;
            return message;
        }
    }
    
    private void addLog(String message) {
        gameLog.add(message);
    }
    
    /**
     * FINALE EPICO - Come suggerisce la guida Colombini:
     * "Dopo che uno ha speso sangue, sudore e lacrime per risolvere 
     * l'avventura, ha diritto ad aspettarsi qualcosa di più gratificante"
     */
    private String getEpicEnding() {
        StringBuilder ending = new StringBuilder();
        
        ending.append("\n");
        ending.append("═══════════════════════════════════════════════════════\n");
        ending.append("     ✈️🌅 L I B E R T À 🌅✈️\n");
        ending.append("═══════════════════════════════════════════════════════\n\n");
        
        ending.append("L'aereo decolla, lasciandosi alle spalle l'isola.\n\n");
        
        ending.append("Sotto di te, la giungla diventa sempre più piccola.\n");
        ending.append("Il Mostro di Fumo ruggisce impotente.\n");
        ending.append("Il Tempio, la Stazione Il Cigno, la Roccia Nera...\n");
        ending.append("tutto scompare all'orizzonte.\n\n");
        
        ending.append("🌊 L'oceano infinito si stende davanti a te.\n");
        ending.append("Finalmente LIBERO.\n\n");
        
        ending.append("═══════════════════════════════════════════════════════\n\n");
        
        ending.append("📜 Stringi la TESI tra le mani.\n");
        ending.append("Quella tesi che ti ha salvato la vita.\n");
        ending.append("Quella tesi che ti ha mostrato la via.\n\n");
        
        ending.append("🎓 E ora... puoi finalmente LAUREARTI!\n\n");
        
        ending.append("═══════════════════════════════════════════════════════\n");
        ending.append("          🏆 HAI COMPLETATO LOST THESIS! 🏆\n");
        ending.append("═══════════════════════════════════════════════════════\n\n");
        
        // Statistiche finali
        ending.append("📊 LE TUE STATISTICHE:\n");
        ending.append("   ⏱️ Giorni sull'isola: ").append(player.getDaysOnIsland()).append("\n");
        ending.append("   ❤️ Salute finale: ").append(player.getHealth()).append("/100\n");
        ending.append("   🧠 Sanità mentale: ").append(player.getSanity()).append("/100\n");
        ending.append("   🎒 Oggetti raccolti: ").append(player.getInventory().size()).append("\n\n");
        
        ending.append("═══════════════════════════════════════════════════════\n");
        ending.append("   \"L'isola non ha finito con te, ").append(player.getName()).append(".\"\n");
        ending.append("                           - Jacob\n");
        ending.append("═══════════════════════════════════════════════════════\n\n");
        
        ending.append("              🎮 GRAZIE PER AVER GIOCATO! 🎮\n\n");
        
        ending.append("        Creato con ❤️ seguendo la Guida Colombini\n");
        ending.append("        'Avventure - Guida pratica alla creazione\n");
        ending.append("         di giochi di avventura' (Jackson, 1985)\n");
        
        return ending.toString();
    }
    
    public String getLastLog() {
        return gameLog.isEmpty() ? "" : gameLog.get(gameLog.size() - 1);
    }
    
    public String getCurrentRoomKey() {
        if (player != null && player.getCurrentRoom() != null) {
            return player.getCurrentRoom().getKey();
        }
        return "spiaggia";
    }
    
    /**
     * Restituisce la chiave dell'immagine da mostrare per il capitolo corrente.
     * Usato dalla GUI per caricare l'immagine corretta.
     */
    public String getCurrentChapterImageKey() {
        if (currentChapter >= storyChapters.size()) {
            return "cap18_freedom";
        }
        Level chapter = storyChapters.get(currentChapter);
        return chapter.getKey();
    }
    
    /**
     * Restituisce il numero del capitolo corrente (1-based per display)
     */
    public int getCurrentChapterNumber() {
        if (storyChapters.isEmpty()) {
            return 0;
        }
        return Math.min(currentChapter + 1, storyChapters.size());
    }

    public String getCurrentChapterTitle() {
        if (gameWon) {
            return "Completato";
        }
        if (storyChapters.isEmpty()) {
            return "N/D";
        }
        int safeIndex = Math.max(0, Math.min(currentChapter, storyChapters.size() - 1));
        return storyChapters.get(safeIndex).getTitle();
    }
    
    /**
     * Restituisce il totale dei capitoli
     */
    public int getTotalChapters() {
        return storyChapters.size();
    }
    
    public Player getPlayer() { return player; }
    public boolean isNarrativeMode() { return narrativeMode; }
    public boolean isGameWon() { return gameWon; }
    public boolean isGameRunning() { return gameRunning; }
    public AudioManager getAudioManager() { return audioManager; }

    // ═══════════════════════════════════════════════════════════════
    // COMANDI SALVA / CARICA
    // ═══════════════════════════════════════════════════════════════

    private String saveGame(String slotName) {
        boolean ok = GameSave.save(this, slotName);
        if (ok) {
            return "💾 Partita salvata nello slot '" + slotName + "'!\n" +
                   "Usa 'carica " + slotName + "' per ricaricarla.";
        }
        return "❌ Errore durante il salvataggio!";
    }

    private String loadGame(String slotName) {
        GameState state = GameSave.load(slotName);
        if (state == null) {
            return "❌ Nessun salvataggio trovato con nome '" + slotName + "'.\n" +
                   "Usa 'carica' per vedere i salvataggi disponibili.";
        }
        loadGameState(state);
        return "✅ Partita caricata dallo slot '" + slotName + "'!\n" +
               "👤 " + player.getName() + " | Cap. " + getCurrentChapterNumber() +
               "/" + getTotalChapters() + " | ❤️ " + player.getHealth() +
               " | 🧠 " + player.getSanity() + "\n\n" +
               "Premi AVANTI per continuare...";
    }

    private String listSaves() {
        List<GameSaveInstance> saves = GameSave.listSaves();
        if (saves.isEmpty()) {
            return "📂 Nessun salvataggio trovato.\n" +
                   "Usa 'salva [nome]' per salvare la partita.";
        }
        StringBuilder sb = new StringBuilder("📂 SALVATAGGI DISPONIBILI:\n\n");
        for (GameSaveInstance save : saves) {
            sb.append("  💾 ").append(save.getDisplayText()).append("\n");
        }
        sb.append("\nUsa 'carica [nome]' per caricare un salvataggio.");
        return sb.toString();
    }

    // Getter per il sistema di salvataggio
    public int getCurrentChapter() { return currentChapter; }
    public boolean isCurrentChapterCompleted() { return currentChapterCompleted; }
    public boolean isCurrentChapterStarted() { return currentChapterStarted; }
    public boolean isHatchOpened() { return hatchOpened; }
    public boolean isBlackRockExplored() { return blackRockExplored; }
    public boolean isJacobMet() { return jacobMet; }
    public boolean isTempleBathed() { return templeBathed; }
    public boolean isDynamiteActive() { return dynamiteActive; }
    public int getDynamiteTimer() { return dynamiteTimer; }
    public int getSmokeMonsterTimer() { return smokeMonsterTimer; }
    public int getOthersTimer() { return othersTimer; }
    public Map<String, Room> getAllRooms() { return allRooms; }

    /**
     * Ripristina lo stato del gioco da un GameState caricato
     */
    public void loadGameState(GameState state) {
        // Ricrea il mondo e i capitoli
        player = new Player(state.getPlayerName());
        createWorld();
        createStoryChapters();

        // Ripristina stato giocatore (via reflection-free: setter diretti)
        // Health e sanity vanno impostati partendo da 100 e sottraendo/aggiungendo
        int healthDiff = state.getHealth() - player.getHealth();
        if (healthDiff > 0) player.addHealth(healthDiff);
        else if (healthDiff < 0) player.removeHealth(-healthDiff);

        int sanityDiff = state.getSanity() - player.getSanity();
        if (sanityDiff > 0) player.addSanity(sanityDiff);
        else if (sanityDiff < 0) player.removeSanity(-sanityDiff);

        // Giorni
        while (player.getDaysOnIsland() < state.getDaysOnIsland()) {
            // nextDay() toglie anche sanita', quindi usiamo un approccio diretto
            // incrementiamo senza effetti collaterali
            player.nextDay();
        }
        // Ricalcola sanity dopo i giorni
        int finalSanityDiff = state.getSanity() - player.getSanity();
        if (finalSanityDiff > 0) player.addSanity(finalSanityDiff);
        else if (finalSanityDiff < 0) player.removeSanity(-finalSanityDiff);

        // Stanza corrente
        Room room = allRooms.get(state.getCurrentRoomKey());
        if (room != null) {
            player.setCurrentRoom(room);
        }

        // Inventario
        player.getInventory().clear();
        if (state.getInventory() != null) {
            for (ItemData itemData : state.getInventory()) {
                player.addItem(itemData.toItem());
            }
        }

        // Oggetti nelle stanze: prima svuota, poi ripristina
        for (Room r : allRooms.values()) {
            r.getItems().clear();
        }
        if (state.getRoomItems() != null) {
            for (Map.Entry<String, List<ItemData>> entry : state.getRoomItems().entrySet()) {
                Room r = allRooms.get(entry.getKey());
                if (r != null) {
                    for (ItemData itemData : entry.getValue()) {
                        r.addItem(itemData.toItem());
                    }
                }
            }
        }

        // Stato narrativa
        this.currentChapter = state.getCurrentChapter();
        this.currentChapterCompleted = state.isCurrentChapterCompleted();
        this.currentChapterStarted = state.isCurrentChapterStarted();
        this.gameRunning = state.isGameRunning();
        this.gameWon = state.isGameWon();
        this.narrativeMode = true;

        // Flag eventi
        this.hatchOpened = state.isHatchOpened();
        this.blackRockExplored = state.isBlackRockExplored();
        this.jacobMet = state.isJacobMet();
        this.templeBathed = state.isTempleBathed();
        this.dynamiteActive = state.isDynamiteActive();
        this.dynamiteTimer = state.getDynamiteTimer();
        this.smokeMonsterTimer = state.getSmokeMonsterTimer();
        this.othersTimer = state.getOthersTimer();

        // Mini gioco resettato
        this.activeMiniGame = null;
        this.miniGameIntroShown = false;
        this.miniGameOutroShown = false;
    }

    private String finalizeTurn(String response, boolean advanceTurn) {
        if (!advanceTurn || !gameRunning) {
            return response;
        }

        String timerEvents = processTimers();
        if (timerEvents.isEmpty()) {
            return response;
        }

        if (response == null || response.isBlank()) {
            return timerEvents;
        }

        return response + "\n\n" + timerEvents;
    }

    private void appendEvent(StringBuilder events, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (!events.isEmpty()) {
            events.append("\n\n");
        }
        events.append(message);
    }
}
