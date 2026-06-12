package com.lost.save;

import com.lost.model.Item;

/**
 * Versione serializzabile di Item per il salvataggio
 */
public class ItemData {
    private String name;
    private String description;
    private boolean takeable;
    private String type;
    private int usesRemaining;

    /** Costruttore vuoto richiesto per la deserializzazione JSON. */
    public ItemData() {}

    /**
     * Converte un Item di gioco nella sua versione serializzabile.
     * @param item oggetto da convertire
     * @return dati serializzabili dell'oggetto
     */
    public static ItemData fromItem(Item item) {
        ItemData data = new ItemData();
        data.name = item.getName();
        data.description = item.getDescription();
        data.takeable = item.isTakeable();
        data.type = item.getType().name();
        data.usesRemaining = item.getUsesRemaining();
        return data;
    }

    /**
     * Ricostruisce l'Item di gioco; se il tipo non e' valido usa GENERICO.
     * @return l'oggetto di gioco ricostruito
     */
    public Item toItem() {
        Item.ItemType itemType;
        try {
            itemType = Item.ItemType.valueOf(type);
        } catch (Exception e) {
            itemType = Item.ItemType.GENERICO;
        }
        return new Item(name, description, takeable, itemType, usesRemaining);
    }

    /** {@return il nome dell'oggetto} */
    public String getName() { return name; }
    /** {@return la descrizione dell'oggetto} */
    public String getDescription() { return description; }
    /** {@return true se l'oggetto puo' essere raccolto} */
    public boolean isTakeable() { return takeable; }
    /** {@return la categoria dell'oggetto come stringa} */
    public String getType() { return type; }
    /** {@return il numero di usi rimanenti (-1 = illimitati)} */
    public int getUsesRemaining() { return usesRemaining; }
}
