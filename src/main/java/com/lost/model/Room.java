package com.lost.model;

import java.util.*;

/**
 * Rappresenta una locazione sull'isola
 */
public class Room {
    private String key;
    private String name;
    private String description;
    private Map<String, Room> exits;
    private List<Item> items;
    private boolean visited;
    private boolean dangerous;
    private String dangerDescription;
    
    /**
     * Crea una locazione dell'isola.
     * @param key chiave univoca della stanza (es. "spiaggia")
     * @param name nome visualizzato
     * @param description descrizione narrativa
     */
    public Room(String key, String name, String description) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.visited = false;
        this.dangerous = false;
        this.dangerDescription = "";
    }
    
    /**
     * Collega questa stanza a un'altra in una direzione.
     * @param direction direzione dell'uscita (es. "nord")
     * @param room stanza di destinazione
     */
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    /**
     * Restituisce la stanza raggiungibile in una direzione.
     * @param direction direzione richiesta
     * @return la stanza collegata, null se non c'e' uscita
     */
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    /**
     * Aggiunge un oggetto alla stanza.
     * @param item oggetto da aggiungere
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Rimuove un oggetto dalla stanza cercandolo per nome (match tollerante).
     * @param itemName nome o parte del nome dell'oggetto
     * @return l'oggetto rimosso, null se non trovato
     */
    public Item removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (matchesItemName(items.get(i).getName(), itemName)) {
                return items.remove(i);
            }
        }
        return null;
    }
    
    /**
     * Cerca un oggetto nella stanza per nome senza rimuoverlo.
     * @param itemName nome o parte del nome dell'oggetto
     * @return l'oggetto trovato, null se assente
     */
    public Item getItem(String itemName) {
        for (Item item : items) {
            if (matchesItemName(item.getName(), itemName)) {
                return item;
            }
        }
        return null;
    }

    private boolean matchesItemName(String actualName, String query) {
        String actual = normalizeItemName(actualName);
        String wanted = normalizeItemName(query);
        return !wanted.isEmpty() && (actual.equals(wanted) || actual.contains(wanted) || wanted.contains(actual));
    }

    private String normalizeItemName(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
            .replace("'", " ")
            .replaceAll("\\b(il|lo|la|i|gli|le|un|uno|una|con|sul|sulla|nel|nella|alla|allo)\\b", " ")
            .replaceAll("[^a-z0-9àèéìòù]+", " ")
            .trim()
            .replaceAll("\\s+", " ");
    }
    
    /**
     * Restituisce la descrizione completa della stanza con oggetti visibili e uscite.
     * @return testo descrittivo pronto per la visualizzazione
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n\n");
        sb.append(description).append("\n");
        
        // Mostra oggetti
        if (!items.isEmpty()) {
            sb.append("\nVedi: ");
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(i).getName());
                if (i < items.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        
        // Mostra uscite
        if (!exits.isEmpty()) {
            sb.append("\nUscite: ");
            sb.append(String.join(", ", exits.keySet()));
        }
        
        return sb.toString();
    }
    
    /**
     * Marca la stanza come pericolosa.
     * @param dangerous true se la stanza e' pericolosa
     * @param description descrizione del pericolo
     */
    public void setDangerous(boolean dangerous, String description) {
        this.dangerous = dangerous;
        this.dangerDescription = description;
    }

    // Getters
    /** {@return la chiave univoca della stanza} */
    public String getKey() { return key; }
    /** {@return il nome visualizzato della stanza} */
    public String getName() { return name; }
    /** {@return la descrizione narrativa della stanza} */
    public String getDescription() { return description; }
    /** {@return le uscite della stanza (direzione, destinazione)} */
    public Map<String, Room> getExits() { return exits; }
    /** {@return gli oggetti presenti nella stanza} */
    public List<Item> getItems() { return items; }
    /** {@return true se la stanza e' gia' stata visitata} */
    public boolean isVisited() { return visited; }
    /**
     * Imposta il flag di stanza visitata.
     * @param visited true se visitata
     */
    public void setVisited(boolean visited) { this.visited = visited; }
    /** {@return true se la stanza e' pericolosa} */
    public boolean isDangerous() { return dangerous; }
    /** {@return la descrizione del pericolo presente} */
    public String getDangerDescription() { return dangerDescription; }
}
