package com.lost.model;

/**
 * Rappresenta un oggetto nel gioco
 */
public class Item {
    
    /** Categorie di oggetti del gioco. */
    public enum ItemType {
        /** Oggetto generico senza effetti particolari. */
        GENERICO,
        /** Cibo o bevanda, ripristina salute. */
        CIBO,
        /** Medicinale, cura il giocatore. */
        MEDICINA,
        /** Arma o attrezzo offensivo. */
        ARMA,
        /** Chiave o componente che sblocca qualcosa. */
        CHIAVE,
        /** Documento leggibile (mappe, diari, lettere). */
        DOCUMENTO,
        /** Strumento utilizzabile (torcia, radio, ecc.). */
        STRUMENTO
    }
    
    private String name;
    private String description;
    private boolean takeable;
    private ItemType type;
    private int healthBoost;
    private int usesRemaining;
    
    /**
     * Crea un oggetto completo.
     * @param name nome dell'oggetto
     * @param description descrizione mostrata al giocatore
     * @param takeable true se l'oggetto puo' essere raccolto
     * @param type categoria dell'oggetto
     * @param healthBoost punti salute restituiti all'uso
     * @param usesRemaining numero di usi disponibili (-1 = illimitati)
     */
    public Item(String name, String description, boolean takeable, ItemType type,
                int healthBoost, int usesRemaining) {
        this.name = name;
        this.description = description;
        this.takeable = takeable;
        this.type = type;
        this.healthBoost = healthBoost;
        this.usesRemaining = usesRemaining;
    }
    
    /**
     * Costruttore semplificato per oggetti generici senza effetti.
     * @param name nome dell'oggetto
     * @param description descrizione mostrata al giocatore
     * @param takeable true se l'oggetto puo' essere raccolto
     */
    public Item(String name, String description, boolean takeable) {
        this(name, description, takeable, ItemType.GENERICO, 0, -1);
    }
    
    /** Consuma un uso dell'oggetto, se ne restano. */
    public void use() {
        if (usesRemaining > 0) {
            usesRemaining--;
        }
    }
    
    // Getters
    /** {@return il nome dell'oggetto} */
    public String getName() { return name; }
    /** {@return la descrizione dell'oggetto} */
    public String getDescription() { return description; }
    /** {@return true se l'oggetto puo' essere raccolto} */
    public boolean isTakeable() { return takeable; }
    /** {@return la categoria dell'oggetto} */
    public ItemType getType() { return type; }
    /** {@return i punti salute restituiti all'uso} */
    public int getHealthBoost() { return healthBoost; }
    /** {@return il numero di usi rimanenti (-1 = illimitati)} */
    public int getUsesRemaining() { return usesRemaining; }
}
