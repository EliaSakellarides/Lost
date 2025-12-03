package com.lostthesis.model;

/**
 * Rappresenta un oggetto nel gioco
 */
public class Item {
    
    public enum ItemType {
        GENERICO,
        CIBO,
        MEDICINA,
        ARMA,
        CHIAVE,
        DOCUMENTO,
        STRUMENTO,
        TESI  // L'oggetto finale per fuggire!
    }
    
    private String name;
    private String description;
    private boolean takeable;
    private ItemType type;
    private int healthBoost;
    private int usesRemaining;
    
    public Item(String name, String description, boolean takeable, ItemType type, 
                int healthBoost, int usesRemaining) {
        this.name = name;
        this.description = description;
        this.takeable = takeable;
        this.type = type;
        this.healthBoost = healthBoost;
        this.usesRemaining = usesRemaining;
    }
    
    // Costruttore semplificato
    public Item(String name, String description, boolean takeable) {
        this(name, description, takeable, ItemType.GENERICO, 0, -1);
    }
    
    public void use() {
        if (usesRemaining > 0) {
            usesRemaining--;
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isTakeable() { return takeable; }
    public ItemType getType() { return type; }
    public int getHealthBoost() { return healthBoost; }
    public int getUsesRemaining() { return usesRemaining; }
}
