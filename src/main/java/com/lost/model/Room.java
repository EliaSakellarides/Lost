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
    
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }
    
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public Item removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (matchesItemName(items.get(i).getName(), itemName)) {
                return items.remove(i);
            }
        }
        return null;
    }
    
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
    
    /** Restituisce la descrizione completa della stanza con oggetti visibili e uscite. */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("📍 ").append(name).append("\n\n");
        sb.append(description).append("\n");
        
        // Mostra oggetti
        if (!items.isEmpty()) {
            sb.append("\n🔍 Vedi: ");
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(i).getName());
                if (i < items.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        
        // Mostra uscite
        if (!exits.isEmpty()) {
            sb.append("\n🚪 Uscite: ");
            sb.append(String.join(", ", exits.keySet()));
        }
        
        return sb.toString();
    }
    
    public void setDangerous(boolean dangerous, String description) {
        this.dangerous = dangerous;
        this.dangerDescription = description;
    }
    
    // Getters
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Map<String, Room> getExits() { return exits; }
    public List<Item> getItems() { return items; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean isDangerous() { return dangerous; }
    public String getDangerDescription() { return dangerDescription; }
}
