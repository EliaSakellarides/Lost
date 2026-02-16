# Lost Thesis

Avventura testuale grafica in Java ispirata alla serie TV LOST.
Il giocatore interpreta un sopravvissuto del volo Oceanic 815, precipitato su un'isola misteriosa.
L'obiettivo e' recuperare una tesi perduta che contiene le coordinate per fuggire dall'isola.

## Trama

Il volo Oceanic 815 si spezza in volo e precipita su un'isola del Pacifico.
Tra relitti fumanti, giungla ostile, il Mostro di Fumo e il gruppo noto come Gli Altri,
il protagonista deve sopravvivere, esplorare le stazioni DHARMA e risolvere enigmi
per trovare la tesi perduta, unica chiave per raggiungere un piccolo aereo nascosto
sulla pista di atterraggio e lasciare l'isola.

## Caratteristiche

- 18 capitoli narrativi con scelte multiple e risposte libere
- 5 minigame integrati nella storia (fuga dal Mostro di Fumo, tracciamento nella giungla, disinnesco dinamite, sintonizzazione frequenze radio, decodifica codice Morse)
- 8 locazioni esplorabili: Spiaggia dello Schianto, Giungla Oscura, La Botola (Il Cigno), Villaggio degli Altri, Il Tempio, La Roccia Nera, Il Faro, Pista di Atterraggio
- Parser comandi con alias multilingua (italiano e inglese) e abbreviazioni rapide
- Sistema di salvataggio/caricamento su file JSON (slot multipli in `~/.lostthesis/saves/`)
- Testo colorato con HTML via JTextPane (dialoghi, parole chiave, emoji tematiche)
- Pixel art procedurale generata tramite PixelArtManager
- Immagini di scena per ogni capitolo
- Colonna sonora (tema LOST in formato WAV)
- Interfaccia grafica fullscreen con sequenza introduttiva animata

## Requisiti

- Java 21 o superiore
- Libreria Gson (inclusa come dipendenza o come JAR esterno)

## Compilazione e avvio

### Con javac (senza Maven)

```bash
JAVA_HOME="C:/Users/elia2/Downloads/java-21-openjdk-21.0.4.0.7-1.win.jdk.x86_64 (1)/java-21-openjdk-21.0.4.0.7-1.win.jdk.x86_64"
JAVAC="$JAVA_HOME/bin/javac"
JAVA="$JAVA_HOME/bin/java"
GSON_JAR="path/to/gson-2.9.1.jar"
SRC="src/main/java"
RES="src/main/resources"

mkdir -p /tmp/lostbuild
$JAVAC -cp "$GSON_JAR" -d /tmp/lostbuild -sourcepath "$SRC" $(find "$SRC" -name "*.java")
cp -r $RES/* /tmp/lostbuild/
cd /tmp/lostbuild && $JAVA -cp ".;$GSON_JAR" com.lostthesis.Main
```

### Con Maven (se disponibile)

```bash
mvn package
java -jar target/lost-thesis-1.0.jar
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
| load | caricapartita, carica_partita, ricarica | Carica una partita salvata |
| aiuto | help, comandi, h, ? | Mostra i comandi disponibili |

Abbreviazioni rapide: `p` prendi, `g` guarda, `l` lascia, `i` inventario, `u` usa, `h` aiuto, `n` avanti.

## Struttura progetto

```
Lost/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/lostthesis/
│       │       ├── Main.java
│       │       ├── engine/
│       │       │   ├── GameEngine.java
│       │       │   ├── Level.java
│       │       │   ├── CommandParser.java
│       │       │   └── CommandType.java
│       │       ├── model/
│       │       │   ├── Player.java
│       │       │   ├── Item.java
│       │       │   └── Room.java
│       │       ├── gui/
│       │       │   ├── FullScreenGUI.java
│       │       │   ├── GuiButtonFactory.java
│       │       │   ├── StatusPanelFactory.java
│       │       │   ├── SceneBuilder.java
│       │       │   └── IntroSequence.java
│       │       ├── graphics/
│       │       │   ├── FullScreenRenderer.java
│       │       │   ├── PixelArtManager.java
│       │       │   └── TextColorizer.java
│       │       ├── audio/
│       │       │   └── AudioManager.java
│       │       ├── minigames/
│       │       │   ├── MiniGame.java
│       │       │   ├── MiniGameState.java
│       │       │   ├── SmokeMonsterChase.java
│       │       │   ├── JungleTrackingGame.java
│       │       │   ├── DynamiteDefusalGame.java
│       │       │   ├── FrequencyTuningGame.java
│       │       │   └── MorseCodeGame.java
│       │       └── save/
│       │           ├── GameSave.java
│       │           ├── GameSaveInstance.java
│       │           ├── GameState.java
│       │           ├── GameConverter.java
│       │           └── ItemData.java
│       └── resources/
│           ├── images/            # 36 immagini di scena (.jpg, .png)
│           └── music/             # Colonna sonora (.wav)
└── README.md
```

29 classi Java, 7 package.

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
│  Renderer    │  │ Item     │  │ SmokMonster  │
│ PixelArt     │  │ Room     │  │ Jungle       │
│  Manager     │  │          │  │ Dynamite     │
│ TextColorizer│  │          │  │ Frequency    │
└──────────────┘  └──────────┘  │ MorseCode    │
                                └──────────────┘
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
Il motore gestisce la progressione tra i 18 `Level`, delegando input al `CommandParser`
e attivando i `MiniGame` nei capitoli corrispondenti.
Il `FullScreenRenderer` e il `TextColorizer` si occupano della resa grafica.
Il package `save` serializza/deserializza lo stato di gioco in JSON tramite Gson.

## Licenza

MIT License

---

*"See you in another life, brother." -- Desmond Hume*

4 8 15 16 23 42
