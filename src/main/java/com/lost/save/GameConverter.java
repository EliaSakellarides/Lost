package com.lost.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lost.engine.GameEngine;
import com.lost.model.Item;
import com.lost.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converte tra GameEngine e GameState (JSON) usando Gson
 */
public final class GameConverter {

    private GameConverter() {
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Estrae lo stato corrente dal GameEngine in un GameState serializzabile
     * @param engine motore di gioco da cui leggere lo stato
     * @return snapshot serializzabile della partita
     */
    public static GameState extractState(GameEngine engine) {
        GameState state = new GameState();

        // Player
        state.setPlayerName(engine.getPlayer().getName());
        state.setHealth(engine.getPlayer().getHealth());
        state.setSanity(engine.getPlayer().getSanity());
        state.setDaysOnIsland(engine.getPlayer().getDaysOnIsland());
        state.setCurrentRoomKey(engine.getCurrentRoomKey());

        // Inventario
        List<ItemData> inv = new ArrayList<>();
        for (Item item : engine.getPlayer().getInventory()) {
            inv.add(ItemData.fromItem(item));
        }
        state.setInventory(inv);

        // Narrativa
        state.setCurrentChapter(engine.getCurrentChapter());
        state.setCurrentChapterCompleted(engine.isCurrentChapterCompleted());
        state.setCurrentChapterStarted(engine.isCurrentChapterStarted());
        state.setGameRunning(engine.isGameRunning());
        state.setGameWon(engine.isGameWon());

        // Flag eventi
        state.setBlackRockExplored(engine.isBlackRockExplored());
        state.setRadioBatteryInstalled(engine.isRadioBatteryInstalled());
        state.setRadioAntennaInstalled(engine.isRadioAntennaInstalled());
        state.setRadioFuseInstalled(engine.isRadioFuseInstalled());
        state.setRadioRepaired(engine.isRadioRepaired());
        state.setRadioMessageReceived(engine.isRadioMessageReceived());
        state.setDynamiteActive(engine.isDynamiteActive());
        state.setDynamiteTimer(engine.getDynamiteTimer());

        // Oggetti nelle stanze
        Map<String, List<ItemData>> roomItems = new HashMap<>();
        for (Map.Entry<String, Room> entry : engine.getAllRooms().entrySet()) {
            List<ItemData> items = new ArrayList<>();
            for (Item item : entry.getValue().getItems()) {
                items.add(ItemData.fromItem(item));
            }
            if (!items.isEmpty()) {
                roomItems.put(entry.getKey(), items);
            }
        }
        state.setRoomItems(roomItems);

        return state;
    }

    /**
     * Serializza uno stato di gioco in JSON
     * @param state stato da serializzare
     * @return rappresentazione JSON dello stato
     */
    public static String toJson(GameState state) {
        return GSON.toJson(state);
    }

    /**
     * Deserializza un JSON in GameState
     * @param json stringa JSON da deserializzare
     * @return stato di gioco ricostruito
     */
    public static GameState fromJson(String json) {
        return GSON.fromJson(json, GameState.class);
    }
}
