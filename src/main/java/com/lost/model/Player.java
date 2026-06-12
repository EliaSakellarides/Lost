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
    private int daysOnIsland;     // Giorni sull'isola

    /**
     * Crea il giocatore al primo giorno sull'isola.
     * @param name nome del giocatore
     */
    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.maxInventorySize = 10;
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
     * CIBO/MEDICINA danno una risposta narrativa, DOCUMENTO mostra testo, ecc.
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
                result = "Mangi " + item.getName() + ". Ti rimette in forze!";
                item.use();
                break;
            case MEDICINA:
                result = "Usi " + item.getName() + ". Le ferite sono medicate.";
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
     * Imposta i giorni trascorsi sull'isola (minimo 1).
     * @param daysOnIsland numero di giorni
     */
    public void setDaysOnIsland(int daysOnIsland) {
        this.daysOnIsland = Math.max(1, daysOnIsland);
    }
    
    /**
     * Riepilogo testuale dello stato del giocatore.
     * @return testo con giorno, posizione e inventario
     */
    public String getStatus() {
        String status = name + " - Giorno " + daysOnIsland + " sull'isola\n";
        if (currentRoom != null) {
            status += "Posizione: " + currentRoom.getName() + "\n";
        }
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
    /** {@return i giorni trascorsi sull'isola} */
    public int getDaysOnIsland() { return daysOnIsland; }
}
