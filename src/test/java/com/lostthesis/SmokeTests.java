package com.lostthesis;

import com.lostthesis.engine.GameEngine;
import com.lostthesis.graphics.PixelArtManager;
import com.lostthesis.model.Item;
import com.lostthesis.save.GameConverter;
import com.lostthesis.save.GameState;

public class SmokeTests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("alias carica riconosciuto", SmokeTests::testCaricaAliasWorks);
        run("mappatura immagini capitoli", SmokeTests::testChapterImagesExist);
        run("timer dinamite", SmokeTests::testDynamiteTimerTriggersExplosion);
        run("save/load round-trip", SmokeTests::testSaveRoundTripPreservesState);

        System.out.println();
        System.out.println("Test superati: " + passed);
        System.out.println("Test falliti: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testCaricaAliasWorks() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Jack");

        String response = engine.processCommand("carica");
        assertContains(response, "salvat");
    }

    private static void testChapterImagesExist() {
        PixelArtManager manager = new PixelArtManager(800, 450);
        String[] chapterKeys = {
            "cap1_firstnight",
            "cap2_survivors",
            "cap3_smoke",
            "cap4_caves",
            "cap5_hunt",
            "cap6_hatch",
            "cap7_blackrock",
            "cap8_openhatch",
            "cap9_swan",
            "cap10_henrygale",
            "cap11_others",
            "cap11_escape_others",
            "cap12_raft",
            "cap13_walt",
            "cap13_flashback",
            "cap14_thesis",
            "cap15_runway",
            "cap16_prep",
            "cap17_escape",
            "cap18_freedom"
        };

        for (String key : chapterKeys) {
            assertTrue(manager.hasImage(key), "immagine non trovata per " + key);
        }
    }

    private static void testDynamiteTimerTriggersExplosion() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Kate");
        engine.getPlayer().addItem(new Item(
            "Dinamite",
            "ATTENZIONE: Altamente instabile!",
            true,
            Item.ItemType.STRUMENTO,
            0,
            1
        ));

        String activation = engine.processCommand("attiva dinamite");
        assertContains(activation, "HAI INNESCATO LA DINAMITE");

        for (int i = 0; i < 3; i++) {
            String interim = engine.processCommand("guarda");
            assertFalse(interim.contains("BOOM"), "la dinamite e' esplosa troppo presto");
            assertTrue(engine.isGameRunning(), "il gioco non dovrebbe essere finito ancora");
        }

        String boom = engine.processCommand("guarda");
        assertContains(boom, "BOOM");
        assertFalse(engine.isGameRunning(), "il gioco dovrebbe terminare dopo l'esplosione");
    }

    private static void testSaveRoundTripPreservesState() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Sawyer");
        engine.forceStartFirstChapter();
        engine.processCommand("A");
        engine.getPlayer().removeHealth(15);
        engine.getPlayer().addItem(new Item(
            "Bussola",
            "Una vecchia bussola.",
            true,
            Item.ItemType.STRUMENTO,
            0,
            -1
        ));

        GameState serialized = GameConverter.fromJson(
            GameConverter.toJson(GameConverter.extractState(engine))
        );

        GameEngine restored = new GameEngine();
        restored.loadGameState(serialized);

        assertEquals("Sawyer", restored.getPlayer().getName(), "nome giocatore");
        assertEquals(85, restored.getPlayer().getHealth(), "salute");
        assertEquals(2, restored.getCurrentChapterNumber(), "numero capitolo");
        assertEquals("I Sopravvissuti", restored.getCurrentChapterTitle(), "titolo capitolo");
        assertTrue(restored.getPlayer().hasItem("Bussola"), "oggetto inventario mancante");
    }

    private static void run(String name, CheckedTest test) {
        try {
            test.run();
            passed++;
            System.out.println("[OK] " + name);
        } catch (AssertionError | RuntimeException e) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void assertContains(String text, String expected) {
        if (text == null || !text.contains(expected)) {
            throw new AssertionError("atteso frammento: " + expected + " | ricevuto: " + text);
        }
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            throw new AssertionError(label + " atteso=" + expected + " attuale=" + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean value, String message) {
        if (value) {
            throw new AssertionError(message);
        }
    }

    @FunctionalInterface
    private interface CheckedTest {
        void run() throws Exception;
    }
}
