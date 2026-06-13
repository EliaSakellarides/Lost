package com.lost.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Costruisce l'elenco ordinato dei capitoli della storia.
 * Separata dal motore per tenere il contenuto narrativo isolato
 * dalla logica di gioco.
 */
public final class StoryFactory {

    private StoryFactory() {
    }

    /**
     * Crea i capitoli della storia di Lost.
     * @param playerName nome del giocatore, inserito in alcuni capitoli
     * @return lista ordinata dei capitoli
     */
    public static List<Level> buildChapters(String playerName) {
        List<Level> chapters = new ArrayList<>();

        // ═══════════════════════════════════════════════════════════════
        // CAPITOLI RIVISITATI - Focus su SOPRAVVIVENZA e cronologia LOST
        // ═══════════════════════════════════════════════════════════════

        // CAPITOLO 1: LA PRIMA NOTTE
        Map<String, String> cap1Choices = new HashMap<>();
        cap1Choices.put("A", "Accendere un fuoco");
        cap1Choices.put("B", "Costruire un riparo");
        cap1Choices.put("C", "Esplorare i dintorni");
        chapters.add(new Level(
            "cap1_firstnight",
            "La Prima Notte",
            "LA PRIMA NOTTE SULL'ISOLA\n\n" +
            "Il sole sta tramontando. La spiaggia si fa buia.\n" +
            "I sopravvissuti si guardano intorno, spaventati.\n" +
            "Dalla giungla arrivano suoni inquietanti...\n\n" +
            "Come vi preparate per la notte?",
            cap1Choices,
            "A",
            "Il fuoco tiene lontani i predatori e solleva il morale!"
        ));

        // CAPITOLO 2: I SOPRAVVISSUTI - Organizzare il campo
        Map<String, String> cap2Choices = new HashMap<>();
        cap2Choices.put("A", "48");
        cap2Choices.put("B", "23");
        cap2Choices.put("C", "108");
        chapters.add(new Level(
            "cap2_survivors",
            "I Sopravvissuti",
            "Sei sulla spiaggia con gli altri sopravvissuti.\n\n" +
            "Jack, il medico, sta organizzando il campo.\n" +
            playerName + " lo aiuta a contare i sopravvissuti.\n" +
            "Jack mormora: 'Quasi cinquanta... ne mancano due alla conta di ieri.'\n" +
            "Kate raccoglie provviste dai rottami.\n" +
            "Sawyer sta già litigando con qualcuno...\n\n" +
            "Qualcuno chiede: 'Quanti siamo sopravvissuti?'\n\n" +
            "Quanti passeggeri sono sopravvissuti allo schianto?",
            cap2Choices,
            "A",
            "48 sopravvissuti iniziali!"
        ));

        // CAPITOLO 3: IL MOSTRO DI FUMO - Cabina di pilotaggio + morte del pilota
        Map<String, String> cap3Choices = new HashMap<>();
        cap3Choices.put("A", "Corri via!");
        cap3Choices.put("B", "Resta immobile");
        cap3Choices.put("C", "Arrampicati su un albero");
        chapters.add(new Level(
            "cap3_smoke",
            "Il Mostro di Fumo",
            "GIORNO 1 - LA GIUNGLA\n\n" +
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
            "Il fumo nero ora e' davanti a te. Non sembra un animale.\n" +
            "Ti osserva. Ogni movimento potrebbe attirarlo.\n\n" +
            "Cosa fai?",
            cap3Choices,
            "B",
            "Chi si muove attira il mostro. Restare immobili e' l'unica via."
        ));

        // CAPITOLO 4: LE GROTTE - Trovare acqua (NUOVO)
        Map<String, String> cap4Choices = new HashMap<>();
        cap4Choices.put("A", "Trasferirsi alle grotte");
        cap4Choices.put("B", "Restare sulla spiaggia");
        cap4Choices.put("C", "Dividere il gruppo");
        chapters.add(new Level(
            "cap4_caves",
            "Le Grotte",
            "GIORNO 3 - EMERGENZA ACQUA\n\n" +
            "L'acqua delle bottiglie sta finendo.\n" +
            playerName + " ha trovato delle GROTTE con acqua dolce!\n\n" +
            "Ma sono nella giungla, lontano dalla spiaggia...\n" +
            "Lontano dai possibili soccorsi.\n\n" +
            "Il gruppo è diviso. Cosa proponi?\n\n" +
            "Qual è la scelta migliore per sopravvivere?",
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
            "GIORNO 5 - IL CIBO SCARSEGGIA\n\n" +
            "Le provviste dell'aereo sono quasi finite.\n" +
            "Locke ha visto dei CINGHIALI nella giungla.\n" +
            "Jin sa pescare. Hurley ha trovato alberi di frutta.\n\n" +
            "Servono proteine per sopravvivere a lungo termine.\n\n" +
            "Come procurarsi il cibo?",
            cap5Choices,
            "A",
            "I cinghiali sono la fonte di proteine più affidabile sull'isola!"
        );
        cap5.setMiniGameKey("jungle_tracking");
        chapters.add(cap5);

        // CAPITOLO 6: LA BOTOLA - Scoperta (non aperta)
        Map<String, String> cap6Choices = new HashMap<>();
        cap6Choices.put("A", "Provare ad aprirla");
        cap6Choices.put("B", "Lasciare perdere");
        cap6Choices.put("C", "Cercare un altro modo");
        chapters.add(new Level(
            "cap6_hatch",
            "La Botola",
            "GIORNO 8 - LA SCOPERTA DI LOCKE\n\n" +
            "Locke e Boone hanno trovato qualcosa nella giungla!\n" +
            "Una BOTOLA di metallo sepolta nel terreno.\n\n" +
            "C'è scritto 'QUARANTINE' e un simbolo: DHARMA.\n" +
            "È sigillata, impossibile aprirla a mani nude.\n\n" +
            "Locke è ossessionato. Vuole sapere cosa c'è sotto.\n\n" +
            "Cosa consigli?",
            cap6Choices,
            "C",
            "Serve qualcosa di potente per aprirla... come degli ESPLOSIVI!"
        ));

        // CAPITOLO 7: LA ROCCIA NERA - Trovare la dinamite nella stiva
        Map<String, String> cap7Choices = new HashMap<>();
        cap7Choices.put("A", "Aprire la cassa nella stiva");
        cap7Choices.put("B", "Lasciare perdere e tornare indietro");
        cap7Choices.put("C", "Cercare qualcosa sul ponte");
        chapters.add(new Level(
            "cap7_blackrock",
            "La Roccia Nera",
            "LA NAVE NELLA GIUNGLA\n\n" +
            "Rousseau vi ha parlato della ROCCIA NERA.\n" +
            "Una nave del 1800 arenata nel mezzo dell'isola!\n\n" +
            "Nella stiva senti odore di polvere e salsedine.\n" +
            "Tra casse marce e catene arrugginite, noti un baule inchiodato.\n\n" +
            "Locke ti guarda: 'Quello non e' finito qui per caso.'\n" +
            "Potrebbe esserci proprio l'esplosivo che vi serve per la botola.\n\n" +
            "Cosa fai?",
            cap7Choices,
            "A",
            "La cosa piu' promettente e' quella cassa chiusa nella stiva."
        ));

        // CAPITOLO 8: APRIRE LA BOTOLA (azione: serve la dinamite)
        Level cap8 = new Level(
            "cap8_openhatch",
            "Aprire la Botola",
            "IL MOMENTO DELLA VERITÀ\n\n" +
            "Siete tornati alla botola.\n" +
            "Locke ti fissa: 'Tocca a te. Sistema la carica.'\n" +
            "Hurley impallidisce vedendo i numeri incisi sul metallo:\n" +
            "4 8 15 16 23 42.\n\n" +
            "'Non mi piacciono per niente...'\n\n" +
            "Scrivi 'usa dinamite' per piazzare la carica sulla botola.\n" +
            "(L'avete presa dalla stiva della Roccia Nera, vero?)",
            Arrays.asList("usa dinamite", "dinamite", "attiva dinamite", "a"),
            "Serve la dinamite della Roccia Nera: prendila e poi 'usa dinamite'."
        );
        cap8.setQuickAnswerLabel("USA DINAMITE");
        chapters.add(cap8);

        // CAPITOLO 9: IL CIGNO - Desmond + MINI GIOCO
        Map<String, String> cap9Choices = new HashMap<>();
        cap9Choices.put("A", "Premere il pulsante");
        cap9Choices.put("B", "Non premere");
        cap9Choices.put("C", "Chiedere spiegazioni");
        Level cap9 = new Level(
            "cap9_swan",
            "La Stazione Il Cigno",
            "DENTRO LA BOTOLA\n\n" +
            "Scendete nella stazione sotterranea.\n" +
            "Trovi un uomo, DESMOND, che vive qui da 3 anni!\n\n" +
            "'Brother! Finalmente qualcuno!'\n" +
            "'Devo premere il pulsante ogni 108 minuti!'\n" +
            "'Se non lo faccio... il mondo finisce!'\n\n" +
            "In un armadietto DHARMA noti pezzi radio, batterie e fusibili.\n" +
            "Sayid osserva la radio danneggiata recuperata nella cabina:\n" +
            "'Si puo' riparare, ma serve alimentazione, antenna e un fusibile sano.'\n\n" +
            "Un timer sta per scadere: 00:01:30\n" +
            "Il computer mostra: 4 8 15 16 23 42\n\n" +
            "Il timer sta per scadere! Cosa fai?",
            cap9Choices,
            "A",
            "Meglio non rischiare... per ora!"
        );
        chapters.add(cap9);

        // CAPITOLO 10: HENRY GALE - Il prigioniero misterioso
        Map<String, String> cap10Choices = new HashMap<>();
        cap10Choices.put("A", "Si, dice la verita'");
        cap10Choices.put("B", "No, sta mentendo");
        cap10Choices.put("C", "Non sei sicuro");
        chapters.add(new Level(
            "cap10_henrygale",
            "Il Prigioniero",
            "UN UOMO NELLA TRAPPOLA\n\n" +
            "Trovate un uomo catturato nella trappola di Rousseau.\n" +
            "Si presenta con calma:\n\n" +
            "\"Mi chiamo Henry Gale.\n" +
            "Sono un pallonista del Minnesota.\n" +
            "Il mio pallone si e' schiantato sull'isola mesi fa.\"\n\n" +
            "La storia sembra plausibile. E' ferito, sembra spaventato.\n" +
            "Ma i suoi occhi... non corrispondono alle parole.\n\n" +
            "Ti fidi di lui?",
            cap10Choices,
            "B",
            "Henry Gale non esiste. Quest'uomo e' Ben Linus, il leader degli Altri."
        ));

        // CAPITOLO 11: GLI ALTRI - Cattura
        Map<String, String> cap11aChoices = new HashMap<>();
        cap11aChoices.put("A", "Collaborare");
        cap11aChoices.put("B", "Resistere");
        cap11aChoices.put("C", "Cercare di fuggire");
        chapters.add(new Level(
            "cap11_others",
            "Gli Altri",
            "CATTURATO!\n\n" +
            "Durante una spedizione, vieni catturato dagli ALTRI!\n" +
            "Ti portano in un villaggio nascosto.\n\n" +
            "Ben ti guarda con calma - lo stesso uomo del bunker.\n" +
            "'Sappiamo tutto di te.'\n" +
            "'Sappiamo perche' sei su quest'isola.'\n" +
            "'La domanda e': tu lo sai?'\n\n" +
            "Come reagisci?",
            cap11aChoices,
            "C",
            "Mai fidarsi degli Altri... cerca un modo per scappare!"
        ));

        // CAPITOLO 11: LA FUGA DAGLI ALTRI
        Map<String, String> cap11Choices = new HashMap<>();
        cap11Choices.put("A", "Attraverso la giungla");
        cap11Choices.put("B", "Seguire il fiume");
        cap11Choices.put("C", "Verso la costa");
        chapters.add(new Level(
            "cap11_escape_others",
            "La Fuga",
            "DEVI SCAPPARE!\n\n" +
            "Riesci a liberarti dalle corde durante la notte.\n" +
            "Il villaggio è silenzioso, le guardie distratte.\n" +
            "Ti rifugi tra le mura di un antico TEMPIO ai margini del villaggio.\n\n" +
            "Da qui vedi tre vie di fuga possibili:\n" +
            "• La giungla - pericolosa ma diretta\n" +
            "• Il fiume - più lungo ma facile da seguire\n" +
            "• La costa - esposto ma familiare\n\n" +
            "Il Mostro di Fumo è stato visto nella giungla...\n\n" +
            "Quale strada prendi?",
            cap11Choices,
            "B",
            "Il fiume ti riporterà al campo... e l'acqua copre le tue tracce!"
        ));

        // CAPITOLO 13: LA ZATTERA - Partenza
        Map<String, String> cap12Choices = new HashMap<>();
        cap12Choices.put("A", "Partire con la zattera");
        cap12Choices.put("B", "Restare sull'isola");
        cap12Choices.put("C", "Aspettare i soccorsi");
        chapters.add(new Level(
            "cap12_raft",
            "La Zattera",
            "IL PIANO DI MICHAEL\n\n" +
            "Michael ha costruito una zattera in poche settimane.\n" +
            "Jin, Sawyer e Walt partiranno con lui.\n\n" +
            "E' l'unica via concreta per uscire dall'isola.\n" +
            "L'oceano e' immenso, ma restare non e' piu' un'opzione.\n\n" +
            "Cosa fai?",
            cap12Choices,
            "A",
            "Meglio rischiare il mare che restare prigionieri sull'isola!"
        ));

        // CAPITOLO 14: IL RAPIMENTO DI WALT
        chapters.add(new Level(
            "cap13_walt",
            "In Mare Aperto",
            "NOTTE - OCEANO APERTO\n\n" +
            "La zattera naviga da ore nel buio.\n" +
            "Poi una barca si avvicina a tutta velocita'.\n\n" +
            "\"Avete visto un bambino?\"\n\n" +
            "Prima che possiate rispondere, sparano.\n" +
            "La zattera esplode.\n\n" +
            "Walt viene RAPITO dagli Altri.\n" +
            "Michael urla il suo nome nel buio.\n" +
            "Sawyer e' ferito. Siete in acqua.\n\n" +
            "Come tornate a riva?",
            Arrays.asList("nuotare", "nuoto", "a nuoto", "nuotiamo", "a"),
            "L'unica via e' tornare a nuoto verso l'isola."
        ));

        // CAPITOLO 15: I FLASHBACK - Ricordi
        Map<String, String> cap13Choices = new HashMap<>();
        cap13Choices.put("A", "815");
        cap13Choices.put("B", "316");
        cap13Choices.put("C", "777");
        chapters.add(new Level(
            "cap13_flashback",
            "Flashback",
            "QUELLA NOTTE... UN SOGNO\n\n" +
            "Ti ricordi di quando sei salito sull'aereo.\n" +
            "L'aeroporto di Sydney era affollato.\n" +
            "L'altoparlante gracchiava l'ultima chiamata per Los Angeles.\n\n" +
            "Ti svegli di colpo, con una certezza:\n" +
            "i documenti DHARMA nel bunker nascondono\n" +
            "piu' risposte di quante ne abbiate trovate.\n\n" +
            "Nella tasca trovi il biglietto sgualcito: OCEANIC 8_5.\n" +
            "La cifra di mezzo e' illeggibile... ma la somma dei Numeri e' 108.\n\n" +
            "Qual era il numero del volo Oceanic?",
            cap13Choices,
            "A",
            "Oceanic Flight 815!"
        ));

        // CAPITOLO 14: LA SCOPERTA DELLA MAPPA
        Level cap14 = new Level(
            "cap14_map",
            "La Scoperta",
            "NEL BUNKER DHARMA...\n\n" +
            "Esplori la stazione Il Cigno più a fondo.\n" +
            "Trovi una stanza segreta dietro una parete!\n\n" +
            "All'interno... documenti DHARMA!\n" +
            "Tra le cartelle trovi una MAPPA con coordinate scritte a mano.\n\n" +
            "'COORDINATE: PISTA DI ATTERRAGGIO HYDRA'\n" +
            "'Per emergenze. Aereo funzionante.'\n\n" +
            "C'è un AEREO nascosto sull'isola!\n\n" +
            "Premi PRENDI per recuperare la mappa.",
            Arrays.asList("prendi", "raccogli", "ok", "si", "a"),
            "Prendi la MAPPA: e' la tua via di fuga."
        );
        cap14.setQuickAnswerLabel("PRENDI");
        chapters.add(cap14);

        // CAPITOLO 15: LA PISTA NASCOSTA
        Map<String, String> cap15Choices = new HashMap<>();
        cap15Choices.put("A", "Andare subito");
        cap15Choices.put("B", "Prepararsi bene");
        cap15Choices.put("C", "Portare tutti");
        chapters.add(new Level(
            "cap15_runway",
            "La Pista Nascosta",
            "LA SPERANZA!\n\n" +
            "Segui le coordinate della mappa.\n" +
            "Attraversi territori pericolosi.\n" +
            "Raggiungi il vecchio FARO sulla scogliera e da lassu' scruti la costa.\n" +
            "Il Mostro di Fumo ruggisce in lontananza.\n\n" +
            "La radio riparata puo' confermare il segnale, ma la mappa basta per orientarti.\n" +
            "Portare tutto il gruppo adesso sarebbe troppo rischioso.\n\n" +
            "Ma finalmente... LA VEDI!\n" +
            "Una pista di atterraggio nascosta!\n" +
            "E un piccolo AEREO Cessna sotto un telo!\n\n" +
            "Come procedi?",
            cap15Choices,
            "B",
            "Meglio prepararsi: andare subito o portare tutti metterebbe il gruppo in pericolo."
        ));

        // CAPITOLO 16: PREPARAZIONE AL VOLO
        Map<String, String> cap16Choices = new HashMap<>();
        cap16Choices.put("A", "Controllare carburante, motore e comandi");
        cap16Choices.put("B", "Decollare subito");
        cap16Choices.put("C", "Aspettare ancora");
        chapters.add(new Level(
            "cap16_prep",
            "Preparazione al Volo",
            "L'AEREO!\n\n" +
            "È un Cessna 172, danneggiato ma riparabile.\n" +
            "Trovate carburante, una cassetta degli attrezzi e un vecchio manuale.\n\n" +
            "Il sole sta calando e la giungla non restera' silenziosa a lungo.\n" +
            "Serve una preparazione semplice ma fatta bene.\n\n" +
            "Prima di partire, cosa fai?",
            cap16Choices,
            "A",
            "Prima di decollare bisogna controllare carburante, motore e comandi."
        ));

        // CAPITOLO 17: LA FUGA FINALE
        Map<String, String> cap17Choices = new HashMap<>();
        cap17Choices.put("A", "Decollare ORA!");
        cap17Choices.put("B", "Aspettare gli altri");
        cap17Choices.put("C", "Tornare indietro");
        chapters.add(new Level(
            "cap17_escape",
            "Il Decollo",
            "IL MOMENTO È ARRIVATO!\n\n" +
            "Il motore si accende! L'elica gira!\n" +
            "Ma qualcosa non va...\n\n" +
            "Il MOSTRO DI FUMO appare dalla giungla!\n" +
            "Gli ALTRI corrono verso la pista!\n" +
            "Ben grida: 'NON PUOI ANDARTENE!'\n\n" +
            "Gli altri sopravvissuti sono nascosti al sicuro. Se aspetti, perdi l'unica finestra.\n" +
            "Partire ora puo' permetterti di tornare con soccorsi veri.\n\n" +
            "Hai solo pochi secondi per decidere!\n\n" +
            "Cosa fai?",
            cap17Choices,
            "A",
            "Non c'è tempo! DECOLLA!"
        ));

        // CAPITOLO 18: LIBERTÀ - FINALE
        Level cap18 = new Level(
            "cap18_freedom",
            "Libertà",
            "CE L'HAI FATTA!\n\n" +
            "L'aereo decolla, lasciandoti alle spalle l'isola.\n" +
            "Il Mostro di Fumo ruggisce impotente sotto di te.\n" +
            "Gli Altri diventano puntini sulla pista.\n\n" +
            "Sotto di te, l'isola diventa sempre più piccola...\n" +
            "Finché non scompare all'orizzonte.\n\n" +
            "L'oceano infinito si stende davanti a te.\n" +
            "Sei LIBERO. Finalmente LIBERO!\n\n" +
            "Ce l'hai fatta davvero.\n\n" +
            "Premi FINE per concludere.",
            Arrays.asList("fine", "finito", "ok", "si", "a"),
            "È finita... o forse no?"
        );
        cap18.setQuickAnswerLabel("FINE");
        chapters.add(cap18);
        return chapters;
    }
}
