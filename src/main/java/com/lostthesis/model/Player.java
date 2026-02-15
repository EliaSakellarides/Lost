package com.lostthesis.model;

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
    
    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.maxInventorySize = 10;
        this.health = 100;
        this.maxHealth = 100;
        this.sanity = 100;
        this.daysOnIsland = 1;
    }
    
    public boolean addItem(Item item) {
        if (inventory.size() >= maxInventorySize) {
            return false;
        }
        inventory.add(item);
        return true;
    }
    
    public Item removeItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(itemName)) {
                return inventory.remove(i);
            }
        }
        return null;
    }
    
    public Item getItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
    
    public boolean hasItem(String itemName) {
        return getItem(itemName) != null;
    }
    
    public String useItem(String itemName) {
        Item item = getItem(itemName);
        if (item == null) {
            return "❌ Non hai questo oggetto nell'inventario!";
        }
        
        String result = "";
        switch (item.getType()) {
            case CIBO:
                health = Math.min(maxHealth, health + item.getHealthBoost());
                result = "🍽️ Mangi " + item.getName() + ". +" + item.getHealthBoost() + " salute!";
                item.use();
                break;
            case MEDICINA:
                health = Math.min(maxHealth, health + item.getHealthBoost());
                sanity = Math.min(100, sanity + 10);
                result = "💊 Usi " + item.getName() + ". Salute e sanità mentale migliorate!";
                item.use();
                break;
            case ARMA:
                result = "🔪 Impugni " + item.getName() + ". Sei pronto a difenderti!";
                break;
            case CHIAVE:
                result = "🔑 " + item.getName() + " - Potrebbe aprire qualcosa...";
                break;
            case DOCUMENTO:
                result = "📄 Leggi " + item.getName() + ":\n" + item.getDescription();
                break;
            case TESI:
                result = "📜 LA TESI! Contiene le coordinate per fuggire dall'isola!\n" +
                        "Devi raggiungere l'aereo sulla pista nascosta!";
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
    
    public void addHealth(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    public void removeHealth(int amount) {
        health = Math.max(0, health - amount);
    }
    
    public void addSanity(int amount) {
        sanity = Math.min(100, sanity + amount);
    }
    
    public void removeSanity(int amount) {
        sanity = Math.max(0, sanity - amount);
    }
    
    public void nextDay() {
        daysOnIsland++;
        // Ogni giorno perdi un po' di sanità
        removeSanity(5);
    }
    
    public String getStatus() {
        String status = "👤 " + name + " - Giorno " + daysOnIsland + " sull'isola\n";
        status += "❤️ Salute: " + health + "/" + maxHealth + "\n";
        status += "🧠 Sanità: " + sanity + "/100\n";
        status += "🎒 Inventario: " + inventory.size() + "/" + maxInventorySize + " oggetti";
        return status;
    }
    
    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "🎒 Il tuo inventario è vuoto.";
        }
        StringBuilder sb = new StringBuilder("🎒 INVENTARIO:\n");
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
    public String getName() { return name; }
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public List<Item> getInventory() { return inventory; }
    public int getHealth() { return health; }
    public int getSanity() { return sanity; }
    public int getDaysOnIsland() { return daysOnIsland; }
    public boolean isAlive() { return health > 0; }
    public boolean isSane() { return sanity > 0; }
}
