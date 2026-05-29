#!/bin/bash
# Script di compilazione per Lost

echo "═══════════════════════════════════════════════════"
echo "  LOST - Compilazione"
echo "═══════════════════════════════════════════════════"

# Directory di lavoro
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SRC_DIR="$PROJECT_DIR/src/main/java"
RES_DIR="$PROJECT_DIR/src/main/resources"
BIN_DIR="$PROJECT_DIR/bin"

resolve_gson_jar() {
    if [ -n "${GSON_JAR:-}" ] && [ -f "$GSON_JAR" ]; then
        printf '%s\n' "$GSON_JAR"
        return 0
    fi

    local cached_jar
    cached_jar="$(find "$HOME/.m2/repository/com/google/code/gson/gson" -name 'gson-*.jar' 2>/dev/null | sort | tail -n 1)"
    if [ -n "$cached_jar" ] && [ -f "$cached_jar" ]; then
        printf '%s\n' "$cached_jar"
        return 0
    fi

    return 1
}

resolve_h2_jar() {
    if [ -n "${H2_JAR:-}" ] && [ -f "$H2_JAR" ]; then
        printf '%s\n' "$H2_JAR"
        return 0
    fi

    local cached_jar
    cached_jar="$(find "$HOME/.m2/repository/com/h2database/h2" -name 'h2-*.jar' 2>/dev/null | sort | tail -n 1)"
    if [ -n "$cached_jar" ] && [ -f "$cached_jar" ]; then
        printf '%s\n' "$cached_jar"
        return 0
    fi

    return 1
}

# Crea cartella bin se non esiste
mkdir -p "$BIN_DIR"

# Pulisci precedenti compilazioni
rm -rf "$BIN_DIR"/*

echo "Compilazione da: $SRC_DIR"
echo "Output in: $BIN_DIR"
echo ""

if [ ! -d "$SRC_DIR" ]; then
    echo "ERRORE: Directory sorgenti non trovata: $SRC_DIR"
    exit 1
fi

GSON_JAR_PATH="$(resolve_gson_jar || true)"
if [ -z "$GSON_JAR_PATH" ]; then
    echo "ERRORE: Dipendenza Gson non trovata."
    echo "   Imposta GSON_JAR=/percorso/a/gson.jar oppure esegui prima 'mvn package' su una macchina con Maven."
    exit 1
fi
H2_JAR_PATH="$(resolve_h2_jar || true)"
if [ -z "$H2_JAR_PATH" ]; then
    echo "ERRORE: Dipendenza H2 non trovata."
    echo "   Imposta H2_JAR=/percorso/a/h2.jar oppure esegui prima 'mvn package' su una macchina con Maven."
    exit 1
fi

echo "Gson: $GSON_JAR_PATH"
echo "H2: $H2_JAR_PATH"
echo ""

# Trova tutti i file Java
echo "Ricerca file Java..."
JAVA_FILES=$(find "$SRC_DIR" -name "*.java")
FILE_COUNT=$(echo "$JAVA_FILES" | wc -l | tr -d ' ')
echo "   Trovati $FILE_COUNT file Java"
echo ""

# Compila
echo "Compilazione in corso..."
if javac -cp "$GSON_JAR_PATH:$H2_JAR_PATH" -d "$BIN_DIR" -sourcepath "$SRC_DIR" $JAVA_FILES 2>&1; then
    if [ -d "$RES_DIR" ]; then
        cp -R "$RES_DIR"/. "$BIN_DIR"/
    fi
    echo ""
    echo "Compilazione completata con successo!"
    echo ""
    echo "Per avviare il gioco, esegui:"
    echo "   ./scripts/run.sh"
else
    echo ""
    echo "ERRORE: Errore durante la compilazione!"
    exit 1
fi

echo "═══════════════════════════════════════════════════"
