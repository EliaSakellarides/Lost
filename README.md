# Lost

Avventura testuale grafica in Java ispirata alla serie TV LOST.
Il giocatore interpreta un sopravvissuto del volo Oceanic 815, precipitato su un'isola misteriosa.
L'obiettivo e' recuperare la mappa DHARMA con le coordinate della pista nascosta e fuggire dall'isola.

## Trama

Il volo Oceanic 815 si spezza in volo e precipita su un'isola del Pacifico.
Tra relitti fumanti, giungla ostile, il Mostro di Fumo e il gruppo noto come Gli Altri,
il protagonista deve sopravvivere, esplorare le stazioni DHARMA e risolvere enigmi
per trovare la mappa con le coordinate della pista di atterraggio nascosta,
unica chiave per raggiungere un piccolo aereo e lasciare l'isola.

## Caratteristiche

- 20 capitoli narrativi con scelte semplici e risposte libere
- 1 minigioco integrato nella storia: tracciamento nella giungla
- Enigmi semplici basati su oggetti, come la riparazione della radio rotta
- 8 locazioni esplorabili: Spiaggia dello Schianto, Giungla Oscura, La Botola (Il Cigno), Villaggio degli Altri, Il Tempio, La Roccia Nera, Il Faro, Pista di Atterraggio
- Parser comandi con alias multilingua (italiano e inglese) e abbreviazioni rapide
- Sistema di salvataggio/caricamento su file JSON (slot multipli in `~/.lost/saves/`)
- Classifica dei migliori tempi salvata su database H2 locale
- API REST locale per consultare e inserire record (`http://localhost:8000/records`)
- Testo colorato con HTML via JTextPane (dialoghi, parole chiave, emoji tematiche)
- Gestione immagini di scena e placeholder tramite `PixelArtManager`
- Immagini di scena per ogni capitolo
- Colonna sonora (tema LOST in formato WAV)
- Interfaccia grafica fullscreen con sequenza introduttiva animata

## Requisiti

- Java 21 o superiore
- Maven 3.9+ oppure un JAR locale di Gson

## Compilazione e avvio

### Con javac (senza Maven)

```bash
export GSON_JAR="/percorso/a/gson-2.11.0.jar"
./scripts/compile.sh
./scripts/test.sh
./scripts/run.sh
```

Gli script compilano solo `src/main/java`, copiano automaticamente `src/main/resources` in `bin/`
e provano anche a riutilizzare Gson dalla cache locale di Maven (`~/.m2`) se presente.
Su macOS `./scripts/run.sh` aggiunge automaticamente `-XstartOnFirstThread`, necessario per l'avvio corretto della GUI.
`./scripts/test.sh` esegue uno smoke test del motore di gioco, dei timer, del save/load,
della mappatura immagini e della classifica H2.

### Con Maven (se disponibile)

```bash
mvn package
java -jar target/lost-1.0-jar-with-dependencies.jar
```

## Record e API REST

All'avvio il gioco prova ad aprire una piccola API locale sulla porta `8000`.
Se la porta e' libera, puoi consultare i record dal browser:

```text
http://localhost:8000/records
http://localhost:8000/records/best
```

I record vengono salvati in un database H2 locale sotto `~/.lost/`.
Ogni record contiene nome giocatore, tempo di completamento e data.

Se vuoi eseguire solo gli smoke test senza GUI:

```bash
./scripts/test.sh
```

## Controlli

| Comando | Alias | Descrizione |
|---------|-------|-------------|
| avanti | continua, prosegui, avanza, vai, next, n | Prosegue nella storia |
| prendi | raccogli, afferra, piglia, take, grab, pick, p | Raccoglie un oggetto |
| lascia | posa, metti, drop, abbandona, l | Lascia un oggetto |
| guarda | osserva, esamina, ispeziona, look, examine, x, g | Esamina oggetto o ambiente |
| usa | utilizza, adopera, impiega, use, u | Usa un oggetto |
| mangia | bevi, divora, sgranocchia, eat, drink | Consuma cibo o bevande |
| attiva | accendi, carica, innesca, activate, light | Attiva un oggetto |
| inventario | zaino, borsa, tasca, oggetti, inventory, inv, i | Mostra l'inventario |
| stato | status, salute, vita, health, hp, st | Mostra la salute |
| salva | save, salvataggio | Salva la partita |
| load | carica, caricapartita, carica_partita, ricarica | Carica una partita salvata |
| aiuto | help, comandi, h, ? | Mostra i comandi disponibili |

Abbreviazioni rapide: `p` prendi, `g` guarda, `l` lascia, `i` inventario, `u` usa, `h` aiuto, `n` avanti.

## Struttura progetto

```
Lost/
├── pom.xml
├── scripts/
│   ├── compile.sh
│   ├── run.sh
│   └── test.sh
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lost/
│   │   │       ├── Main.java
│   │   │       ├── engine/
│   │   │       ├── model/
│   │   │       ├── gui/
│   │   │       ├── graphics/
│   │   │       ├── audio/
│   │   │       ├── minigames/
│   │   │       └── save/
│   │   └── resources/
│   │       ├── images/
│   │       └── music/
│   └── test/
│       └── java/
│           └── com/lost/
│               └── SmokeTests.java
└── README.md
```

29 classi Java runtime, 7 package, piu uno smoke test dedicato.

Nota: la build usa `src/main/java` e `src/main/resources`.

## Architettura

```
┌─────────────────────────────────────────────────────┐
│                      Main                           │
│              (entry point, bootstrap)               │
└──────────────────────┬──────────────────────────────┘
                       │
          ┌────────────┴────────────┐
          v                         v
┌──────────────────┐     ┌──────────────────┐
│   FullScreenGUI  │     │   GameEngine     │
│                  │◄───►│                  │
│  GuiButtonFactory│     │  CommandParser   │
│  StatusPanel     │     │  Level           │
│  SceneBuilder    │     │  CommandType     │
│  IntroSequence   │     └──────┬───────────┘
└──────┬───────────┘            │
       │                 ┌──────┴───────┐
       v                 v              v
┌──────────────┐  ┌──────────┐  ┌──────────────┐
│   graphics/  │  │  model/  │  │  minigames/  │
│              │  │          │  │              │
│ FullScreen   │  │ Player   │  │ MiniGame     │
│  Renderer    │  │ Item     │  │ Tracking     │
│ PixelArt     │  │ Room     │  │              │
│  Manager     │  │          │  │              │
│ TextColorizer│  │          │  │              │
└──────────────┘  └──────────┘  └──────────────┘
       ┌────────────────┐  ┌────────────────┐
       │    audio/      │  │     save/      │
       │                │  │                │
       │ AudioManager   │  │ GameSave       │
       │ (.wav playback)│  │ GameState      │
       └────────────────┘  │ GameConverter  │
                           │ GameSaveInst.  │
                           │ ItemData       │
                           └────────────────┘
```

Flusso di gioco: `Main` avvia `FullScreenGUI` che crea il `GameEngine`.
Il motore gestisce la progressione tra i 20 `Level`, delegando input al `CommandParser`
e attivando il minigioco di tracciamento quando previsto.
Il `FullScreenRenderer` e il `TextColorizer` si occupano della resa grafica.
Il package `save` serializza/deserializza lo stato di gioco in JSON tramite Gson.

## Licenza

MIT License

---

*"See you in another life, brother." -- Desmond Hume*

4 8 15 16 23 42
