package com.lost.model;

import java.util.*;

/**
 * Rappresenta il giocatore sopravvissuto
 */
public class Player {
    private String name;
    private Room currentRoom;
    private List<Item> inventory;
    private int maxInventorySize;
    private int health;           // Salute del giocatore
    private int maxHealth;
    private int sanity;           // Sanità mentale (l'isola è strana...)
    private int daysOnIsland;     // Giorni sull'isola
    
    /**
     * Crea il giocatore con statistiche iniziali al massimo.
     * @param name nome del giocatore
     */
    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.maxInventorySize = 10;
        this.health = 100;
        this.maxHealth = 100;
        this.sanity = 100;
        this.daysOnIsland = 1;
    }
    
    /**
     * Aggiunge un oggetto all'inventario, se c'e' spazio.
     * @param item oggetto da aggiungere
     * @return true se l'oggetto e' stato aggiunto
     */
    public boolean addItem(Item item) {
        if (inventory.size() >= maxInventorySize) {
            return false;
        }
        inventory.add(item);
        return true;
    }
    
    /**
     * Rimuove un oggetto dall'inventario cercandolo per nome.
     * @param itemName nome (anche parziale) dell'oggetto
     * @return l'oggetto rimosso, null se non trovato
     */
    public Item removeItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (matchesItemName(inventory.get(i).getName(), itemName)) {
                return inventory.remove(i);
            }
        }
        return null;
    }
    
    /**
     * Cerca un oggetto nell'inventario per nome.
     * @param itemName nome (anche parziale) dell'oggetto
     * @return l'oggetto trovato, null se assente
     */
    public Item getItem(String itemName) {
        for (Item item : inventory) {
            if (matchesItemName(item.getName(), itemName)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Verifica se un oggetto e' nell'inventario.
     * @param itemName nome (anche parziale) dell'oggetto
     * @return true se l'oggetto e' presente
     */
    public boolean hasItem(String itemName) {
        return getItem(itemName) != null;
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
     * Usa un oggetto dall'inventario. L'effetto dipende dal tipo:
     * CIBO/MEDICINA ripristinano salute, DOCUMENTO mostra testo, ecc.
     * Se l'oggetto esaurisce gli usi rimanenti, viene rimosso.
     * @param itemName nome (anche parziale) dell'oggetto da usare
     * @return testo che descrive l'effetto dell'uso
     */
    public String useItem(String itemName) {
        Item item = getItem(itemName);
        if (item == null) {
            return "Non hai questo oggetto nell'inventario!";
        }
        
        String result = "";
        switch (item.getType()) {
            case CIBO:
                health = Math.min(maxHealth, health + item.getHealthBoost());
                result = "Mangi " + item.getName() + ". +" + item.getHealthBoost() + " salute!";
                item.use();
                break;
            case MEDICINA:
                health = Math.min(maxHealth, health + item.getHealthBoost());
                sanity = Math.min(100, sanity + 10);
                result = "Usi " + item.getName() + ". Salute e sanità mentale migliorate!";
                item.use();
                break;
            case ARMA:
                result = "Impugni " + item.getName() + ". Sei pronto a difenderti!";
                break;
            case CHIAVE:
                result = item.getName() + " - Potrebbe aprire qualcosa...";
                break;
            case DOCUMENTO:
                result = "Leggi " + item.getName() + ":\n" + item.getDescription();
                break;
            default:
                result = "Usi " + item.getName() + "...";
        }
        
        if (item.getUsesRemaining() == 0) {
            removeItem(itemName);
            result += "\n(Oggetto esaurito)";
        }
        
        return result;
    }
    
    /**
     * Aumenta la salute, senza superare il massimo.
     * @param amount punti salute da aggiungere
     */
    public void addHealth(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    /**
     * Riduce la salute, senza scendere sotto zero.
     * @param amount punti salute da togliere
     */
    public void removeHealth(int amount) {
        health = Math.max(0, health - amount);
    }

    /**
     * Aumenta la sanita' mentale, senza superare 100.
     * @param amount punti sanita' da aggiungere
     */
    public void addSanity(int amount) {
        sanity = Math.min(100, sanity + amount);
    }

    /**
     * Riduce la sanita' mentale, senza scendere sotto zero.
     * @param amount punti sanita' da togliere
     */
    public void removeSanity(int amount) {
        sanity = Math.max(0, sanity - amount);
    }
    
    /** Avanza di un giorno. Ogni giorno sull'isola riduce la sanita' di 5 punti. */
    public void nextDay() {
        daysOnIsland++;
        // Ogni giorno perdi un po' di sanità
        removeSanity(5);
    }

    /**
     * Imposta i giorni trascorsi sull'isola (minimo 1).
     * @param daysOnIsland numero di giorni
     */
    public void setDaysOnIsland(int daysOnIsland) {
        this.daysOnIsland = Math.max(1, daysOnIsland);
    }
    
    /**
     * Riepilogo testuale dello stato del giocatore.
     * @return testo con giorno, salute, sanita' e inventario
     */
    public String getStatus() {
        String status = name + " - Giorno " + daysOnIsland + " sull'isola\n";
        status += "Salute: " + health + "/" + maxHealth + "\n";
        status += "Sanità: " + sanity + "/100\n";
        status += "Inventario: " + inventory.size() + "/" + maxInventorySize + " oggetti";
        return status;
    }
    
    /**
     * Elenco testuale degli oggetti nell'inventario.
     * @return testo formattato dell'inventario
     */
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Il tuo inventario è vuoto.";
        }
        StringBuilder sb = new StringBuilder("INVENTARIO:\n");
        for (Item item : inventory) {
            sb.append("  • ").append(item.getName());
            if (item.getUsesRemaining() > 0 && item.getUsesRemaining() < 99) {
                sb.append(" (usi: ").append(item.getUsesRemaining()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    // Getters e Setters
    /** {@return il nome del giocatore} */
    public String getName() { return name; }
    /** {@return la stanza in cui si trova il giocatore} */
    public Room getCurrentRoom() { return currentRoom; }
    /**
     * Sposta il giocatore in una stanza.
     * @param room stanza di destinazione
     */
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    /** {@return la lista degli oggetti nell'inventario} */
    public List<Item> getInventory() { return inventory; }
    /** {@return la salute corrente (0-100)} */
    public int getHealth() { return health; }
    /** {@return la sanita' mentale corrente (0-100)} */
    public int getSanity() { return sanity; }
    /** {@return i giorni trascorsi sull'isola} */
    public int getDaysOnIsland() { return daysOnIsland; }
    /** {@return true se il giocatore e' vivo} */
    public boolean isAlive() { return health > 0; }
    /** {@return true se il giocatore e' ancora sano di mente} */
    public boolean isSane() { return sanity > 0; }
}
