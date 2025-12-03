# Lost Thesis 🏝️✈️

Un'avventura testuale grafica ispirata alla serie TV **LOST**, sviluppata in Java.

## 📖 Trama

Il volo **Oceanic 815** è precipitato su un'isola misteriosa. Sei uno dei sopravvissuti e devi affrontare i pericoli dell'isola: il **Mostro di Fumo**, **Gli Altri**, e i misteri della **DHARMA Initiative**.

La tua unica speranza di fuga? Trovare la **TESI** perduta che contiene le coordinate per un piccolo aereo nascosto sull'isola!

## 🎮 Come Giocare

1. **Compila il gioco:**
   ```bash
   chmod +x scripts/*.sh
   ./scripts/compile.sh
   ```

2. **Avvia il gioco:**
   ```bash
   ./scripts/run.sh
   ```

3. **Controlli:**
   - `A`, `B`, `C` - Scegli un'opzione
   - `AVANTI` / `SPAZIO` - Continua la storia
   - `ESC` - Esci dal gioco
   - `🎒` - Inventario
   - `❤️` - Stato salute

## 🌴 Caratteristiche

- 🏝️ **17 capitoli** di avventura sull'isola
- 🎨 **Grafica pixel art** generata proceduralmente
- 🧩 **Enigmi** basati sulla serie LOST
- 📍 **8 location** da esplorare
- 🔢 I misteriosi **numeri DHARMA**: 4, 8, 15, 16, 23, 42

## 📁 Struttura Progetto

```
LostThesis/
├── src/
│   └── com/lostthesis/
│       ├── Main.java          # Entry point
│       ├── engine/
│       │   ├── GameEngine.java
│       │   └── Level.java
│       ├── model/
│       │   ├── Player.java
│       │   ├── Item.java
│       │   └── Room.java
│       ├── gui/
│       │   └── FullScreenGUI.java
│       ├── graphics/
│       │   ├── FullScreenRenderer.java
│       │   └── PixelArtManager.java
│       └── audio/
│           └── AudioManager.java
├── scripts/
│   ├── compile.sh
│   └── run.sh
├── assets/
│   ├── images/
│   └── music/
└── README.md
```

## 🎓 Progetto Universitario

Questo gioco è stato sviluppato come progetto universitario, con il tema "la tesi come chiave per la salvezza".

## 🔧 Requisiti

- Java 21 LTS o superiore
- Sistema operativo: macOS, Linux, Windows

## 📜 Licenza

MIT License - Progetto a scopo educativo

---

*"See you in another life, brother!" - Desmond Hume*

**4 8 15 16 23 42**
