package com.lost;

import com.lost.engine.GameEngine;
import com.lost.graphics.PixelArtManager;
import com.lost.model.Item;
import com.lost.records.GameRecord;
import com.lost.records.RecordRepository;
import com.lost.records.RecordService;
import com.lost.save.GameConverter;
import com.lost.save.GameSave;
import com.lost.save.GameState;

import java.util.List;

public class SmokeTests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("alias carica riconosciuto", SmokeTests::testCaricaAliasWorks);
        run("immagine spiaggia", SmokeTests::testBeachImageExists);
        run("mappatura immagini capitoli", SmokeTests::testChapterImagesExist);
        run("timer dinamite", SmokeTests::testDynamiteTimerTriggersExplosion);
        run("enigma radio riparabile", SmokeTests::testRadioRepairPuzzle);
        run("scelte multiple esatte", SmokeTests::testMultipleChoiceRequiresExactOption);
        run("salto minigioco avanza", SmokeTests::testMiniGameSkipAdvancesChapter);
        run("la scoperta accetta prendi", SmokeTests::testLaScopertaAcceptsBarePrendi);
        run("tesi ottenuta solo alla scoperta", SmokeTests::testTesiAwardedOnlyAfterLaScoperta);
        run("giorno narrativo nello status", SmokeTests::testStatusDayTracksNarrativeDay);
        run("slot salvataggio sanitizzato", SmokeTests::testSaveSlotSanitization);
        run("record H2 migliori tempi", SmokeTests::testRecordServiceStoresBestTimes);
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

    private static void testBeachImageExists() {
        PixelArtManager manager = new PixelArtManager(800, 450);
        assertTrue(manager.hasImage("spiaggia"), "immagine non trovata per spiaggia");
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

    private static void testRadioRepairPuzzle() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Sayid");

        engine.getPlayer().setCurrentRoom(engine.getAllRooms().get("giungla"));
        assertContains(engine.processCommand("prendi radio"), "Radio danneggiata");

        engine.getPlayer().setCurrentRoom(engine.getAllRooms().get("spiaggia"));
        assertContains(engine.processCommand("prendi cavo"), "Cavo antenna");

        engine.getPlayer().setCurrentRoom(engine.getAllRooms().get("botola"));
        assertContains(engine.processCommand("prendi batteria"), "Batteria DHARMA");
        assertContains(engine.processCommand("prendi fusibile"), "Fusibile");

        assertContains(engine.processCommand("usa batteria con radio"), "Alimentazione: OK");
        assertContains(engine.processCommand("usa cavo con radio"), "Antenna: OK");
        String repaired = engine.processCommand("usa fusibile con radio");
        assertContains(repaired, "Trasmettitore riparato");
        assertTrue(engine.isRadioRepaired(), "la radio dovrebbe risultare riparata");
        assertTrue(engine.getPlayer().hasItem("trasmettitore"), "trasmettitore mancante");

        String message = engine.processCommand("usa trasmettitore");
        assertContains(message, "Hydra");
        assertTrue(engine.isRadioMessageReceived(), "messaggio radio non registrato");

        GameState serialized = GameConverter.fromJson(
            GameConverter.toJson(GameConverter.extractState(engine))
        );
        GameEngine restored = new GameEngine();
        restored.loadGameState(serialized);
        assertTrue(restored.isRadioRepaired(), "stato radio riparata non salvato");
        assertTrue(restored.isRadioMessageReceived(), "messaggio radio non salvato");
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

    private static void testMultipleChoiceRequiresExactOption() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Hurley");
        engine.forceStartFirstChapter();

        String response = engine.processCommand("banana");

        assertContains(response, "Risposta sbagliata");
        assertEquals(1, engine.getCurrentChapterNumber(), "capitolo dopo risposta ambigua");
        assertEquals("La Prima Notte", engine.getCurrentChapterTitle(), "titolo dopo risposta ambigua");
    }

    private static void testMiniGameSkipAdvancesChapter() {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame("Locke");
        engine.forceStartFirstChapter();

        answerAndContinue(engine, "A");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "B");
        answerAndContinue(engine, "C");

        String miniGameStart = engine.processCommand("A");
        assertContains(miniGameStart, "MINI GIOCO");
        assertTrue(engine.hasMiniGameActive(), "il minigioco dovrebbe essere attivo");

        String skipped = engine.processCommand("salta");
        assertContains(skipped, "saltato");
        assertFalse(engine.hasMiniGameActive(), "il minigioco dovrebbe essere chiuso");
        assertEquals(6, engine.getCurrentChapterNumber(), "capitolo dopo salto minigioco");
        assertEquals("La Botola", engine.getCurrentChapterTitle(), "titolo dopo salto minigioco");
    }

    private static void testLaScopertaAcceptsBarePrendi() {
        GameEngine engine = newStartedEngine("Desmond");
        advanceToLaScoperta(engine);

        assertEquals("La Scoperta", engine.getCurrentChapterTitle(), "capitolo prima di prendi");

        String response = engine.processCommand("prendi");

        assertContains(response, "CORRETTO");
        assertContains(response, "Hai recuperato la TESI");
        assertTrue(engine.getPlayer().hasItem("TESI"), "TESI mancante dopo La Scoperta");
        assertEquals("La Pista Nascosta", engine.getCurrentChapterTitle(), "capitolo dopo La Scoperta");
    }

    private static void testTesiAwardedOnlyAfterLaScoperta() {
        GameEngine engine = newStartedEngine("Charlie");
        advanceToFlashback(engine);

        assertEquals("Flashback", engine.getCurrentChapterTitle(), "capitolo flashback");
        assertFalse(engine.getPlayer().hasItem("TESI"), "TESI ottenuta troppo presto");

        answerAndContinue(engine, "A");
        assertEquals("La Scoperta", engine.getCurrentChapterTitle(), "capitolo scoperta");
        assertFalse(engine.getPlayer().hasItem("TESI"), "TESI ottenuta prima di prenderla");

        assertContains(engine.processCommand("prendi"), "Hai recuperato la TESI");
        assertTrue(engine.getPlayer().hasItem("TESI"), "TESI non ottenuta alla scoperta");
    }

    private static void testStatusDayTracksNarrativeDay() {
        GameEngine engine = newStartedEngine("Sun");

        answerAndContinue(engine, "A");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "B");
        assertEquals("Le Grotte", engine.getCurrentChapterTitle(), "capitolo giorno 3");
        assertEquals(3, engine.getPlayer().getDaysOnIsland(), "giorno grotte");
        assertContains(engine.processCommand("stato"), "Giorno 3");

        answerAndContinue(engine, "C");
        assertEquals("La Caccia", engine.getCurrentChapterTitle(), "capitolo giorno 5");
        assertEquals(5, engine.getPlayer().getDaysOnIsland(), "giorno caccia");
        assertContains(engine.processCommand("stato"), "Giorno 5");

        assertContains(engine.processCommand("A"), "MINI GIOCO");
        assertContains(engine.processCommand("salta"), "saltato");
        engine.processCommand("avanti");
        assertEquals("La Botola", engine.getCurrentChapterTitle(), "capitolo giorno 8");
        assertEquals(8, engine.getPlayer().getDaysOnIsland(), "giorno botola");
        assertContains(engine.processCommand("stato"), "Giorno 8");
    }

    private static void testSaveSlotSanitization() {
        assertEquals("fake_h2", GameSave.sanitizeSlotName("../../fake h2"), "slot traversal");
        assertEquals("", GameSave.sanitizeSlotName(""), "slot vuoto");
    }

    private static void testRecordServiceStoresBestTimes() {
        String dbName = "lost_smoke_" + System.nanoTime();
        RecordService service = new RecordService(
            new RecordRepository("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1")
        );

        service.saveCompletion("Jack", 120_000);
        service.saveCompletion("Kate", 80_000);
        service.saveCompletion("Hurley", 150_000);

        List<GameRecord> records = service.getBestRecords(2);

        assertEquals(2, records.size(), "numero record");
        assertEquals("Kate", records.get(0).getPlayerName(), "primo record");
        assertEquals("Jack", records.get(1).getPlayerName(), "secondo record");
        assertEquals("01:20", records.get(0).getFormattedTime(), "tempo formattato");
    }

    private static void answerAndContinue(GameEngine engine, String answer) {
        String response = engine.processCommand(answer);
        assertContains(response, "CORRETTO");
        engine.processCommand("avanti");
    }

    private static GameEngine newStartedEngine(String playerName) {
        GameEngine engine = new GameEngine();
        engine.getAudioManager().toggleMusic();
        engine.initializeGame(playerName);
        engine.forceStartFirstChapter();
        return engine;
    }

    private static void advanceToFlashback(GameEngine engine) {
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "B");
        answerAndContinue(engine, "C");

        assertContains(engine.processCommand("A"), "MINI GIOCO");
        assertContains(engine.processCommand("salta"), "saltato");
        engine.processCommand("avanti");

        answerAndContinue(engine, "C");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "B");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "B");
        answerAndContinue(engine, "C");
        answerAndContinue(engine, "B");
        answerAndContinue(engine, "A");
        answerAndContinue(engine, "nuotare");
    }

    private static void advanceToLaScoperta(GameEngine engine) {
        advanceToFlashback(engine);
        answerAndContinue(engine, "A");
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
