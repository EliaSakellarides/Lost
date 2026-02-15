package com.lostthesis.save;

import com.lostthesis.model.Item;

/**
 * Versione serializzabile di Item per il salvataggio
 */
public class ItemData {
    private String name;
    private String description;
    private boolean takeable;
    private String type;
    private int healthBoost;
    private int usesRemaining;

    public ItemData() {}

    public static ItemData fromItem(Item item) {
        ItemData data = new ItemData();
        data.name = item.getName();
        data.description = item.getDescription();
        data.takeable = item.isTakeable();
        data.type = item.getType().name();
        data.healthBoost = item.getHealthBoost();
        data.usesRemaining = item.getUsesRemaining();
        return data;
    }

    public Item toItem() {
        Item.ItemType itemType;
        try {
            itemType = Item.ItemType.valueOf(type);
        } catch (Exception e) {
            itemType = Item.ItemType.GENERICO;
        }
        return new Item(name, description, takeable, itemType, healthBoost, usesRemaining);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isTakeable() { return takeable; }
    public String getType() { return type; }
    public int getHealthBoost() { return healthBoost; }
    public int getUsesRemaining() { return usesRemaining; }
}
