package com.lostthesis.engine;

import com.lostthesis.model.*;
import com.lostthesis.audio.AudioManager;
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
    }
    
    public void initializeGame(String playerName) {
        player = new Player(playerName);
        createWorld();
        createStoryChapters();
        player.setCurrentRoom(startRoom);
        gameRunning = true;
        
        // 🎵 Avvia la sigla di LOST! (suona per 15 secondi, poi fade out)
        audioManager.playBackgroundMusic("lost___opening_titles.wav", false, 15000);
        
        addLog("═══════════════════════════════════════════════════");
        addLog("  ✈️ LOST THESIS - L'ISOLA MISTERIOSA ✈️");
        addLog("═══════════════════════════════════════════════════");
        addLog("");
        addLog("Il volo Oceanic 815 è precipitato su un'isola sconosciuta.");
        addLog("Sei uno dei sopravvissuti, " + playerName + ".");
        addLog("");
        addLog("L'isola nasconde segreti terrificanti...");
        addLog("Ma anche una via di fuga: LA TESI.");
        addLog("");
        addLog("Trova la TESI perduta per scappare con l'aereo!");
        addLog("═══════════════════════════════════════════════════");
    }

    private void createStoryChapters() {
        storyChapters.clear();
        
        // CAPITOLO 1: LO SCHIANTO
        Map<String, String> cap1Choices = new HashMap<>();
        cap1Choices.put("A", "Aiutare i feriti");
        cap1Choices.put("B", "Esplorare i rottami");
        cap1Choices.put("C", "Fuggire nella giungla");
        storyChapters.add(new Level(
            "cap1_crash",
            "Lo Schianto",
            "✈️💥 CRASH! L'aereo si è spezzato in due!\n\n" +
            "Ti svegli sulla spiaggia tra i rottami fumanti.\n" +
            "Urla, fuoco, confusione ovunque.\n" +
            "Un motore sta ancora girando pericolosamente...\n\n" +
            "❓ Cosa fai per prima cosa?",
            cap1Choices,
            "A",
            "Essere un eroe è sempre la scelta giusta!"
        ));
        
        // CAPITOLO 2: I SOPRAVVISSUTI
        Map<String, String> cap2Choices = new HashMap<>();
        cap2Choices.put("A", "48");
        cap2Choices.put("B", "23");
        cap2Choices.put("C", "108");
        storyChapters.add(new Level(
            "cap2_survivors",
            "I Sopravvissuti",
            "🏝️ Sei sulla spiaggia con gli altri sopravvissuti.\n\n" +
            "Jack, il medico, sta organizzando il campo.\n" +
            "Kate raccoglie provviste dai rottami.\n" +
            "Sawyer sta già litigando con qualcuno...\n\n" +
            "Qualcuno chiede: 'Quanti siamo sopravvissuti?'\n\n" +
            "❓ Quanti passeggeri sono sopravvissuti allo schianto? (Suggerimento: è uno dei NUMERI)",
            cap2Choices,
            "A",
            "48 sopravvissuti iniziali!"
        ));
        
        // CAPITOLO 3: IL MOSTRO DI FUMO
        Map<String, String> cap3Choices = new HashMap<>();
        cap3Choices.put("A", "Corri via!");
        cap3Choices.put("B", "Resta immobile");
        cap3Choices.put("C", "Affrontalo");
        storyChapters.add(new Level(
            "cap3_smoke",
            "Il Mostro di Fumo",
            "🌫️ NELLA GIUNGLA...\n\n" +
            "Stai esplorando quando senti un rumore terrificante.\n" +
            "TICK... TICK... TICK... *RUGGITO MECCANICO*\n\n" +
            "Una colonna di FUMO NERO si avvicina!\n" +
            "È il MOSTRO dell'isola!\n\n" +
            "❓ Cosa fai?",
            cap3Choices,
            "B",
            "Il mostro non attacca chi sta fermo..."
        ));
        
        // CAPITOLO 4: LA BOTOLA
        Map<String, String> cap4Choices = new HashMap<>();
        cap4Choices.put("A", "4");
        cap4Choices.put("B", "8");
        cap4Choices.put("C", "15");
        storyChapters.add(new Level(
            "cap4_hatch",
            "La Botola",
            "🚪 Hai trovato una BOTOLA nel terreno!\n\n" +
            "È coperta di terra e foglie.\n" +
            "C'è una scritta: 'QUARANTINE' e un logo: DHARMA.\n\n" +
            "Per aprirla devi inserire un codice...\n\n" +
            "❓ Qual è il primo numero della sequenza DHARMA?",
            cap4Choices,
            "A",
            "4-8-15-16-23-42!"
        ));
        
        // CAPITOLO 5: IL CIGNO
        storyChapters.add(new Level(
            "cap5_swan",
            "La Stazione Il Cigno",
            "🦢 Scendi nella stazione sotterranea.\n\n" +
            "Trovi un uomo, DESMOND, che vive qui da anni.\n" +
            "'Brother! Finalmente qualcuno!'\n" +
            "'Devi premere il pulsante ogni 108 minuti!'\n" +
            "'Altrimenti... il mondo finisce!'\n\n" +
            "Sul computer c'è scritto: 4 8 15 16 23 42\n\n" +
            "❓ Quanto fa la somma dei numeri DHARMA?",
            Arrays.asList("108", "centootto"),
            "4+8+15+16+23+42 = ?"
        ));
        
        // CAPITOLO 6: GLI ALTRI
        Map<String, String> cap6Choices = new HashMap<>();
        cap6Choices.put("A", "Ben Linus");
        cap6Choices.put("B", "John Locke");
        cap6Choices.put("C", "Jacob");
        storyChapters.add(new Level(
            "cap6_others",
            "Gli Altri",
            "👥 Sei stato catturato dagli ALTRI!\n\n" +
            "Ti portano in un villaggio nascosto.\n" +
            "Sembrano vivere sull'isola da molto tempo.\n\n" +
            "Un uomo con occhiali ti interroga:\n" +
            "'Sappiamo tutto di te. Sappiamo perché sei qui.'\n" +
            "'L'isola ti ha scelto.'\n\n" +
            "❓ Chi è il leader degli Altri?",
            cap6Choices,
            "A",
            "L'uomo misterioso con gli occhiali..."
        ));
        
        // CAPITOLO 7: IL TEMPIO
        Map<String, String> cap7Choices = new HashMap<>();
        cap7Choices.put("A", "Bere l'acqua");
        cap7Choices.put("B", "Rifiutare");
        cap7Choices.put("C", "Scappare");
        storyChapters.add(new Level(
            "cap7_temple",
            "Il Tempio",
            "🏛️ Arrivi al TEMPIO nascosto.\n\n" +
            "I guardiani del tempio ti portano davanti a una vasca.\n" +
            "'L'acqua del tempio può guarirti...'\n" +
            "'Ma ha un prezzo.'\n\n" +
            "L'acqua è scura e misteriosa.\n\n" +
            "❓ Cosa fai?",
            cap7Choices,
            "A",
            "L'isola richiede fede..."
        ));
        
        // CAPITOLO 8: I FLASHBACK
        Map<String, String> cap8Choices = new HashMap<>();
        cap8Choices.put("A", "815");
        cap8Choices.put("B", "316");
        cap8Choices.put("C", "777");
        storyChapters.add(new Level(
            "cap8_flashback",
            "I Flashback",
            "💭 FLASHBACK - PRIMA DELLO SCHIANTO\n\n" +
            "Ti ricordi di quando sei salito sull'aereo.\n" +
            "L'aeroporto di Sydney era affollato.\n" +
            "Avevi con te una tesi importante...\n" +
            "Dovevi consegnarla a Los Angeles.\n\n" +
            "❓ Qual era il numero del volo Oceanic?",
            cap8Choices,
            "A",
            "Oceanic Flight..."
        ));
        
        // CAPITOLO 9: LA ROCCIA NERA
        storyChapters.add(new Level(
            "cap9_blackrock",
            "La Roccia Nera",
            "⚓ Trovi una NAVE nel mezzo della giungla!\n\n" +
            "È una vecchia nave schiavista: LA ROCCIA NERA.\n" +
            "Come è arrivata qui, in mezzo all'isola?!\n\n" +
            "All'interno trovi... DINAMITE!\n" +
            "E un vecchio diario con una mappa.\n" +
            "La mappa mostra una 'PISTA DI ATTERRAGGIO SEGRETA'!\n\n" +
            "❓ Quanti anni ha questa nave? (1800s - anno corrente circa 1867)",
            Arrays.asList("1867", "150", "centocinquanta"),
            "Nave del 1800..."
        ));
        
        // CAPITOLO 10: JACOB
        Map<String, String> cap10Choices = new HashMap<>();
        cap10Choices.put("A", "Il Protettore dell'isola");
        cap10Choices.put("B", "Un prigioniero");
        cap10Choices.put("C", "Un'illusione");
        storyChapters.add(new Level(
            "cap10_jacob",
            "Jacob",
            "🕯️ Nella notte vedi una figura luminosa.\n\n" +
            "Un uomo vestito di bianco ti parla:\n" +
            "'Sei qui per un motivo, " + "' + player.getName() + '" + ".'\n" +
            "'L'isola ti ha scelto.'\n" +
            "'Ma devi scegliere tu se restare... o andare.'\n\n" +
            "❓ Chi è Jacob?",
            cap10Choices,
            "A",
            "Il guardiano dell'isola..."
        ));
        
        // CAPITOLO 11: L'UOMO IN NERO
        Map<String, String> cap11Choices = new HashMap<>();
        cap11Choices.put("A", "Rifiutare");
        cap11Choices.put("B", "Accettare");
        cap11Choices.put("C", "Chiedere tempo");
        storyChapters.add(new Level(
            "cap11_mib",
            "L'Uomo in Nero",
            "🖤 L'UOMO IN NERO appare davanti a te.\n\n" +
            "Ha l'aspetto di John Locke, ma... non è lui.\n" +
            "'Vuoi lasciare quest'isola?'\n" +
            "'Posso aiutarti. Devi solo fare una cosa per me.'\n" +
            "'Uccidi Jacob.'\n\n" +
            "❓ Cosa rispondi?",
            cap11Choices,
            "A",
            "Mai fidarsi dell'Uomo in Nero!"
        ));
        
        // CAPITOLO 12: IL FARO
        Map<String, String> cap12Choices = new HashMap<>();
        cap12Choices.put("A", "23");
        cap12Choices.put("B", "42");
        cap12Choices.put("C", "108");
        storyChapters.add(new Level(
            "cap12_lighthouse",
            "Il Faro",
            "🗼 Trovi un antico FARO sull'isola!\n\n" +
            "All'interno c'è uno specchio magico.\n" +
            "Ruotando la ruota, lo specchio mostra... LA TUA CASA!\n\n" +
            "Sulla ruota ci sono nomi con numeri.\n" +
            "Il tuo nome è associato a un numero...\n\n" +
            "❓ Qual è il numero di Jack Shephard?",
            cap12Choices,
            "A",
            "Uno dei numeri DHARMA..."
        ));
        
        // CAPITOLO 13: LA SCOPERTA DELLA TESI
        storyChapters.add(new Level(
            "cap13_thesis",
            "La Scoperta",
            "📜 NEL BUNKER DHARMA...\n\n" +
            "Trovi una stanza segreta dietro una parete!\n" +
            "All'interno c'è un documento polveroso:\n\n" +
            "📖 'TESI: COORDINATE PER LA FUGA'\n" +
            "'Autore: Un sopravvissuto, anno 1977'\n\n" +
            "La tesi contiene le coordinate della pista segreta!\n" +
            "E le istruzioni per far funzionare l'aereo!\n\n" +
            "❓ Hai trovato la TESI! Digita 'prendi' per raccoglierla.",
            Arrays.asList("prendi", "raccogli", "ok", "si"),
            "Raccoglila!"
        ));
        
        // CAPITOLO 14: LA PISTA NASCOSTA
        Map<String, String> cap14Choices = new HashMap<>();
        cap14Choices.put("A", "Nord dell'isola");
        cap14Choices.put("B", "Centro dell'isola");
        cap14Choices.put("C", "Sud dell'isola");
        storyChapters.add(new Level(
            "cap14_runway",
            "La Pista Nascosta",
            "🛬 Segui le coordinate della TESI.\n\n" +
            "Attraversi la giungla per giorni.\n" +
            "Il Mostro di Fumo ti insegue.\n" +
            "Gli Altri cercano di fermarti.\n\n" +
            "Ma finalmente... LA VEDI!\n" +
            "Una pista di atterraggio nascosta tra le palme!\n" +
            "E un piccolo AEREO!\n\n" +
            "❓ Dove si trova la pista secondo la mappa?",
            cap14Choices,
            "A",
            "La bussola punta a nord..."
        ));
        
        // CAPITOLO 15: PREPARAZIONE AL VOLO
        storyChapters.add(new Level(
            "cap15_prep",
            "Preparazione al Volo",
            "✈️ L'AEREO È QUI!\n\n" +
            "È un piccolo Cessna, danneggiato ma riparabile.\n" +
            "Grazie alle istruzioni della TESI, sai come farlo partire.\n\n" +
            "Devi inserire il codice di accensione.\n" +
            "La TESI dice: 'Il codice è la somma dei numeri diviso 2'\n\n" +
            "4 + 8 + 15 + 16 + 23 + 42 = 108\n" +
            "108 / 2 = ?\n\n" +
            "❓ Qual è il codice di accensione?",
            Arrays.asList("54", "cinquantaquattro"),
            "108 diviso 2..."
        ));
        
        // CAPITOLO 16: LA FUGA
        Map<String, String> cap16Choices = new HashMap<>();
        cap16Choices.put("A", "Decollare subito!");
        cap16Choices.put("B", "Aspettare gli altri");
        cap16Choices.put("C", "Tornare indietro");
        storyChapters.add(new Level(
            "cap16_escape",
            "La Fuga",
            "🛫 IL MOTORE SI ACCENDE!\n\n" +
            "L'aereo vibra, pronto a partire.\n" +
            "Ma il Mostro di Fumo si avvicina!\n" +
            "Gli Altri corrono verso la pista!\n\n" +
            "Hai solo pochi secondi!\n\n" +
            "❓ Cosa fai?",
            cap16Choices,
            "A",
            "Non c'è tempo da perdere!"
        ));
        
        // CAPITOLO 17: FINALE - LIBERTÀ
        storyChapters.add(new Level(
            "cap17_freedom",
            "Libertà",
            "🌅 CE L'HAI FATTA!\n\n" +
            "L'aereo decolla, lasciandoti alle spalle l'isola.\n" +
            "Il Mostro di Fumo ruggisce impotente.\n" +
            "Gli Altri ti guardano volare via.\n\n" +
            "Sotto di te, l'isola diventa sempre più piccola...\n" +
            "Finché non scompare all'orizzonte.\n\n" +
            "Stringi la TESI tra le mani.\n" +
            "Ce l'hai fatta. Sei libero.\n\n" +
            "🎓 E ora... puoi finalmente laurearti!\n\n" +
            "❓ Digita 'fine' per concludere la tua avventura.",
            Arrays.asList("fine", "finito", "ok", "si"),
            "È finita!"
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
    
    public String processCommand(String command) {
        if (!gameRunning) {
            return "Il gioco è terminato!";
        }
        
        String cmd = command.trim().toLowerCase();
        String[] parts = cmd.split("\\s+", 2);
        String action = parts[0];
        String target = parts.length > 1 ? parts[1] : "";
        
        // Modalità narrativa
        if (narrativeMode) {
            // Gestione pulsanti rapidi A, B, C
            if (action.equals("a") || action.equals("b") || action.equals("c")) {
                return processChoice(action.toUpperCase());
            }
            
            switch (action) {
                case "avanti":
                case "continua":
                case "":
                    return startNextChapter();
                    
                case "rispondi":
                    if (target.isEmpty()) {
                        return "Devi scrivere una risposta!";
                    }
                    return answerChapter(target);
                
                case "scegli":
                    if (target.isEmpty()) {
                        return "Devi scegliere A, B o C!";
                    }
                    return processChoice(target.trim().toUpperCase());
                
                case "prendi":
                case "raccogli":
                case "afferra":
                case "piglia":
                case "take":
                    if (currentChapter == 12) { // Capitolo della TESI
                        return answerChapter("prendi");
                    }
                    return takeItemFromRoom(target);
                    
                case "lascia":
                case "posa":
                case "metti":
                case "drop":
                    return dropItem(target);
                    
                case "guarda":
                case "osserva":
                case "esamina":
                case "ispeziona":
                case "look":
                    return lookAt(target);
                    
                case "mangia":
                case "bevi":
                    return eatOrDrink(target);
                    
                case "attiva":
                case "accendi":
                case "carica":
                    return activateItem(target);
                    
                case "usa":
                    return player.useItem(target);
                    
                case "inventario":
                case "zaino":
                    return player.getInventoryString();
                    
                case "stato":
                case "status":
                    return player.getStatus();
                    
                case "aiuto":
                case "help":
                    return getHelpText();
                    
                default:
                    // Prova come risposta diretta
                    return answerChapter(cmd);
            }
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
    
    public String forceStartFirstChapter() {
        currentChapterStarted = false;
        currentChapterCompleted = false;
        return startNextChapter();
    }
    
    private String startNextChapter() {
        if (currentChapterStarted && !currentChapterCompleted && currentChapter < storyChapters.size()) {
            return "⚠️ Devi prima rispondere alla domanda!\n💡 Usa i pulsanti A, B, C o scrivi la risposta.";
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
            currentChapter++;
            currentChapterCompleted = true;
            currentChapterStarted = false;
            
            String success = "✅ CORRETTO!\n\n";
            
            // Aggiungi la TESI all'inventario nel capitolo giusto
            if (currentChapter == 13) {
                Item tesi = new Item("TESI", 
                    "📜 La TESI perduta! Contiene le coordinate per fuggire dall'isola!",
                    true, Item.ItemType.TESI, 0, -1);
                player.addItem(tesi);
                success += "📜 Hai ottenuto la TESI!\n\n";
            }
            
            if (currentChapter >= storyChapters.size()) {
                gameWon = true;
                success += getEpicEnding();
            } else {
                success += "Premi AVANTI per continuare...";
            }
            
            return success;
        } else {
            return "❌ Risposta sbagliata!\n💡 Suggerimento: " + chapter.getHint();
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
    
    private String getHelpText() {
        return "═══════════════════════════════════════\n" +
               "  ✈️ LOST THESIS - COMANDI ✈️\n" +
               "═══════════════════════════════════════\n" +
               "🔘 A, B, C - Scegli un'opzione\n" +
               "➡️ AVANTI - Continua la storia\n" +
               "📦 prendi/raccogli - Raccogli oggetto\n" +
               "👁️ guarda/esamina - Osserva oggetto\n" +
               "🎒 inventario - Vedi oggetti\n" +
               "❤️ stato - Vedi salute\n" +
               "🍎 mangia/bevi - Usa cibo/bevande\n" +
               "💣 attiva - Attiva oggetto\n" +
               "❓ aiuto - Questo messaggio\n" +
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
    private void processTimers() {
        // Timer dinamite
        if (dynamiteTimer > 0) {
            dynamiteTimer--;
            if (dynamiteTimer == 0 && dynamiteActive) {
                explodeDynamite();
            }
        }
        
        // Timer mostro di fumo (casuale)
        if (smokeMonsterTimer > 0) {
            smokeMonsterTimer--;
            if (smokeMonsterTimer == 0) {
                smokeMonsterNearby = true;
            }
        }
    }
    
    /**
     * Esplosione dinamite
     */
    private void explodeDynamite() {
        // Trova dove è la dinamite
        Item dinamite = player.getItem("dinamite");
        if (dinamite != null) {
            // Se ce l'hai in mano... BOOM!
            player.removeHealth(100);
            addLog("💥💥💥 BOOM! 💥💥💥\n" +
                   "La dinamite è esplosa TRA LE TUE MANI!\n" +
                   "Non avresti dovuto tenerla...\n\n" +
                   "☠️ SEI MORTO ☠️");
            gameRunning = false;
        } else {
            // Esplode nella stanza dove l'hai lasciata
            addLog("💥 BOOM! 💥\n" +
                   "Senti un'esplosione in lontananza.\n" +
                   "Qualcosa è stato distrutto...");
        }
        dynamiteActive = false;
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
    
    public Player getPlayer() { return player; }
    public boolean isNarrativeMode() { return narrativeMode; }
    public boolean isGameWon() { return gameWon; }
    public boolean isGameRunning() { return gameRunning; }
    public AudioManager getAudioManager() { return audioManager; }
}
