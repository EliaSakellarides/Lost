package com.lost.engine;

import com.lost.model.Item;
import com.lost.model.Room;

import java.util.HashMap;
import java.util.Map;

/**
 * Costruisce il mondo di gioco: le locazioni dell'isola, le loro
 * connessioni e gli oggetti raccoglibili. Separata dal motore per
 * isolare la definizione del mondo dalla logica di gioco.
 */
public final class WorldFactory {

    private WorldFactory() {
    }

    /**
     * Crea le stanze dell'isola, collegate e popolate di oggetti.
     * @return mappa chiave-stanza di tutte le locazioni
     */
    public static Map<String, Room> buildWorld() {
        Map<String, Room> allRooms = new HashMap<>();
        // SPIAGGIA - Punto di partenza
        Room spiaggia = new Room("spiaggia", "Spiaggia dello Schianto",
            "La spiaggia è coperta di rottami dell'aereo. " +
            "Il fumo sale ancora dalla fusoliera. " +
            "Il mare è calmo, ma la giungla dietro di te sembra ostile.");

        // GIUNGLA
        Room giungla = new Room("giungla", "Giungla Oscura",
            "Alberi altissimi bloccano la luce del sole. " +
            "Senti strani rumori... ticchettii meccanici. " +
            "Qualcosa di grosso si muove tra gli alberi.");
        giungla.setDangerous(true, "Il Mostro di Fumo potrebbe essere qui...");

        // BOTOLA
        Room botola = new Room("botola", "La Botola (Il Cigno)",
            "Una stazione sotterranea della DHARMA Initiative. " +
            "Computer antiquati, un pulsante misterioso, e molto cibo in scatola. " +
            "Desmond viveva qui, premendo il pulsante ogni 108 minuti.");

        // VILLAGGIO DEGLI ALTRI
        Room villaggio = new Room("villaggio", "Villaggio degli Altri",
            "Un villaggio apparentemente normale nel mezzo dell'isola. " +
            "Case bianche, giardini curati... ma qualcosa non quadra. " +
            "Gli abitanti ti osservano con sospetto.");

        // TEMPIO
        Room tempio = new Room("tempio", "Il Tempio",
            "Un antico tempio nascosto nella giungla. " +
            "I guardiani proteggono questo luogo sacro. " +
            "Al centro c'è una vasca con acqua misteriosa.");

        // ROCCIA NERA
        Room rocciaNera = new Room("roccianera", "La Roccia Nera",
            "Una nave schiavista del 1800 in mezzo alla giungla! " +
            "Come ci è arrivata? È piena di dinamite instabile. " +
            "Nei diari trovi mappe dell'isola.");

        // FARO
        Room faro = new Room("faro", "Il Faro",
            "Un faro antico sulla scogliera. " +
            "All'interno, uno specchio magico mostra luoghi lontani. " +
            "Jacob usava questo posto per osservare i candidati.");

        // PISTA
        Room pista = new Room("pista", "Pista di Atterraggio",
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
            Item.ItemType.CIBO, 3));
        spiaggia.addItem(new Item("Kit Medico", "Kit di pronto soccorso", true,
            Item.ItemType.MEDICINA, 2));
        spiaggia.addItem(new Item("Cavo antenna",
            "Un cavo coassiale strappato dal sistema radio dell'aereo.",
            true, Item.ItemType.STRUMENTO, -1));

        giungla.addItem(new Item("Radio danneggiata",
            "La radio del cockpit: schermo crepato, antenna spezzata e vano batteria vuoto.",
            true, Item.ItemType.STRUMENTO, -1));

        botola.addItem(new Item("Cibo DHARMA", "Scatolette con logo DHARMA", true,
            Item.ItemType.CIBO, 5));
        botola.addItem(new Item("Mappa DHARMA", "Mappa delle stazioni sull'isola", true,
            Item.ItemType.DOCUMENTO, -1));
        botola.addItem(new Item("Batteria DHARMA",
            "Una batteria pesante con morsetti ossidati ma ancora carica.",
            true, Item.ItemType.STRUMENTO, -1));
        botola.addItem(new Item("Fusibile",
            "Un fusibile di ricambio conservato in una scatola etichettata COMUNICAZIONI.",
            true, Item.ItemType.STRUMENTO, -1));

        rocciaNera.addItem(new Item("Dinamite", "ATTENZIONE: Altamente instabile!", true,
            Item.ItemType.STRUMENTO, 1));
        rocciaNera.addItem(new Item("Diario", "Diario del capitano con mappe", true,
            Item.ItemType.DOCUMENTO, -1));

        faro.addItem(new Item("Bussola", "Una vecchia bussola che punta sempre a nord", true,
            Item.ItemType.STRUMENTO, -1));

        // La mappa della pista Hydra viene consegnata nel capitolo La Scoperta

        return allRooms;
    }
}
